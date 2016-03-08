package lab.cadl.analysis.behavior.engine.model.constraint;

import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

/**
 *
 */
public class CountConstraint implements BehaviorConstraint {
    private CountConstraintType type;
    private RelativeOp relativeOp;
    private Value value;

    public CountConstraint(CountConstraintType type, RelativeOp relativeOp, Value value) {
        this.type = type;
        this.relativeOp = relativeOp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + type.getName() + relativeOp.getOp() + String.valueOf(value) + "]";
    }
}
