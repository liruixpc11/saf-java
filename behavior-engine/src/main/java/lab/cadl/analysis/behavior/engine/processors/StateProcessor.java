package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.event.Event;
import lab.cadl.analysis.behavior.engine.event.EventAssignment;
import lab.cadl.analysis.behavior.engine.event.EventCriteria;
import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.Constants;
import lab.cadl.analysis.behavior.engine.model.attribute.ArgumentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.IndependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.PrimeValue;
import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.state.StateArgument;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateRef;
import lab.cadl.analysis.behavior.engine.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private String defaultEventType;

    public StateProcessor(EventRepository eventRepository, AnalysisInstanceRegistry instanceRegistry) {
        this.eventRepository = eventRepository;
        this.instanceRegistry = instanceRegistry;
    }

    public void setDefaultEventType(String defaultEventType) {
        this.defaultEventType = defaultEventType;
    }

    public List<StateInstance> process(StateDesc desc) {
//        // 已经处理过的直接返回结果处理
//        List<StateInstance> instances = instanceRegistry.query(desc);
//        if (instances != null) {
//            return instances;
//        }

        if (desc.isDependent()) {
            return processDependent(desc);
        } else {
            return processIndependent(desc);
        }
    }

    @NotNull
    private List<StateInstance> processDependent(@NotNull StateDesc desc) {
        StateRef ref = desc.getRef();
        assert ref != null;

        Pair<List<EventCriteria>, List<EventAssignment>> info = extract(desc);

        return process(ref.getRef()).stream()
                .map(s -> convert(s, info.getLeft(), info.getRight()))
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }

    @Nullable
    private StateInstance convert(StateInstance instance, List<EventCriteria> criteriaList, List<EventAssignment> assignmentList) {
        for (EventCriteria criteria : criteriaList) {
            if (!criteria.ok(instance)) {
                return null;
            }
        }

        return null;
    }

    private Pair<List<EventCriteria>, List<EventAssignment>> extract(StateDesc desc) {
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

        return new Pair<>(criteriaList, assignmentList);
    }

    @NotNull
    private List<StateInstance> processIndependent(@NotNull StateDesc desc) {
        Pair<List<EventCriteria>, List<EventAssignment>> info = extract(desc);
        String eventType = eventType(desc);
        return eventRepository.query(eventType, info.getLeft(), info.getRight()).stream()
                .map(e -> new StateInstance(e, desc))
                .collect(Collectors.toList());
    }

    @NotNull
    private String eventType(@NotNull StateDesc desc) {
        StateDesc qualifier = desc.getModel().getQualifier();
        if (qualifier.hasArg(Constants.EVENT_TYPE_KEY)) {
            return qualifier.arg(Constants.EVENT_TYPE_KEY).toString();
        } else {
            return defaultEventType;
        }
    }

    private IndependentValue resolve(Value value) {
        return new PrimeValue<>();
    }
}
