package lab.cadl.analysis.behavior.engine.model.behavior;

/**
 *
 */
public class AlwaysBehaviorNode extends AbstractBehaviorNode {
    private BehaviorNode child;

    public AlwaysBehaviorNode(BehaviorNode child) {
        this.child = child;
    }

    public BehaviorNode getChild() {
        return child;
    }

    @Override
    public String toString() {
        return "[]" + String.valueOf(child);
    }

    @Override
    public <T> T accept(BehaviorNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
