package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public class DurationRangeValue extends TemporalValue {
    private DurationValue begin;
    private DurationValue end;

    public DurationRangeValue(DurationValue begin, DurationValue end) {
        this.begin = begin;
        this.end = end;
    }

    public DurationValue getBegin() {
        return begin;
    }

    public DurationValue getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return begin + ":" + end;
    }
}
