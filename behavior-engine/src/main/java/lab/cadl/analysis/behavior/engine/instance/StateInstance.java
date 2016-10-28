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
    private List<StateInstance> dependeeList;

    public StateInstance(Event event, StateDesc desc) {
        this.event = event;
        this.desc = desc;
        if (this.desc.isDependent()) {
            refMap = new HashMap<>();
            dependeeList = new ArrayList<>();
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
        dependeeList.add(instance);
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
        return desc.getId() + "->" + event.toString();
    }

    @Override
    public boolean isDependent() {
        return desc.isDependent();
    }

    public List<StateInstance> dependeeList() {
        return dependeeList;
    }

    public IndependentValue resolve(String attribute) {
        return IndependentValue.of(event.attr(attribute));
    }
}
