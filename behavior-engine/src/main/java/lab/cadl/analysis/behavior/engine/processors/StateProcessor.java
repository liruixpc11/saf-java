package lab.cadl.analysis.behavior.engine.processors;

import com.sun.deploy.util.ArrayUtil;
import lab.cadl.analysis.behavior.engine.event.Event;
import lab.cadl.analysis.behavior.engine.event.EventAssignment;
import lab.cadl.analysis.behavior.engine.event.EventCriteria;
import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.exception.EngineException;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.Constants;
import lab.cadl.analysis.behavior.engine.model.attribute.ArgumentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.DependentValue;
import lab.cadl.analysis.behavior.engine.model.attribute.Value;
import lab.cadl.analysis.behavior.engine.model.attribute.VariableValue;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    public List<AnalysisInstance> process(StateDesc desc) {
        List<AnalysisInstance> instances = instanceRegistry.query(desc);
        if (instances != null) {
            logger.debug("use instances of {} in cache", desc.getQualifiedName());
            return instances;
        }

        instances = desc.isDependent() ? processDependent(desc) : processIndependent(desc);
        logger.info("state {} found {} instances",
                desc.getQualifiedName(),
                instances.size()
        );
        return instances;
    }

    @NotNull
    private List<AnalysisInstance> processDependent(@NotNull StateDesc desc) {
        // instances dependent
        List<StateDesc> dependeeList = new ArrayList<>(extractDependee(desc));
        List<List<AnalysisInstance>> instancesListList = dependeeList.stream()
                .map(this::process)
                .collect(Collectors.toList());
        InstanceProduct product = new InstanceProduct(instancesListList);

        // query parameters
        List<EventAssignment> assignments = extractAssignments(desc);
        List<EventCriteria> criteriaList = extractIndependentCriteria(desc);

        List<EventCriteria> dependentCriteriaList = extractDependentCriteria(desc);

        // traverse
        String eventType = eventType(desc);
        Set<AnalysisInstance> instanceSet = new HashSet<>();
        AnalysisInstance[] dependeeRecord;
        while ((dependeeRecord = product.next()) != null) {
            if (Arrays.stream(dependeeRecord).allMatch(instanceSet::contains)) {
                continue;
            }

            List<EventCriteria> resolvedCriteria = resolve(dependentCriteriaList, dependeeRecord, dependeeList);
            criteriaList.addAll(resolvedCriteria);

            List<Pair<DependentValue, AnalysisInstance>> dependencies = new ArrayList<>();
            for (EventCriteria criteria : dependentCriteriaList) {
                VariableValue variable = (VariableValue) criteria.getValue();
                dependencies.add(new ImmutablePair<>(variable, dependeeRecord[indexOf(dependeeList, variable.getDesc())]));
            }

            for (Event event : eventRepository.query(eventType, criteriaList, assignments)) {
                StateInstance instance = new StateInstance(event, desc);
                for (Pair<DependentValue, AnalysisInstance> pair : dependencies) {
                    instance.addRef(pair.getLeft(), (StateInstance) pair.getRight());
                }

                instanceSet.add(instance);
            }

            criteriaList.removeAll(resolvedCriteria);
        }

        List<AnalysisInstance> instances = new ArrayList<>(instanceSet);
        instanceRegistry.register(desc, instances);
        return instances;
    }

    private long[] aggregateEventNumbers(AnalysisInstance[] instances) {
        long[] eventNumbers = new long[instances.length];
        for (int i = 0; i < eventNumbers.length; i++) {
            eventNumbers[i] = ((StateInstance) instances[i]).getEvent().getEventNumber();
        }

        return eventNumbers;
    }

    private long[] aggregateEventNumbers(List<List<AnalysisInstance>> instancesListList) {
        Set<AnalysisInstance> dependencies = new HashSet<>();
        instancesListList.forEach(dependencies::addAll);
        long[] eventNumbers = new long[dependencies.size()];
        int i = 0;
        for (AnalysisInstance instance : dependencies) {
            eventNumbers[i] = ((StateInstance) instance).getEvent().getEventNumber();
            i++;
        }

        Arrays.sort(eventNumbers);
        return eventNumbers;
    }

    private int indexOf(List<StateDesc> dependeeList, StateDesc desc) {
        int index = 0;
        for (; index < dependeeList.size(); index++) {
            if (dependeeList.get(index).is(desc)) {
                break;
            }
        }

        if (index == dependeeList.size()) {
            throw new EngineException("未找到依赖的StateDesc: " + desc);
        }

        return index;
    }

    private List<EventCriteria> resolve(List<EventCriteria> dependentCriteriaList, AnalysisInstance[] dependeeRecord, List<StateDesc> dependeeList) {
        return dependentCriteriaList.stream()
                .map(c -> {
                    VariableValue variable = (VariableValue) c.getValue();
                    Value value = ((StateInstance) dependeeRecord[indexOf(dependeeList, variable.getDesc())]).resolve(variable.getAttribute());
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
        final Set<String> overwrites = new HashSet<>();
        List<EventCriteria> criteriaList = new ArrayList<>();
        for (; desc != null; overwrites.addAll(desc.getArguments().keySet()), desc = desc.getRef() == null ? null : desc.getRef().getRef()) {
            desc.getArguments().values()
                    .stream()
                    .filter(argument -> !overwrites.contains(argument.getName()) && !argument.getValue().isAssignment() && argument.getValue().isDependent())
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
        final Set<String> overwrites = new HashSet<>();
        List<EventAssignment> assignments = new ArrayList<>();
        for (; desc != null; overwrites.addAll(desc.getArguments().keySet()), desc = desc.getRef() == null ? null : desc.getRef().getRef()) {
            desc.getArguments().values()
                    .stream()
                    .filter(argument -> !overwrites.contains(argument.getName()) && argument.getValue().isAssignment())
                    .map(argument -> new EventAssignment(argument.getName(), ((ArgumentValue) argument.getValue()).position()))
                    .forEach(assignments::add);
        }

        return assignments;
    }

    @NotNull
    private List<AnalysisInstance> processIndependent(@NotNull StateDesc desc) {
        List<EventCriteria> criteriaList = extractIndependentCriteria(desc);
        List<EventAssignment> assignmentList = extractAssignments(desc);
        String eventType = eventType(desc);
        List<AnalysisInstance> states = eventRepository.query(eventType, criteriaList, assignmentList).stream()
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
