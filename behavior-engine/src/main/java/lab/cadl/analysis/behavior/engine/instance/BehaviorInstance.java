package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;

import java.time.Instant;
import java.util.List;

/**
 *
 */
public class BehaviorInstance implements AnalysisInstance<BehaviorDesc> {
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
    public BehaviorDesc desc() {
        return null;
    }
}
