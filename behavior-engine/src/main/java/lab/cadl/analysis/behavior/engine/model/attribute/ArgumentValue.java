package lab.cadl.analysis.behavior.engine.model.attribute;

import lab.cadl.analysis.behavior.engine.event.Event;

/**
 *
 */
public class ArgumentValue extends IndependentValue {
    private int position;

    public ArgumentValue(int position) {
        this.position = position;
    }

    public IndependentValue resolve(Event event) {
        return ValueUtils.wrap(event.attr(position));
    }

    @Override
    public String toString() {
        return "$" + position;
    }
}
