package lab.cadl.analysis.behavior.engine.model.behavior;

import lab.cadl.analysis.behavior.engine.model.op.LogicalOp;

/**
 *
 */
public class LogicalBehaviorNode extends AbstractBehaviorNode {
    private BehaviorNode left;
    private LogicalOp op;
    private BehaviorNode right;

    public LogicalBehaviorNode(BehaviorNode left, LogicalOp op, BehaviorNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(left) + " " + op.getOp() + " " + String.valueOf(right) + ")";
    }
}
