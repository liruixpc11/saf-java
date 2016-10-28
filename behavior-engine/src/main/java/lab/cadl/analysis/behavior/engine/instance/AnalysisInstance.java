package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.utils.TimeUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public interface AnalysisInstance<TD extends AnalysisDesc> {
    List<AnalysisInstance> EMPTY = Collections.emptyList();

    Instant startTime();

    Instant endTime();

    default long startNanos() {
        return TimeUtils.nanos(startTime());
    }

    default long endNanos() {
        return TimeUtils.nanos(endTime());
    }

    default long durationNanos() {
        return endNanos() - startNanos();
    }

    /**
     * count of events
     */
    long size();

    List<AnalysisInstance> content();

    TD desc();

    default List<AnalysisInstance> toList() {
        return Collections.singletonList(this);
    }

    default boolean isDependent() {
        return false;
    }

    default long rate() {
        return size() * 1000_000_000 / durationNanos();
    }

    default String toString(int indent) {
        String s = "";
        for (int i = 0; i < indent; i++) {
            s += "  ";
        }

        s += toString();
        return s;
    }
}
