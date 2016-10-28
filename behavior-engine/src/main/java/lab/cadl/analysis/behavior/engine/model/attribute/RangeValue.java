package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public class RangeValue extends IndependentValue {
    private long min;
    private long max;

    public RangeValue(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    @Override
    public String toString() {
        return min + ":" + max;
    }
}
