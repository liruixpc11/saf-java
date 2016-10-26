package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.event.Event;
import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.attribute.DependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.IndependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;

import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class StateInstance implements AnalysisInstance<StateDesc> {
    private Event event;
    private StateDesc desc;
    private Map<DependentValue, StateInstance> refMap;

    public StateInstance(Event event, StateDesc desc) {
        this.event = event;
        this.desc = desc;
        if (this.desc.isDependent()) {
            refMap = new HashMap<>();
        }
    }

    @Override
    public Instant startTime() {
        return event.getTimestamp();
    }

    @Override
    public Instant endTime() {
        return event.getTimestamp();
    }

    @Override
    public long size() {
        return 1;
    }

    @Override
    public List<AnalysisInstance> content() {
        return Collections.emptyList();
    }

    public void addRef(DependentValue value, StateInstance instance) {
        assert desc.isDependent();
        refMap.put(value, instance);
    }

    public Map<DependentValue, StateInstance> getRefMap() {
        return refMap;
    }

    @Override
    public StateDesc desc() {
        return desc;
    }

    @Override
    public String toString() {
        String s = desc.getId() + "->" + event.toString();
        if (refMap != null) {
            for (StateInstance dependee : refMap.values().stream().distinct().collect(Collectors.toList())) {
                s += "\n  " + dependee;
            }
        }

        return s;
    }

    public IndependentValue resolve(String attribute) {
        return IndependentValue.of(event.attr(attribute));
    }
}
