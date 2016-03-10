package lab.cadl.analysis.behavior.engine.parser.saf;

import lab.cadl.analysis.behavior.engine.exception.EngineException;
import lab.cadl.analysis.behavior.engine.model.attribute.*;
import lab.cadl.analysis.behavior.engine.model.behavior.*;
import lab.cadl.analysis.behavior.engine.model.QualifiedName;
import lab.cadl.analysis.behavior.engine.model.SymbolTable;
import lab.cadl.analysis.behavior.engine.model.constraint.CountConstraint;
import lab.cadl.analysis.behavior.engine.model.constraint.CountConstraintType;
import lab.cadl.analysis.behavior.engine.model.constraint.TimeConstraint;
import lab.cadl.analysis.behavior.engine.model.constraint.TimeConstraintType;
import lab.cadl.analysis.behavior.engine.model.op.LogicalOp;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;
import lab.cadl.analysis.behavior.engine.model.op.TimeOp;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.grammar.BehaviorBaseVisitor;
import lab.cadl.analysis.behavior.grammar.BehaviorLexer;
import lab.cadl.analysis.behavior.grammar.BehaviorParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class BehaviorLineParser {
    private static final Logger logger = LoggerFactory.getLogger(BehaviorLineParser.class);

    private static class Visitor extends BehaviorBaseVisitor<BehaviorNode> {
        private final SymbolTable symbolTable;
        private final String namespace;

        public Visitor(SymbolTable symbolTable, String namespace) {
            this.symbolTable = symbolTable;
            this.namespace = namespace;
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
                    return behavior.getRoot();
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
                        return new ConstraintBehaviorNode(child, new CountConstraint(type, op, new PrimeValue<>(count)));
                    } else {
                        long begin = Long.parseLong(countCtx.INT(0).getText());
                        long end = Long.parseLong(countCtx.INT(1).getText());
                        return new ConstraintBehaviorNode(child, new CountConstraint(type, op, new RangeValue(begin, end)));
                    }
                } else if (ctx.behaviorConstraint().timeConstraint() != null) {
                    BehaviorParser.TimeConstraintContext timeCtx = ctx.behaviorConstraint().timeConstraint();
                    TimeConstraintType type = TimeConstraintType.parse(timeCtx.TIME_POSITION().getText());
                    RelativeOp op = RelativeOp.parse(timeCtx.RELATIVE_OP().getText());
                    if (timeCtx.time().size() == 1) {
                        DurationValue time = parseTime(timeCtx.time(0));
                        return new ConstraintBehaviorNode(child, new TimeConstraint(type, op, time));
                    } else {
                        DurationValue begin = parseTime(timeCtx.time(0));
                        DurationValue end = parseTime(timeCtx.time(1));
                        return new ConstraintBehaviorNode(child, new TimeConstraint(type, op, new DurationRangeValue(begin, end)));
                    }
                } else {
                    throw new EngineException("未知约束类型：" + ctx.behaviorConstraint().getText());
                }
            }
        }

        private DurationValue parseTime(BehaviorParser.TimeContext ctx) {
            long value = Long.parseLong(ctx.INT().getText());
            String unit = ctx.TIME_UNIT().getText().toLowerCase();
            if (unit.startsWith("m")) {
                return DurationValue.fromMs(value);
            } else if (unit.startsWith("s")) {
                return DurationValue.fromMs(value * 1000);
            } else {
                throw new EngineException("未知时间单位：" + unit);
            }
        }

        @Override
        public BehaviorNode visitNegationBehavior(BehaviorParser.NegationBehaviorContext ctx) {
            logger.trace("negation behavior {}", ctx.getText());
            return new NegationBehavior(visit(ctx.behavior()));
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
            return new TimeBehaviorNode(left, op, right);
        }
    }

    public BehaviorDesc parse(QualifiedName qualifiedName, SymbolTable symbolTable, InputStream inputStream) throws IOException {
        logger.debug("parsing behavior {}", qualifiedName);
        BehaviorLexer lexer = new BehaviorLexer(new ANTLRInputStream(inputStream));
        BehaviorParser parser = new BehaviorParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new ErrorStrategy());

        Visitor visitor = new Visitor(symbolTable, qualifiedName.getNameSpace());
        BehaviorNode root = visitor.visit(parser.behavior());

        if (root == null) {
            logger.warn("behavior {} is empty", qualifiedName);
            return null;
        } else {
            BehaviorDesc behavior = new BehaviorDesc(qualifiedName, root);
            symbolTable.add(behavior);
            return behavior;
        }
    }
}
