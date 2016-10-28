package lab.cadl.analysis.behavior.engine.model.constraint;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.model.attribute.*;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorNode;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 *
 */
public class TimeConstraint implements BehaviorConstraint {
    private TimeConstraintType type;
    private RelativeOp relativeOp;
    private TemporalValue value;

    public TimeConstraint(TimeConstraintType type, RelativeOp relativeOp, TemporalValue value) {
        this.type = type;
        this.relativeOp = relativeOp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + type.getName() + relativeOp.getOp() + String.valueOf(value) + "]";
    }

    @Override
    public List<AnalysisInstance> apply(BehaviorDesc desc, BehaviorNode node, List<AnalysisInstance> instances) {
        if (value instanceof TimeValue) {
            TimeValue time = (TimeValue) value;
            switch (type) {
                case At:
                    return instances.stream()
                            .filter(i -> relativeOp.test(i.startNanos(), time.nanos()))
                            .collect(Collectors.toList());
                case End:
                    return instances.stream()
                            .filter(i -> relativeOp.test(i.endNanos(), time.nanos()))
                            .collect(Collectors.toList());
                default:
                    throw new IllegalArgumentException(value.getClass().getSimpleName() + "数据不支持该类型时间约束: " + type);
            }
        } else if (value instanceof DurationValue) {
            DurationValue duration = (DurationValue) value;
            switch (type) {
                case Duration:
                    return instances.stream()
                            .filter(i -> relativeOp.test(i.durationNanos(), duration.nano()))
                            .collect(Collectors.toList());
                default:
                    throw new IllegalArgumentException(value.getClass().getSimpleName() + "数据不支持该类型时间约束: " + type);
            }
        } else if (value instanceof TimeRangeValue) {
            TimeRangeValue range = (TimeRangeValue) value;
            if (relativeOp == RelativeOp.Equal) {
                switch (type) {
                    case At:
                        return instances.stream()
                                .filter(i -> range.getBeginNanos() <= i.startNanos() && i.startNanos() <= range.getEndNanos())
                                .collect(Collectors.toList());
                    case End:
                        return instances.stream()
                                .filter(i -> range.getEndNanos() <= i.endNanos() && i.endNanos() <= range.getEndNanos())
                                .collect(Collectors.toList());
                    default:
                        throw new IllegalArgumentException(value.getClass().getSimpleName() + "数据不支持该类型时间约束: " + type);
                }
            } else if (relativeOp == RelativeOp.NotEqual) {
                switch (type) {
                    case At:
                        return instances.stream()
                                .filter(i -> range.getBeginNanos() >= i.startNanos() || i.endNanos() >= range.getEndNanos())
                                .collect(Collectors.toList());
                    case End:
                        return instances.stream()
                                .filter(i -> range.getEndNanos() >= i.startNanos() || i.endNanos() >= range.getEndNanos())
                                .collect(Collectors.toList());
                    default:
                        throw new IllegalArgumentException(value.getClass().getSimpleName() + "数据不支持该类型时间约束: " + type);
                }
            } else {
                throw new IllegalArgumentException("范围时间约束中,不支持该操作符: " + relativeOp);
            }
        } else if (value instanceof DurationRangeValue) {
            DurationRangeValue range = (DurationRangeValue) value;
            if (relativeOp == RelativeOp.Equal) {
                switch (type) {
                    case Duration:
                        return instances.stream()
                                .filter(i -> range.getBegin().nano() <= i.durationNanos() && i.durationNanos() <= range.getEnd().nano())
                                .collect(Collectors.toList());
                    default:
                        throw new IllegalArgumentException(value.getClass().getSimpleName() + "数据不支持该类型时间约束: " + type);
                }
            } else if (relativeOp == RelativeOp.NotEqual) {
                switch (type) {
                    case Duration:
                        return instances.stream()
                                .filter(i -> range.getBegin().nano() >= i.durationNanos() || i.durationNanos() >= range.getEnd().nano())
                                .collect(Collectors.toList());
                    default:
                        throw new IllegalArgumentException(value.getClass().getSimpleName() + "数据不支持该类型时间约束: " + type);
                }
            } else {
                throw new IllegalArgumentException("持续范围时间约束中,不支持该操作符: " + relativeOp);
            }
        } else {
            throw new IllegalArgumentException("时间约束中,不支持该类型约束值: " + value.getClass().getSimpleName());
        }
    }
}
