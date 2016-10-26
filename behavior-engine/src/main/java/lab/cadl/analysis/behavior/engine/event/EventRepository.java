package lab.cadl.analysis.behavior.engine.event;

import lab.cadl.analysis.behavior.engine.model.constraint.StateConstraint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public interface EventRepository extends AutoCloseable {
    default Event query(String eventId) {
        List<Event> events = query(Collections.singletonList(eventId));
        if (events.size() == 1) {
            return events.get(0);
        } else {
            return null;
        }
    }

    List<Event> query(List<String> eventIds);

    List<Event> list(String eventType);

    List<Event> query(String eventType, List<EventCriteria> criteriaList, List<EventAssignment> assignmentList);

    @Override
    void close();
}
