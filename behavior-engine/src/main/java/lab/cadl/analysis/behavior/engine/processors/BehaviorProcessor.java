package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.exception.NotSupportException;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.BehaviorInstance;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.behavior.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class BehaviorProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BehaviorProcessor.class);

    private StateProcessor stateProcessor;
    private AnalysisInstanceRegistry instanceRegistry;

    public BehaviorProcessor(StateProcessor stateProcessor, AnalysisInstanceRegistry instanceRegistry) {
        this.stateProcessor = stateProcessor;
        this.instanceRegistry = instanceRegistry;
    }

    public List<AnalysisInstance> process(BehaviorDesc desc) {
        return desc.getRoot().accept(new Visitor(desc));
    }

    private class Visitor implements BehaviorNodeVisitor<List<AnalysisInstance>> {
        BehaviorDesc behavior;

        Visitor(BehaviorDesc behavior) {
            this.behavior = behavior;
        }

        @Override
        public List<AnalysisInstance> visit(AlwaysBehaviorNode node) {
            return node.accept(Visitor.this);
        }

        @Override
        public List<AnalysisInstance> visit(ConstraintBehaviorNode node) {
            List<AnalysisInstance> instances = node.getChild().accept(Visitor.this);
            return node.getBehaviorConstraint().apply(behavior, node, instances);
        }

        @Override
        public List<AnalysisInstance> visit(LogicalBehaviorNode node) {
            List<AnalysisInstance> leftList = node.getLeft().accept(Visitor.this);
            List<AnalysisInstance> rightList = node.getRight().accept(Visitor.this);
            switch (node.getOp()) {
                case Or:
                    if (!leftList.isEmpty() || !rightList.isEmpty()) {
                        return new BehaviorInstance(behavior, node).merge(leftList).merge(rightList).toList();
                    } else {
                        return AnalysisInstance.EMPTY;
                    }
                case And:
                    if (!leftList.isEmpty() && !rightList.isEmpty()) {
                        return new BehaviorInstance(behavior, node).merge(leftList).merge(rightList).toList();
                    } else {
                        return AnalysisInstance.EMPTY;
                    }
                case Xor:
                    if (!leftList.isEmpty() ^ !rightList.isEmpty()) {
                        return new BehaviorInstance(behavior, node).merge(leftList).merge(rightList).toList();
                    } else {
                        return AnalysisInstance.EMPTY;
                    }
                default:
                    throw new IllegalArgumentException(String.format("不支持操作符%s", node.getOp()));
            }
        }

        @Override
        public List<AnalysisInstance> visit(NegationBehaviorNode node) {
            List<AnalysisInstance> instances = node.getChild().accept(Visitor.this);
            if (instances.isEmpty()) {
                return new BehaviorInstance(behavior, node).toList();
            } else {
                return AnalysisInstance.EMPTY;
            }
        }

        @Override
        public List<AnalysisInstance> visit(RefBehaviorNode node) {
            if (node.getBehavior().containsRecursive()) {
                // cannot cache
                // when to stop?
//                return process(node.getBehavior());
                throw new NotSupportException("由于未解决无限递归问题,目前不支持递归查询");
            } else {
                List<AnalysisInstance> instances = instanceRegistry.query(node.getBehavior());
                if (instances != null) {
                    return instances;
                } else {
                    instances = process(node.getBehavior());
                    instanceRegistry.register(node.getBehavior(), instances);
                    return instances;
                }
            }
        }

        @Override
        public List<AnalysisInstance> visit(StateBehaviorNode node) {
            return stateProcessor.process(node.getState());
        }

        @Override
        public List<AnalysisInstance> visit(TimeBehaviorNode node) {
            List<AnalysisInstance> leftList = node.getLeft().accept(Visitor.this);
            List<AnalysisInstance> rightList = node.getRight().accept(Visitor.this);

            // optimized for empty
            if (leftList.isEmpty() || rightList.isEmpty()) {
                return AnalysisInstance.EMPTY;
            }

            switch (node.getTimeOp()) {
                case LeadTo:
                    return processTimeOp(leftList, rightList, node, (ls, le, rs, re) ->
                            le < rs && (node.getConstraint() == null || node.getConstraint().satisfy(le, rs))
                    );
                case StartWidth:
                    return processTimeOp(leftList, rightList, node, (ls, le, rs, re) ->
                            ls == rs && (node.getConstraint() == null || node.getConstraint().satisfy(ls, rs))
                    );
                case EndWidth:
                    return processTimeOp(leftList, rightList, node, (ls, le, rs, re) ->
                            le == re && (node.getConstraint() == null || node.getConstraint().satisfy(le, re))
                    );

                case Overlap:
                    return processTimeOp(leftList, rightList, node, (ls, le, rs, re) ->
                            rs < ls && ls < re && re < le && (node.getConstraint() == null || node.getConstraint().satisfy(ls, re))
                    );
                case Duration:
                    return processTimeOp(leftList, rightList, node, (ls, le, rs, re) ->
                            rs < ls && ls < re && re < le && (node.getConstraint() == null || node.getConstraint().satisfy(ls, re))
                    );
                case Equal:
                    return processTimeOp(leftList, rightList, node, (ls, le, rs, re) ->
                            re - rs == le - ls && (node.getConstraint() == null || node.getConstraint().satisfy(ls, le))
                    );
                default:
                    throw new IllegalArgumentException("不支持时序操作符" + node.getTimeOp());
            }
        }

        private List<AnalysisInstance> processTimeOp(List<AnalysisInstance> leftList,
                                                     List<AnalysisInstance> rightList,
                                                     TimeBehaviorNode node,
                                                     TimeRangePredict predictor) {
            boolean rightDependent = rightList.get(0).isDependent();

            List<AnalysisInstance> instances = new ArrayList<>();
            if (rightDependent) {
                for (AnalysisInstance right : rightList) {
                    List<StateInstance> dependee = ((StateInstance) right).dependeeList();

                    instances.addAll(dependee.stream()
                            .filter(left -> leftList.contains(left) &&
                                    predictor.test(left.startNanos(),
                                            left.endNanos(),
                                            right.startNanos(),
                                            right.endNanos()))
                            .map(left -> new BehaviorInstance(behavior, node, left, right))
                            .collect(Collectors.toList())
                    );
                }
            } else {
                // how?
                int rightBegin = 0;
                for (AnalysisInstance left : leftList) {
                    for (int j = rightBegin; j < rightList.size(); j++) {
                        AnalysisInstance right = rightList.get(j);
                        if (left.equals(right)) {
                            rightBegin = j + 1;
                            continue;
                        }

                        if (predictor.test(left.startNanos(),
                                left.endNanos(),
                                right.startNanos(),
                                right.endNanos())) {
                            instances.add(new BehaviorInstance(behavior, node, left, right));
                        }
                    }
                }
            }

            return instances;
        }
    }
}
