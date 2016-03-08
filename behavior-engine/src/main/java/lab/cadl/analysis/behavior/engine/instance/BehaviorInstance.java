package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;

import java.time.Instant;
import java.util.List;

/**
 *
 */
public class BehaviorInstance implements AnalysisInstance {
    private List<AnalysisInstance> content;

    @Override
    public Instant startTime() {
        return null;
    }

    @Override
    public Instant endTime() {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public List<AnalysisInstance> content() {
        return null;
    }

    @Override
    public AnalysisDesc desc() {
        return null;
    }
}
