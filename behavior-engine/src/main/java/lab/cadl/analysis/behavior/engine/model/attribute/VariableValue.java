package lab.cadl.analysis.behavior.engine.model.attribute;

import lab.cadl.analysis.behavior.engine.exception.EngineException;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.SymbolTable;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;

/**
 *
 */
public class VariableValue extends DependentValue {
    private StateDesc desc;
    private String attribute;

    public VariableValue(StateDesc desc, String attribute) {
        this.desc = desc;
        this.attribute = attribute;
    }

    public StateDesc getDesc() {
        return desc;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return "$" + desc.getName() + "." + attribute;
    }
}
