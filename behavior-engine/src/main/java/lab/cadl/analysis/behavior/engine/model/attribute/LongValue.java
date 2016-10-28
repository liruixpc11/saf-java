package lab.cadl.analysis.behavior.engine.model.attribute;

public class LongValue extends IndependentValue {
    private long value;

    public LongValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
