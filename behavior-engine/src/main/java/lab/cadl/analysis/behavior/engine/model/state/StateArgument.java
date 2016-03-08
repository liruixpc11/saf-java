package lab.cadl.analysis.behavior.engine.model.state;

import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

/**
 *
 */
public class StateArgument {
    String name;
    RelativeOp op;
    Value value;

    public StateArgument(String name, RelativeOp op, Value value) {
        this.name = name;
        this.op = op;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public RelativeOp getOp() {
        return op;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "" + op.getOp() + "" + value;
    }
}
