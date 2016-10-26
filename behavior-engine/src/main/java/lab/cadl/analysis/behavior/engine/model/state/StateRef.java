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
    private StateDesc context;

    public StateRef(StateDesc ref) {
        this.ref = ref;
    }

    public StateDesc getContext() {
        return context;
    }

    public StateDesc getRef() {
        return ref;
    }

    public void setContext(StateDesc context) {
        this.context = context;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StateRef{");
        sb.append("ref=").append(ref.getId());
        sb.append('}');
        return sb.toString();
    }
}
