package lab.cadl.analysis.behavior.engine.model.behavior;

import lab.cadl.analysis.behavior.engine.model.state.StateDesc;

/**
 *
 */
public class StateBehaviorNode extends AbstractBehaviorNode {
    private StateDesc state;

    public StateBehaviorNode(StateDesc state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state.getName();
    }
}
