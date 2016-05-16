package lab.cadl.analysis.behavior.engine.event;

/**
 * Created by lirui on 2016/5/15.
 */
public class EventAssignment {
    private String name;
    private int position;

    public EventAssignment(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}
