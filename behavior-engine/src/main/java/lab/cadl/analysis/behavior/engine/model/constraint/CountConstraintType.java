package lab.cadl.analysis.behavior.engine.model.constraint;

/**
 *
 */
public enum CountConstraintType {
    BehaviorCount("bcount"),
    InstanceCount("icount"),
    Rate("rate")
    ;

    private String name;

    CountConstraintType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CountConstraintType parse(String name) {
        for (CountConstraintType constraint : values()) {
            if (constraint.getName().equalsIgnoreCase(name)) {
                return constraint;
            }
        }

        throw new IllegalArgumentException("未知计数约束类型：" + String.valueOf(name));
    }
}
