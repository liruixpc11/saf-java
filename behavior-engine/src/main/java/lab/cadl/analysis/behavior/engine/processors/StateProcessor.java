package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.event.EventCriteria;
import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
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

    public List<StateInstance> process(StateDesc desc) {
        List<EventCriteria> criteriaList = new ArrayList<>();
        for (StateArgument argument : desc.getArguments().values()) {
            if (argument.getValue() instanceof IndependentValue) {
                criteriaList.add(new EventCriteria(argument.getName(), argument.getOp(), (IndependentValue) argument.getValue()));
            } else {
                IndependentValue value = resolve(argument.getValue());
                criteriaList.add(new EventCriteria(argument.getName(), argument.getOp(), value));
            }
        }

        return eventRepository.query("", criteriaList).stream().map(e -> new StateInstance(e, desc)).collect(Collectors.toList());
    }

    private IndependentValue resolve(Value value) {
        return new PrimeValue<>();
    }
}
