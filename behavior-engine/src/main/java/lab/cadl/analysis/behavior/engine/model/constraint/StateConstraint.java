package lab.cadl.analysis.behavior.engine.model.constraint;

import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

/**
 *
 */
public class StateConstraint {
    private StateConstraintType type;
    private RelativeOp op;
    private Value value;

    public StateConstraint(StateConstraintType type, RelativeOp op, Value value) {
        this.type = type;
        this.op = op;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(type.getName()) + String.valueOf(op.getOp()) + String.valueOf(value);
    }
}
