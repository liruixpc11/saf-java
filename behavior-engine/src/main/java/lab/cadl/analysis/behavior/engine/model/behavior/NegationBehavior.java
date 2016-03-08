package lab.cadl.analysis.behavior.engine.model.behavior;

/**
 *
 */
public class NegationBehavior extends AbstractBehaviorNode {
    private BehaviorNode child;

    public NegationBehavior(BehaviorNode child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "not " + String.valueOf(child);
    }
}
