package lab.cadl.analysis.behavior.engine.model.behavior;

import lab.cadl.analysis.behavior.engine.model.op.TimeOp;

/**
 *
 */
public class TimeBehaviorNode extends AbstractBehaviorNode {
    private BehaviorNode left;
    private TimeOp timeOp;
    private BehaviorNode right;

    public TimeBehaviorNode(BehaviorNode left, TimeOp timeOp, BehaviorNode right) {
        this.left = left;
        this.timeOp = timeOp;
        this.right = right;
    }

    @Override
    public <T> T accept(BehaviorNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(left) + " " + timeOp.getOp() + " " + String.valueOf(right) + ")";
    }
}
