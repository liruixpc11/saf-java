package lab.cadl.analysis.behavior.engine.event;

import lab.cadl.analysis.behavior.engine.model.constraint.StateConstraint;

import java.util.List;

/**
 *
 */
public interface EventRepository extends AutoCloseable {
    List<Event> list(String eventType);

    List<Event> query(String eventType, List<EventCriteria> criteriaList);

    @Override
    void close();
}
