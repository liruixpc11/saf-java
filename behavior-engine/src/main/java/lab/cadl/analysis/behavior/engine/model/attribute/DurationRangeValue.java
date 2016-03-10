package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public class DurationRangeValue extends IndependentValue {
    private DurationValue begin;
    private DurationValue end;

    public DurationRangeValue(DurationValue begin, DurationValue end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return begin + ":" + end;
    }
}
