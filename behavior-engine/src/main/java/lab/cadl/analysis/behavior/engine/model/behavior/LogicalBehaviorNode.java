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

    public BehaviorNode getLeft() {
        return left;
    }

    public LogicalOp getOp() {
        return op;
    }

    public BehaviorNode getRight() {
        return right;
    }

    @Override
    public <T> T accept(BehaviorNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(left) + " " + op.getOp() + " " + String.valueOf(right) + ")";
    }
}
