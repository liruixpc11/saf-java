package lab.cadl.analysis.behavior.engine.model;

import ca.szc.configparser.StringUtil;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BehaviorModel extends IdentifiedObject {
    // 过滤事件，只处理满足条件的事件
    private StateDesc qualifier;
    private SymbolTable symbolTable;
    private List<String> importStrings = new ArrayList<>();
    private List<StateDesc> states = new ArrayList<>();
    private List<BehaviorDesc> behaviors = new ArrayList<>();
    private List<OutputDesc> outputs = new ArrayList<>();

    public BehaviorModel(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public StateDesc getQualifier() {
        return qualifier;
    }

    public void setQualifier(StateDesc qualifier) {
        this.qualifier = qualifier;
    }

    public List<String> getImportStrings() {
        return importStrings;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<StateDesc> getStates() {
        return states;
    }

    public List<BehaviorDesc> getBehaviors() {
        return behaviors;
    }

    public List<OutputDesc> getOutputs() {
        return outputs;
    }

    public void addState(StateDesc state) {
        state.setModel(this);
        states.add(state);
    }

    public void addBehavior(BehaviorDesc behavior) {
        behavior.setModel(this);
        behaviors.add(behavior);
    }

    public void addOutput(OutputDesc output) {
        output.setModel(this);
        outputs.add(output);
    }

    @Override
    public String toString() {
        String s = "[header]\n";
        s += "NAMESPACE = " + getNameSpace() + "\n";
        s += "NAME = " + getName() + "\n";
        s += "QUALIFIER = " + getQualifier() + "\n";
        if (!importStrings.isEmpty()) {
            s += "IMPORT = " + StringUtils.join(importStrings, ", ") + "\n";
        }
        s += "\n";

        s += "[states]\n";
        s += StringUtils.join(states, "\n");
        s += "\n\n";

        s += "[behaviors]\n";
        s += StringUtils.join(behaviors, "\n");
        s += "\n\n";

        s += "[model]\n";
        s += StringUtils.join(outputs, "\n");
        s += "\n";
        return s;
    }
}
