package lab.cadl.analysis.behavior.engine.model;

import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BehaviorModel extends IdentifiedObject {
    private String qualifier;
    private SymbolTable symbolTable;
    private List<String> importStrings = new ArrayList<>();
    private List<StateDesc> states = new ArrayList<>();
    private List<BehaviorDesc> behaviors = new ArrayList<>();
    private List<OutputDesc> outputs = new ArrayList<>();

    public BehaviorModel(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
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
        states.add(state);
    }

    public void addBehavior(BehaviorDesc behavior) {
        behaviors.add(behavior);
    }

    public void addOutput(OutputDesc output) {
        outputs.add(output);
    }

    @Override
    public String toString() {
        return "BehaviorModel{" +
                "nameSpace='" + getNameSpace() + '\'' +
                ", name='" + getName() + '\'' +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }
}
