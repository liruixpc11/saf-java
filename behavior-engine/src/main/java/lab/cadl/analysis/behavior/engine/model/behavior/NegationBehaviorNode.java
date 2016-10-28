package lab.cadl.analysis.behavior.engine.model.behavior;

/**
 *
 */
public class NegationBehaviorNode extends AbstractBehaviorNode {
    private BehaviorNode child;

    public NegationBehaviorNode(BehaviorNode child) {
        this.child = child;
    }

    public BehaviorNode getChild() {
        return child;
    }

    @Override
    public <T> T accept(BehaviorNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "not " + String.valueOf(child);
    }
}
