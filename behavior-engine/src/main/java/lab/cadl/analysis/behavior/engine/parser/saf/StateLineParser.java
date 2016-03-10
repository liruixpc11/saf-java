package lab.cadl.analysis.behavior.engine.parser.saf;

import lab.cadl.analysis.behavior.engine.model.*;
import lab.cadl.analysis.behavior.engine.model.attribute.*;
import lab.cadl.analysis.behavior.engine.model.constraint.StateConstraint;
import lab.cadl.analysis.behavior.engine.model.constraint.StateConstraintType;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateRef;
import lab.cadl.analysis.behavior.grammar.BehaviorBaseListener;
import lab.cadl.analysis.behavior.grammar.BehaviorLexer;
import lab.cadl.analysis.behavior.grammar.BehaviorParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class StateLineParser {
    private static final Logger logger = LoggerFactory.getLogger(StateLineParser.class);

    private static class Listener extends BehaviorBaseListener {
        private SymbolTable symbolTable;
        private StateDesc desc;

        public Listener(SymbolTable symbolTable, StateDesc desc) {
            this.symbolTable = symbolTable;
            this.desc = desc;
        }

        @Override
        public void enterStateConstraint(BehaviorParser.StateConstraintContext ctx) {
            StateConstraintType type = StateConstraintType.parse(ctx.stateConstraintType().getText());
            RelativeOp op = RelativeOp.parse(ctx.RELATIVE_OP().getText());
            Value value;
            if (ctx.INT() != null) {
                value = new PrimeValue<>(Integer.valueOf(ctx.INT().getText()));
            } else if (ctx.RANGE() != null) {
                String[] values = ctx.RANGE().getText().split(":");
                long min = Long.parseLong(values[0]);
                long max = Long.parseLong(values[1]);
                value = new RangeValue(min, max);
            } else {
                throw new IllegalArgumentException("不支持该类型约束值：" + ctx.getText());
            }

            desc.setConstraint(new StateConstraint(type, op, value));
        }

        @Override
        public void enterImportState(BehaviorParser.ImportStateContext ctx) {
            String namespace = ctx.importVariable().ID(0).getText();
            String name = ctx.importVariable().ID(1).getText();

            StateDesc refState = symbolTable.checkState(namespace, name);
            StateRef ref = new StateRef(refState);
            desc.setRef(ref);
        }

        @Override
        public void enterAttributeList(BehaviorParser.AttributeListContext ctx) {
            StateRef ref = desc.getRef();
            for (BehaviorParser.AttributePairContext pairContext : ctx.attributePair()) {
                String name = pairContext.ID().getText();
                RelativeOp op = RelativeOp.parse(pairContext.RELATIVE_OP().getText());
                Value value = parseValue(pairContext.value());

                if (ref != null) {
                    ref.put(name, op, value);
                } else {
                    desc.put(name, op, value);
                }
            }
        }

        private Value parseValue(BehaviorParser.ValueContext value) {
            if (value.ID() != null) {
                return new PrimeValue<>(value.ID().getText());
            } else if (value.INT() != null) {
                return new PrimeValue<>(Long.parseLong(value.INT().getText()));
            } else if (value.FLOAT() != null) {
                return new PrimeValue<>(Double.parseDouble(value.FLOAT().getText()));
            } else if (value.STRING() != null) {
                return new PrimeValue<>(StringUtils.strip(value.STRING().getText(), "\""));
            } else if (value.RANGE() != null) {
                String[] values = value.RANGE().getText().split(":");
                long min = Long.parseLong(values[0]);
                long max = Long.parseLong(values[1]);
                return new RangeValue(min, max);
            } else if (value.variable() != null) {
                String namespace = desc.getNameSpace();
                String name = value.variable().ID(0).getText();
                String attribute = value.variable().ID(1).getText();
                StateDesc desc = symbolTable.checkState(namespace, name);
                return new VariableValue(desc, attribute);
            } else if (value.argument() != null) {
                int position = Integer.parseInt(value.argument().INT().getText());
                return new ArgumentValue(position);
            } else {
                throw new IllegalArgumentException("未知类型：" + value.getText());
            }
        }
    }

    public StateDesc parse(QualifiedName qualifiedName, SymbolTable symbolTable, InputStream inputStream) throws IOException {
        logger.debug("parsing state {}", qualifiedName);
        BehaviorLexer lexer = new BehaviorLexer(new ANTLRInputStream(inputStream));
        BehaviorParser parser = new BehaviorParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new ErrorStrategy());

        StateDesc desc = new StateDesc(qualifiedName);
        Listener listener = new Listener(symbolTable, desc);
        new ParseTreeWalker().walk(listener, parser.constraintState());

        symbolTable.add(desc);
        return desc;
    }
}
