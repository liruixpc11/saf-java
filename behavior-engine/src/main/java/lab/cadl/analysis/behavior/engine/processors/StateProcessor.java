package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.event.Event;
import lab.cadl.analysis.behavior.engine.event.EventAssignment;
import lab.cadl.analysis.behavior.engine.event.EventCriteria;
import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.Constants;
import lab.cadl.analysis.behavior.engine.model.attribute.ArgumentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.DependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.attribute.VariableValue;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class StateProcessor {
    private static final Logger logger = LoggerFactory.getLogger(StateProcessor.class);
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
        List<StateInstance> instances = instanceRegistry.query(desc);
        if (instances != null) {
            logger.debug("use instances of {} in cache", desc.getQualifiedName());
            return instances;
        }

        if (desc.isDependent()) {
            return processDependent(desc);
        } else {
            return processIndependent(desc);
        }
    }

    @NotNull
    private List<StateInstance> processDependent(@NotNull StateDesc desc) {
        // instances dependent
        List<StateDesc> dependeeList = new ArrayList<>(extractDependee(desc));
        List<List<StateInstance>> instancesListList = dependeeList.stream()
                .map(this::process)
                .collect(Collectors.toList());
        StateProduct product = new StateProduct(instancesListList);

        // query parameters
        List<EventAssignment> assignments = extractAssignments(desc);
        List<EventCriteria> criteriaList = extractIndependentCriteria(desc);

        List<EventCriteria> dependentCriteriaList = extractDependentCriteria(desc);

        // traverse
        String eventType = eventType(desc);
        List<StateInstance> instances = new ArrayList<>();
        StateInstance[] dependeeRecord;
        while ((dependeeRecord = product.next()) != null) {
            List<EventCriteria> resolvedCriteria = resolve(dependentCriteriaList, dependeeRecord, dependeeList);
            criteriaList.addAll(resolvedCriteria);

            List<Pair<DependentValue, StateInstance>> dependencies = new ArrayList<>();
            for (EventCriteria criteria : dependentCriteriaList) {
                VariableValue variable = (VariableValue) criteria.getValue();
                dependencies.add(new ImmutablePair<>(variable, dependeeRecord[dependeeList.indexOf(variable.getDesc())]));
            }

            for (Event event : eventRepository.query(eventType, criteriaList, assignments)) {
                StateInstance instance = new StateInstance(event, desc);
                for (Pair<DependentValue, StateInstance> pair : dependencies) {
                    instance.addRef(pair.getLeft(), pair.getRight());
                }

                instances.add(instance);
            }

            criteriaList.removeAll(resolvedCriteria);
        }

        instanceRegistry.register(desc, instances);
        return instances;
    }

    private List<EventCriteria> resolve(List<EventCriteria> dependentCriteriaList, StateInstance[] dependeeRecord, List<StateDesc> dependeeList) {
        return dependentCriteriaList.stream()
                .map(c -> {
                    VariableValue variable = (VariableValue) c.getValue();
                    int index = dependeeList.indexOf(variable.getDesc());
                    Value value = dependeeRecord[index].resolve(variable.getAttribute());

                    return new EventCriteria(c.getName(), c.getOp(), value);
                })
                .collect(Collectors.toList());
    }

    private Set<StateDesc> extractDependee(StateDesc desc) {
        return desc.getArguments().values()
                .stream()
                .filter(argument -> argument.getValue() instanceof VariableValue)
                .map(argument -> ((VariableValue) argument.getValue()).getDesc())
                .collect(Collectors.toSet());
    }

    private List<EventCriteria> extractDependentCriteria(StateDesc desc) {
        List<EventCriteria> criteriaList = new ArrayList<>();
        for (; desc != null; desc = desc.getRef() == null ? null : desc.getRef().getRef()) {
            desc.getArguments().values()
                    .stream()
                    .filter(argument -> !argument.getValue().isAssignment() && argument.getValue().isDependent())
                    .map(argument -> new EventCriteria(argument.getName(), argument.getOp(), argument.getValue()))
                    .forEach(criteriaList::add);
        }

        return criteriaList;
    }

    private List<EventCriteria> extractIndependentCriteria(StateDesc desc) {
        List<EventCriteria> criteriaList = new ArrayList<>();
        for (; desc != null; desc = desc.getRef() == null ? null : desc.getRef().getRef()) {
            desc.getArguments().values()
                    .stream()
                    .filter(argument -> !argument.getValue().isAssignment() && !argument.getValue().isDependent())
                    .map(argument -> new EventCriteria(argument.getName(), argument.getOp(), argument.getValue()))
                    .forEach(criteriaList::add);
        }

        return criteriaList;
    }

    private List<EventAssignment> extractAssignments(StateDesc desc) {
        List<EventAssignment> assignments = new ArrayList<>();
        for (; desc != null; desc = desc.getRef() == null ? null : desc.getRef().getRef()) {
            desc.getArguments().values()
                    .stream()
                    .filter(argument -> argument.getValue().isAssignment())
                    .map(argument -> new EventAssignment(argument.getName(), ((ArgumentValue) argument.getValue()).position()))
                    .forEach(assignments::add);
        }

        return assignments;
    }

    @NotNull
    private List<StateInstance> processIndependent(@NotNull StateDesc desc) {
        List<EventCriteria> criteriaList = extractIndependentCriteria(desc);
        List<EventAssignment> assignmentList = extractAssignments(desc);
        String eventType = eventType(desc);
        List<StateInstance> states = eventRepository.query(eventType, criteriaList, assignmentList).stream()
                .map(e -> new StateInstance(e, desc))
                .collect(Collectors.toList());
        instanceRegistry.register(desc, states);
        return states;
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
}
