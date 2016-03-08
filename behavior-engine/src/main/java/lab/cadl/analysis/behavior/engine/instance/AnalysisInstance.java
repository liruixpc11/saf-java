package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;

import java.time.Instant;
import java.util.List;

/**
 *
 */
public interface AnalysisInstance {
    Instant startTime();
    Instant endTime();
    long size();
    List<AnalysisInstance> content();
    AnalysisDesc desc();
}
