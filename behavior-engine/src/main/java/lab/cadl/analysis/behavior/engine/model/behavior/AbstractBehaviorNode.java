package lab.cadl.analysis.behavior.engine.model.behavior;

import java.util.List;

/**
 *
 */
public abstract class AbstractBehaviorNode implements BehaviorNode {
    private BehaviorNode parent;
    private List<BehaviorNode> children;

    @Override
    public AbstractBehaviorNode setParent(BehaviorNode parent) {
        this.parent = parent;

        if (parent != null && parent instanceof AbstractBehaviorNode) {
            ((AbstractBehaviorNode) parent).children.add(this);
        }

        return this;
    }

    @Override
    public BehaviorNode parent() {
        return parent;
    }

    @Override
    public List<BehaviorNode> children() {
        return children;
    }
}
