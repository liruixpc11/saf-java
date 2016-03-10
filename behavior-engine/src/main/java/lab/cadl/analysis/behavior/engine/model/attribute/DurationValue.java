package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public class DurationValue extends IndependentValue {
    private long seconds;
    private long us;

    public DurationValue(long seconds, long us) {
        this.seconds = seconds;
        this.us = us;
    }

    public static DurationValue fromMs(long ms) {
        return new DurationValue(ms / 1000, (ms % 1000) * 1000);
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
}
