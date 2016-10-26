package lab.cadl.analysis.behavior.engine.model.attribute;

import java.time.Instant;
import java.util.Date;

/**
 *
 */
public abstract class IndependentValue implements Value {
    @Override
    public final boolean isDependent() {
        return false;
    }

    public static IndependentValue of(Object value) {
        if (value instanceof Date) {
            return new TimeValue(((Date) value).getTime());
        } else if (value instanceof Instant) {
            return new TimeValue((Instant) value);
        } else {
            return new PrimeValue<>(value);
        }
    }
}
