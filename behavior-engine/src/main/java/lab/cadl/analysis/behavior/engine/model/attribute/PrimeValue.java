package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public class PrimeValue<T> extends IndependentValue {
    private T value;

    public PrimeValue(T value) {
        this.value = value;
    }

    public PrimeValue() {
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
