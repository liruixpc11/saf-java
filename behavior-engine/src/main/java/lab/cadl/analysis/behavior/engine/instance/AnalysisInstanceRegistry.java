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

    private Map<String, AnalysisInstance> instanceMap = new HashMap<>();
    private Map<AnalysisDesc, List<AnalysisInstance>> descMap = new HashMap<>();
    private Map<StateDesc, List<StateInstance>> stateMap = new HashMap<>();
    private Map<BehaviorDesc, List<BehaviorInstance>> behaviorMap = new HashMap<>();

    public void register(String name, AnalysisInstance instance) {
        instanceMap.put(name, instance);

        add(descMap, instance);
        if (instance instanceof StateInstance) {
            add(stateMap, (StateInstance) instance);
        } else if (instance instanceof BehaviorInstance) {
            add(behaviorMap, (BehaviorInstance) instance);
        } else {
            logger.warn("unknown instance type: {}", instance.getClass().getName());
        }
    }

    private <TD extends AnalysisDesc, TI extends AnalysisInstance> void add(Map<TD, List<TI>> map, TI instance) {
        List<TI> instances = map.get(instance.desc());
        if (instances == null) {
            instances = new ArrayList<>();
            map.put((TD) instance.desc(), instances);
        }

        instances.add(instance);
    }

    public AnalysisInstance check(String name) {
        if (!instanceMap.containsKey(name)) {
            throw new IllegalArgumentException("KEY " + String.valueOf(name) + " not found");
        }

        return instanceMap.get(name);
    }

    public List<AnalysisInstance> check(AnalysisDesc desc) {
        if (!descMap.containsKey(desc)) {
            throw new IllegalArgumentException("DESC " + String.valueOf(desc) + " not found");
        }

        return descMap.get(desc);
    }

    public List<StateInstance> query(StateDesc desc) {
        return stateMap.get(desc);
    }

    public void register(StateDesc desc, List<StateInstance> instances) {
        stateMap.put(desc, instances);
    }
}
