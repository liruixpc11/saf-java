package lab.cadl.analysis.behavior.engine.utils;

import java.time.Instant;

public class TimeUtils {
    public static long nanos(Instant instant) {
        return instant.getEpochSecond() * 1000_000_000L + instant.getNano();
    }
}
