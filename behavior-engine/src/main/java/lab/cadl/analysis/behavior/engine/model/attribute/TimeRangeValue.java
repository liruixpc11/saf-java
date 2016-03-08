package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public class TimeRangeValue extends IndependentValue {
    private TimeValue begin;
    private TimeValue end;

    public TimeRangeValue(TimeValue begin, TimeValue end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return begin + ":" + end;
    }
}
