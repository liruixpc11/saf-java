package lab.cadl.analysis.behavior.engine.model.state;

import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class StateRef {

    private StateDesc ref;
    private Map<String, StateArgument> arguments = new HashMap<>();

    public StateRef(StateDesc ref) {
        this.ref = ref;
    }

    public StateDesc getRef() {
        return ref;
    }

    public Map<String, StateArgument> getArguments() {
        return arguments;
    }

    public StateRef put(String name, RelativeOp op, Value value) {
        arguments.put(name, new StateArgument(name, op, value));
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StateRef{");
        sb.append("ref=").append(ref.getId());
        sb.append(", arguments=").append(arguments.values());
        sb.append('}');
        return sb.toString();
    }
}
