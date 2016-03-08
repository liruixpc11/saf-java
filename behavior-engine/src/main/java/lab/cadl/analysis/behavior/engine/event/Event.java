package lab.cadl.analysis.behavior.engine.event;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Event {
    private long eventNumber;
    private Instant timestamp;
    private String type;
    private String origin;
    private final List<String> columnNames;

    private Map<String, Object> attributes = new HashMap<>();

    public Event(long eventNumber, Instant timestamp, String type, String origin, List<String> columnNames) {
        this.eventNumber = eventNumber;
        this.timestamp = timestamp;
        this.type = type;
        this.origin = origin;
        this.columnNames = columnNames;
    }

    public long getEventNumber() {
        return eventNumber;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getOrigin() {
        return origin;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Event attr(String name, Object object) {
        attributes.put(name, object);
        return this;
    }

    public Object attr(String name) {
        return attributes.get(name);
    }

    public Object attr(int index) {
        return attributes.get(columnNames.get(index));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event{");
        sb.append("eventNumber=").append(eventNumber);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", type='").append(type).append('\'');
        sb.append(", origin='").append(origin).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }
}
