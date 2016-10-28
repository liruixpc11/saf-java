package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class AnalysisInstanceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisInstanceRegistry.class);

    private Map<AnalysisDesc, List<AnalysisInstance>> registry = new HashMap<>();

    public List<AnalysisInstance> check(AnalysisDesc desc) {
        List<AnalysisInstance> instances = query(desc);
        if (instances == null) {
            throw new IllegalArgumentException("DESC " + String.valueOf(desc) + " not found");
        }

        return instances;
    }

    public List<AnalysisInstance> query(AnalysisDesc desc) {
        return registry.get(desc);
    }

    public void register(AnalysisDesc desc, List<AnalysisInstance> instances) {
        registry.put(desc, instances);
    }
}
