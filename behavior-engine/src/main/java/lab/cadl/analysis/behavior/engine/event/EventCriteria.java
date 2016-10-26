package lab.cadl.analysis.behavior.engine.event;

import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.attribute.IndependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

/**
 *
 */
public class EventCriteria {
    private String name;
    private RelativeOp op;
    private Value value;

    public EventCriteria(String name, RelativeOp op, Value value) {
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

    public boolean ok(StateInstance instance) {
        return false;
    }
}
