package lab.cadl.analysis.behavior.engine.model.attribute;

import lab.cadl.analysis.behavior.engine.model.SymbolTable;

/**
 *
 */
public class VariableValue extends DependentValue {
    private String namespace;
    private String name;

    public VariableValue(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public IndependentValue resolve(SymbolTable symbolTable) {
        // TODO 解析变量
        return null;
    }

    @Override
    public String toString() {
        return "$" + namespace + "." + name;
    }
}
