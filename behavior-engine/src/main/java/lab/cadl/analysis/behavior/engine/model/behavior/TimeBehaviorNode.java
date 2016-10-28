package lab.cadl.analysis.behavior.engine.model.behavior;

import lab.cadl.analysis.behavior.engine.model.constraint.TimeOpConstraint;
import lab.cadl.analysis.behavior.engine.model.op.TimeOp;

/**
 *
 */
public class TimeBehaviorNode extends AbstractBehaviorNode {
    private BehaviorNode left;
    private TimeOp timeOp;
    private BehaviorNode right;
    private TimeOpConstraint constraint;

    public TimeBehaviorNode(BehaviorNode left, TimeOp timeOp, BehaviorNode right) {
        this.left = left;
        this.timeOp = timeOp;
        this.right = right;
    }

    public TimeBehaviorNode(BehaviorNode left, TimeOp timeOp, BehaviorNode right, TimeOpConstraint constraint) {
        this.left = left;
        this.timeOp = timeOp;
        this.right = right;
        this.constraint = constraint;
    }

    public BehaviorNode getLeft() {
        return left;
    }

    public TimeOp getTimeOp() {
        return timeOp;
    }

    public BehaviorNode getRight() {
        return right;
    }

    public TimeOpConstraint getConstraint() {
        return constraint;
    }

    @Override
    public <T> T accept(BehaviorNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        String s = "(" + String.valueOf(left) + " " + timeOp.getOp();
        if (constraint != null) {
            s += constraint;
        }
        s += " " + String.valueOf(right) + ")";
        return s;
    }
}
