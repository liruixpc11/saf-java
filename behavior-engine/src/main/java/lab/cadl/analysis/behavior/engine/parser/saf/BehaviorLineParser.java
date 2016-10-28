package lab.cadl.analysis.behavior.engine.parser.saf;

import lab.cadl.analysis.behavior.engine.exception.EngineException;
import lab.cadl.analysis.behavior.engine.model.attribute.*;
import lab.cadl.analysis.behavior.engine.model.behavior.*;
import lab.cadl.analysis.behavior.engine.model.QualifiedName;
import lab.cadl.analysis.behavior.engine.model.SymbolTable;
import lab.cadl.analysis.behavior.engine.model.constraint.*;
import lab.cadl.analysis.behavior.engine.model.op.LogicalOp;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;
import lab.cadl.analysis.behavior.engine.model.op.TimeOp;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.grammar.BehaviorBaseVisitor;
import lab.cadl.analysis.behavior.grammar.BehaviorLexer;
import lab.cadl.analysis.behavior.grammar.BehaviorParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class BehaviorLineParser {
    private static final Logger logger = LoggerFactory.getLogger(BehaviorLineParser.class);

    private static class Visitor extends BehaviorBaseVisitor<BehaviorNode> {
        private final SymbolTable symbolTable;
        private final String namespace;
        private final BehaviorDesc behavior;

        Visitor(SymbolTable symbolTable, String namespace, BehaviorDesc behavior) {
            this.symbolTable = symbolTable;
            this.namespace = namespace;
            this.behavior = behavior;
        }

        @Override
        public BehaviorNode visitStateBehavior(BehaviorParser.StateBehaviorContext ctx) {
            logger.trace("state behavior {}", ctx.getText());

            String name = ctx.getText();
            return handleRef(name);
        }

        private BehaviorNode handleRef(String name) {
            StateDesc state = symbolTable.queryState(namespace, name);
            if (state == null) {
                BehaviorDesc behavior = symbolTable.queryBehavior(namespace, name);
                if (behavior == null) {
                    throw new EngineException("未找到名称为" + name + "的State或Behavior");
                } else {
                    // 这么做简化了遍历树结构的操作,但是不好处理递归
                    // return behavior.getRoot();

                    // 这种方式比较好地支持了递归操作,但是在遍历行为树的时候需要特殊处理一下
                    return new RefBehaviorNode(behavior, behavior == this.behavior);
                }
            } else {
                return new StateBehaviorNode(state);
            }
        }

        @Override
        public BehaviorNode visitConstraintBehavior(BehaviorParser.ConstraintBehaviorContext ctx) {
            logger.trace("constraint behavior {}", ctx.getText());

            BehaviorNode child = visit(ctx.behavior());

            if (ctx.behaviorConstraint() == null) {
                return child;
            } else {
                if (ctx.behaviorConstraint().countConstraint() != null) {
                    BehaviorParser.CountConstraintContext countCtx = ctx.behaviorConstraint().countConstraint();
                    CountConstraintType type = CountConstraintType.parse(countCtx.COUNT_TYPE().getText());
                    RelativeOp op = RelativeOp.parse(countCtx.RELATIVE_OP().getText());
                    if (countCtx.INT().size() == 1) {
                        long count = Long.parseLong(countCtx.INT(0).getText());
                        return new ConstraintBehaviorNode(child, new CountConstraint(type, op, new LongValue(count)));
                    } else {
                        long begin = Long.parseLong(countCtx.INT(0).getText());
                        long end = Long.parseLong(countCtx.INT(1).getText());
                        return new ConstraintBehaviorNode(child, new CountConstraint(type, op, new RangeValue(begin, end)));
                    }
                } else if (ctx.behaviorConstraint().timeConstraint() != null) {
                    BehaviorParser.TimeConstraintContext timeCtx = ctx.behaviorConstraint().timeConstraint();
                    TimeConstraintType type = TimeConstraintType.parse(timeCtx.TIME_POSITION().getText());
                    RelativeOp op = RelativeOp.parse(timeCtx.RELATIVE_OP().getText());
                    return new ConstraintBehaviorNode(child, new TimeConstraint(type, op, parseTime(timeCtx.TIME())));
                } else {
                    throw new EngineException("未知约束类型：" + ctx.behaviorConstraint().getText());
                }
            }
        }

        private TemporalValue parseTime(List<TerminalNode> contexts) {
            if (contexts.size() == 1) {
                return parseTime(contexts.get(0));
            } else {
                DurationValue begin = parseTime(contexts.get(0));
                DurationValue end = parseTime(contexts.get(1));
                return new DurationRangeValue(begin, end);
            }
        }

        private DurationValue parseTime(TerminalNode node) {
            Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z_]+)");
            Matcher matcher = pattern.matcher(node.getText());
            if (matcher.matches()) {
                long value = Long.parseLong(matcher.group(1));
                String unit = matcher.group(2).toLowerCase();
                if (unit.startsWith("m")) {
                    return DurationValue.fromMs(value);
                } else if (unit.startsWith("s")) {
                    return DurationValue.fromMs(value * 1000);
                } else if (unit.startsWith("u")) {
                    return DurationValue.fromUs(value);
                } else {
                    throw new EngineException("未知时间单位：" + unit);
                }
            } else {
                throw new IllegalArgumentException("非法时间格式: " + node.getText());
            }
        }

        @Override
        public BehaviorNode visitNegationBehavior(BehaviorParser.NegationBehaviorContext ctx) {
            logger.trace("negation behavior {}", ctx.getText());
            return new NegationBehaviorNode(visit(ctx.behavior()));
        }

        @Override
        public BehaviorNode visitLogicalBehavior(BehaviorParser.LogicalBehaviorContext ctx) {
            logger.trace("logical behavior {}", ctx.getText());

            BehaviorNode left = visit(ctx.behavior(0));
            BehaviorNode right = visit(ctx.behavior(1));
            LogicalOp op = LogicalOp.parse(ctx.LOGICAL_OP().getText());

            return new LogicalBehaviorNode(left, op, right);
        }

        @Override
        public BehaviorNode visitAlwaysBehavior(BehaviorParser.AlwaysBehaviorContext ctx) {
            logger.trace("always behavior {}", ctx.getText());
            return new AlwaysBehaviorNode(visit(ctx.behavior()));
        }

        @Override
        public BehaviorNode visitTimeBehavior(BehaviorParser.TimeBehaviorContext ctx) {
            logger.trace("time behavior {}", ctx.getText());
            BehaviorNode left = visit(ctx.behavior(0));
            BehaviorNode right = visit(ctx.behavior(1));
            TimeOp op = TimeOp.parse(ctx.constrainnTimeOp().TIME_OP().getText());

            BehaviorParser.OperationConstraintContext constraintCtx = ctx.constrainnTimeOp().operationConstraint();
            if (constraintCtx == null) {
                return new TimeBehaviorNode(left, op, right);
            } else {
                TimeOpConstraint constraint = new TimeOpConstraint(
                        RelativeOp.parse(constraintCtx.RELATIVE_OP().getText()),
                        parseTime(constraintCtx.TIME())
                );
                return new TimeBehaviorNode(left, op, right, constraint);
            }
        }
    }

    public BehaviorDesc parse(QualifiedName qualifiedName, SymbolTable symbolTable, InputStream inputStream) throws IOException {
        logger.debug("parsing behavior {}", qualifiedName);
        BehaviorLexer lexer = new BehaviorLexer(new ANTLRInputStream(inputStream));
        BehaviorParser parser = new BehaviorParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new ErrorStrategy());

        BehaviorDesc behavior = new BehaviorDesc(qualifiedName);
        // 在这里加入符号表以支持递归
        symbolTable.add(behavior);

        Visitor visitor = new Visitor(symbolTable, qualifiedName.getNameSpace(), behavior);
        BehaviorNode root = visitor.visit(parser.behavior());

        if (root == null) {
            logger.warn("behavior {} is empty", qualifiedName);
            return null;
        } else {
            behavior.setRoot(root);
            return behavior;
        }
    }
}
