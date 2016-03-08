package lab.cadl.analysis.behavior.engine.model.op;

/**
 *
 */
public enum RelativeOp {
    Equal("="),
    NotEqual("!="),
    Larger(">"),
    EqualLarger(">="),
    Less("<"),
    EqualLess("<=");

    private String op;

    RelativeOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public static RelativeOp parse(String op) {
        for (RelativeOp relativeOp : values()) {
            if (relativeOp.getOp().equalsIgnoreCase(op)) {
                return relativeOp;
            }
        }

        throw new IllegalArgumentException("未知关系操作符：" + String.valueOf(op));
    }
}
