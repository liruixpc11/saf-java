package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.event.Event;
import lab.cadl.analysis.behavior.engine.event.EventAssignment;
import lab.cadl.analysis.behavior.engine.event.EventCriteria;
import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.attribute.ArgumentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.IndependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.PrimeValue;
import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.state.StateArgument;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class StateProcessor {
    private EventRepository eventRepository;
    private AnalysisInstanceRegistry instanceRegistry;

    public StateProcessor(EventRepository eventRepository, AnalysisInstanceRegistry instanceRegistry) {
        this.eventRepository = eventRepository;
        this.instanceRegistry = instanceRegistry;
    }

    public List<StateInstance> process(StateDesc desc) {
        // 已经处理过的直接返回结果处理
        List<StateInstance> instances = instanceRegistry.query(desc);
        if (instances != null) {
            return instances;
        }

        if (desc.isDependent()) {
            return processDependent(desc);
        } else {
            return processIndependent(desc);
        }
    }

    private List<StateInstance> processDependent(StateDesc desc) {
        return Collections.emptyList();
    }

    private List<StateInstance> processIndependent(StateDesc desc) {
        List<EventCriteria> criteriaList = desc.getArguments().values()
                .stream()
                .filter(argument -> !argument.getValue().isAssignment())
                .map(argument -> new EventCriteria(argument.getName(), argument.getOp(), (IndependentValue) argument.getValue()))
                .collect(Collectors.toList());

        List<EventAssignment> assignmentList = desc.getArguments().values()
                .stream()
                .filter(argument -> argument.getValue().isAssignment())
                .map(argument -> new EventAssignment(argument.getName(), ((ArgumentValue) argument.getValue()).position()))
                .collect(Collectors.toList());

        return eventRepository.query("PACKET_TCP", criteriaList, assignmentList).stream().map(e -> new StateInstance(e, desc)).collect(Collectors.toList());
    }


    private IndependentValue resolve(Value value) {
        return new PrimeValue<>();
    }
}
