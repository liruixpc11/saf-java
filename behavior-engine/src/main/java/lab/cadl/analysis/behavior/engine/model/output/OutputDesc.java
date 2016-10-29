package lab.cadl.analysis.behavior.engine.model.output;

import lab.cadl.analysis.behavior.engine.model.IdentifiedObject;
import lab.cadl.analysis.behavior.engine.model.QualifiedName;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class OutputDesc extends IdentifiedObject {
    private List<String> fields = new ArrayList<>();
    private BehaviorDesc behavior;

    public OutputDesc(QualifiedName qualifiedName, Collection<String> fields) {
        super(qualifiedName);
        this.fields.add("eventno");
        this.fields.addAll(fields);
    }

    public List<String> getFields() {
        return fields;
    }

    public BehaviorDesc getBehavior() {
        return behavior;
    }

    public void setBehavior(BehaviorDesc behavior) {
        this.behavior = behavior;
    }

    @Override
    public String toString() {
        return getName() + "(" + StringUtils.join(fields, ", ") + ") = " + behavior.getRoot();
    }
}
