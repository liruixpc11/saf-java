package lab.cadl.analysis.behavior.engine.model.attribute;

import java.time.Instant;

/**
 *
 */
public class TimeValue extends IndependentValue {
    private Instant instant;

    public TimeValue(long milliSeconds) {
        instant = Instant.ofEpochSecond(milliSeconds / 1000, (milliSeconds % 1000) * 1000_000);
    }

    public TimeValue(Instant instant) {
        this.instant = instant;
    }

    @Override
    public String toString() {
        return String.valueOf(instant);
    }
}
