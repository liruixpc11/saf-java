package lab.cadl.analysis.behavior.engine.model.constraint;

/**
 *
 */
public enum StateConstraintType {
    BehaviorCount("bcount"),
    InstanceCount("icount"),
    Rate("rate"),
    Limit("_limit"),
    EventNumber("_eventno")
    ;

    private String name;

    StateConstraintType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static StateConstraintType parse(String name) {
        for (StateConstraintType constraint : values()) {
            if (constraint.getName().equalsIgnoreCase(name)) {
                return constraint;
            }
        }

        throw new IllegalArgumentException("未知State约束类型：" + String.valueOf(name));
    }
}
