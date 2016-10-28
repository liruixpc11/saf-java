package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public class DurationValue extends TemporalValue {
    private long seconds;
    private long us;

    public DurationValue(long seconds, long us) {
        this.seconds = seconds;
        this.us = us;
    }

    public static DurationValue fromMs(long ms) {
        return new DurationValue(ms / 1000, (ms % 1000) * 1000);
    }

    public long nano() {
        return seconds * 1000_000_000 + us * 1000;
    }

    public long millis() {
        return seconds * 1000_000 + us;
    }

    @Override
    public String toString() {
        if (us == 0) {
            return seconds + "s";
        } else if (us % 1000 == 0) {
            return (seconds * 1000 + us / 1000) + "ms";
        } else {
            return (seconds * 1000_000 + us) + "us";
        }
    }

    public static DurationValue fromUs(long us) {
        return new DurationValue(us / 1000_000, us % 1000_000);
    }
}
