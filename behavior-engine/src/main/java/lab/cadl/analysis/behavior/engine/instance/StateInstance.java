package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.event.Event;
import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;

import java.time.Instant;
import java.util.*;
import java.util.List;

/**
 *
 */
public class StateInstance implements AnalysisInstance<StateDesc> {
    private Event event;
    private StateDesc desc;

    public StateInstance(Event event, StateDesc desc) {
        this.event = event;
        this.desc = desc;
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

    @Override
    public StateDesc desc() {
        return desc;
    }

    @Override
    public String toString() {
        return desc.getId() + "-" + event.toString();
    }
}
