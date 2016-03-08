package lab.cadl.analysis.behavior.engine.model.behavior;

/**
 *
 */
public class AlwaysBehaviorNode extends AbstractBehaviorNode {
    private BehaviorNode child;

    public AlwaysBehaviorNode(BehaviorNode child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "[]" + String.valueOf(child);
    }
}
