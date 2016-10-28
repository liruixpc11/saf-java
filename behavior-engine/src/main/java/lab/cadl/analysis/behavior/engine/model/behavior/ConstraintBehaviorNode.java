package lab.cadl.analysis.behavior.engine.model.behavior;

import lab.cadl.analysis.behavior.engine.model.constraint.BehaviorConstraint;
import lab.cadl.analysis.behavior.engine.model.constraint.CountConstraint;
import lab.cadl.analysis.behavior.engine.model.constraint.TimeConstraint;

/**
 *
 */
public class ConstraintBehaviorNode extends AbstractBehaviorNode {
    private BehaviorNode child;
    private BehaviorConstraint behaviorConstraint;

    public ConstraintBehaviorNode(BehaviorNode child, BehaviorConstraint behaviorConstraint) {
        this.child = child;
        this.behaviorConstraint = behaviorConstraint;
    }

    public BehaviorNode getChild() {
        return child;
    }

    public BehaviorConstraint getBehaviorConstraint() {
        return behaviorConstraint;
    }

    @Override
    public String toString() {
        return "(" + child.toString() + ")" + String.valueOf(behaviorConstraint);
    }

    @Override
    public <T> T accept(BehaviorNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
