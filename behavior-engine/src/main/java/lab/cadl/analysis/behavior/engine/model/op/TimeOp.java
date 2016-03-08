package lab.cadl.analysis.behavior.engine.model.op;

/**
 *
 */
public enum TimeOp {
    LeadTo("~>"),
    Overlap("olap"),
    Duration("dur"),
    StartWidth("sw"),
    EndWidth("ew"),
    Equal("eq")
    ;

    private String op;

    TimeOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public static TimeOp parse(String symbol) {
        for (TimeOp timeOp : values()) {
            if (timeOp.getOp().equalsIgnoreCase(symbol)) {
                return timeOp;
            }
        }

        throw new IllegalArgumentException("未知时间操作符：" + String.valueOf(symbol));
    }
}
