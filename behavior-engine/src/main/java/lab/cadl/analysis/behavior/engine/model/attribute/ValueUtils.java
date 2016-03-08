package lab.cadl.analysis.behavior.engine.model.attribute;

import java.time.Instant;

/**
 *
 */
public class ValueUtils {
    private ValueUtils() {}

    public static IndependentValue wrap(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (o instanceof IndependentValue) {
            return (IndependentValue) o;
        } else if (o instanceof Value) {
            throw new IllegalArgumentException("is Value but not IndependentValue: " + o.getClass().getName());
        } else if (o instanceof Instant) {
            return new TimeValue((Instant) o);
        } else {
            return new PrimeValue<>(o);
        }
    }
}
