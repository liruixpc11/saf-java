package lab.cadl.analysis.behavior.engine.model.constraint;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.BehaviorInstance;
import lab.cadl.analysis.behavior.engine.model.attribute.IndependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.LongValue;
import lab.cadl.analysis.behavior.engine.model.attribute.RangeValue;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorNode;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class CountConstraint implements BehaviorConstraint {
    private CountConstraintType type;
    private RelativeOp relativeOp;
    private IndependentValue value;

    public CountConstraint(CountConstraintType type, RelativeOp relativeOp, IndependentValue value) {
        this.type = type;
        this.relativeOp = relativeOp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + type.getName() + relativeOp.getOp() + value + "]";
    }

    @Override
    public List<AnalysisInstance> apply(BehaviorDesc desc, BehaviorNode node, List<AnalysisInstance> instances) {
        if (value instanceof LongValue) {
            long count = ((LongValue) value).getValue();
            switch (type) {
                case BehaviorCount:
                    return instances.stream()
                            .filter(i -> relativeOp.test(i.size(), count))
                            .collect(Collectors.toList());
                case InstanceCount:
                    if (relativeOp.test(instances.size(), count)) {
                        return new BehaviorInstance(desc, node, instances).toList();
                    } else {
                        return AnalysisInstance.EMPTY;
                    }
                case Rate:
                    return instances.stream()
                            .filter(i -> relativeOp.test(i.rate(), count))
                            .collect(Collectors.toList());
                default:
                    throw new IllegalArgumentException("不支持该数量约束类型: " + type);
            }
        } else if (value instanceof RangeValue) {
            RangeValue range = (RangeValue) value;
            long min = range.getMin();
            long max = range.getMax();

            if (relativeOp == RelativeOp.Equal) {
                switch (type) {
                    case BehaviorCount:
                        return instances.stream()
                                .filter(i -> min <= i.size() && i.size() <= max)
                                .collect(Collectors.toList());
                    case InstanceCount:
                        if (min <= instances.size() && instances.size() <= max) {
                            return new BehaviorInstance(desc, node, instances).toList();
                        } else {
                            return AnalysisInstance.EMPTY;
                        }
                    case Rate:
                        return instances.stream()
                                .filter(i -> min <= i.rate() && i.rate() <= max)
                                .collect(Collectors.toList());
                    default:
                        throw new IllegalArgumentException("不支持该数量约束类型: " + type);
                }
            } else if (relativeOp == RelativeOp.NotEqual) {
                switch (type) {
                    case BehaviorCount:
                        return instances.stream()
                                .filter(i -> min >= i.size() || i.size() >= max)
                                .collect(Collectors.toList());
                    case InstanceCount:
                        if (min >= instances.size() || instances.size() >= max) {
                            return new BehaviorInstance(desc, node, instances).toList();
                        } else {
                            return AnalysisInstance.EMPTY;
                        }
                    case Rate:
                        return instances.stream()
                                .filter(i -> min >= i.rate() || i.rate() >= max)
                                .collect(Collectors.toList());
                    default:
                        throw new IllegalArgumentException("不支持该数量约束类型: " + type);
                }
            } else {
                throw new IllegalArgumentException(value.getClass().getSimpleName() + "数据不支持该操作符: " + relativeOp);
            }
        } else {
            throw new IllegalArgumentException("不支持该数据类型: " + value.getClass().getSimpleName());
        }
    }
}
