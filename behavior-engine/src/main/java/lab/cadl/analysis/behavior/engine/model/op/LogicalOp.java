package lab.cadl.analysis.behavior.engine.model.op;

/**
 *
 */
public enum LogicalOp {
    And("and"),
    Or("or"),
    Xor("xor")
    ;

    private String op;

    LogicalOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public static LogicalOp parse(String symbol) {
        for (LogicalOp logicalOp : values()) {
            if (logicalOp.getOp().equalsIgnoreCase(symbol)) {
                return logicalOp;
            }
        }

        throw new IllegalArgumentException("未知逻辑操作符：" + String.valueOf(symbol));
    }
}
