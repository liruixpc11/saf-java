package lab.cadl.analysis.behavior.engine.model.attribute;

import lab.cadl.analysis.behavior.engine.utils.TimeUtils;

public class TimeRangeValue extends TemporalValue {
    private TimeValue begin;
    private TimeValue end;

    public TimeRangeValue(TimeValue begin, TimeValue end) {
        this.begin = begin;
        this.end = end;
    }

    public TimeValue getBegin() {
        return begin;
    }

    public TimeValue getEnd() {
        return end;
    }

    public long getBeginNanos() {
        return TimeUtils.nanos(begin.getInstant());
    }

    public long getEndNanos() {
        return TimeUtils.nanos(end.getInstant());
    }
}
