package lab.cadl.analysis.behavior.engine.model.state;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.constraint.StateConstraint;
import lab.cadl.analysis.behavior.engine.model.IdentifiedObject;
import lab.cadl.analysis.behavior.engine.model.QualifiedName;
import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class StateDesc extends IdentifiedObject implements AnalysisDesc {
    // id -> attribute
    private Map<String, StateArgument> attributes = new HashMap<>();
    private StateRef ref;
    private StateConstraint constraint;
    private boolean dependent;

    public StateDesc(QualifiedName qualifiedName) {
        super(qualifiedName);
    }

    public Map<String, StateArgument> getArguments() {
        return attributes;
    }

    public void put(String id, RelativeOp op, Value value) {
        this.attributes.put(id, new StateArgument(id, op, value));
        if (value.isDependent()) {
            this.dependent = true;
        }
    }

    public boolean isPrime() {
        return ref == null;
    }

    public boolean isDependent() {
        return dependent;
    }

    public StateRef getRef() {
        return ref;
    }

    public void setRef(StateRef ref) {
        this.ref = ref;
        if (ref != null && ref.getRef().isDependent()) {
            this.dependent = true;
        }
    }

    public StateConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(StateConstraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StateDesc");
        sb.append("[").append(getId()).append("]{");
        sb.append("attributes=").append(attributes.values());
        sb.append(", constraint=").append(constraint);
        sb.append(", ref=").append(ref);
        sb.append('}');
        return sb.toString();
    }

    public Value arg(String name) {
        StateArgument argument = attributes.get(name);
        if (argument == null) {
            throw new IllegalArgumentException("no arg named " + String.valueOf(name));
        }

        return argument.getValue();
    }

    public boolean hasArg(String name) {
        return attributes.containsKey(name);
    }
}
