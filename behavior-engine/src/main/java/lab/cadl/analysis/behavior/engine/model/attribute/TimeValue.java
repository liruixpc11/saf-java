package lab.cadl.analysis.behavior.engine.model.attribute;

import lab.cadl.analysis.behavior.engine.utils.TimeUtils;

import java.time.Instant;

/**
 *
 */
public class TimeValue extends TemporalValue {
    private Instant instant;

    public TimeValue(long milliSeconds) {
        instant = Instant.ofEpochSecond(milliSeconds / 1000, (milliSeconds % 1000) * 1000_000);
    }

    public TimeValue(Instant instant) {
        this.instant = instant;
    }

    public Instant getInstant() {
        return instant;
    }

    public long nanos() {
        return TimeUtils.nanos(instant);
    }

    @Override
    public String toString() {
        return String.valueOf(instant);
    }
}
