package lab.cadl.analysis.behavior.engine.model.behavior;

public class RefBehaviorNode extends AbstractBehaviorNode {
    private BehaviorDesc behavior;
    private boolean recursive;

    public RefBehaviorNode(BehaviorDesc behavior) {
        this.behavior = behavior;
        this.recursive = false;
    }

    public RefBehaviorNode(BehaviorDesc behavior, boolean recursive) {
        this.behavior = behavior;
        this.recursive = recursive;
    }

    public BehaviorDesc getBehavior() {
        return behavior;
    }

    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public <T> T accept(BehaviorNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (!recursive && behavior.getRoot() != null) {
            return behavior.getRoot().toString();
        } else {
            return behavior.getQualifiedName().toString();
        }
    }
}
