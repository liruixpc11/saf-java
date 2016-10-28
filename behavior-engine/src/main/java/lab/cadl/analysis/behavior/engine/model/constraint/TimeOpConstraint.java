package lab.cadl.analysis.behavior.engine.model.constraint;

import lab.cadl.analysis.behavior.engine.model.attribute.DurationRangeValue;
import lab.cadl.analysis.behavior.engine.model.attribute.DurationValue;
import lab.cadl.analysis.behavior.engine.model.attribute.TemporalValue;
import lab.cadl.analysis.behavior.engine.model.op.RelativeOp;

public class TimeOpConstraint {
    private RelativeOp op;
    private TemporalValue value;

    public TimeOpConstraint(RelativeOp op, TemporalValue value) {
        this.op = op;
        this.value = value;
    }

    public boolean satisfy(long nano1, long nano2) {
        long delta = nano2 - nano1;
        if (value instanceof DurationValue) {
            long limit = ((DurationValue) value).nano();
            return op.test(delta, limit);
        } else {
            DurationRangeValue range = (DurationRangeValue) value;

            long low = range.getBegin().nano();
            long high = range.getEnd().nano();

            switch (op) {
                case Equal:
                    return delta > low && delta < high;
                case NotEqual:
                    return delta < low || delta > high;
                default:
                    // TODO 可以在语法层面把这个约束加上
                    throw new IllegalArgumentException("Range约束不支持非相等操作符");
            }
        }
    }

    @Override
    public String toString() {
        return String.format("[%s%s]", op.getOp(), value);
    }

}
