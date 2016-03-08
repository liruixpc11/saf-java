package lab.cadl.analysis.behavior.engine.model.constraint;

/**
 *
 */
public enum TimeConstraintType {
    At("at"),
    Duration("duration"),
    End("end")
    ;

    private String name;

    TimeConstraintType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TimeConstraintType parse(String name) {
        for (TimeConstraintType constraint : values()) {
            if (constraint.getName().equalsIgnoreCase(name)) {
                return constraint;
            }
        }

        throw new IllegalArgumentException("未知时间约束类型：" + String.valueOf(name));
    }
}
