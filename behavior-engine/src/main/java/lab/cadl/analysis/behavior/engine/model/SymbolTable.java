package lab.cadl.analysis.behavior.engine.model;

import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  符号表
 */
public class SymbolTable {
    private static final Logger logger = LoggerFactory.getLogger(SymbolTable.class);
    private SymbolTable parent;
    private final Map<String, IdentifiedObject> symbolTable = new HashMap<>();

    public SymbolTable() {
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public IdentifiedObject check(String id) {
        IdentifiedObject object = symbolTable.get(id);
        if (object == null) {
            if (parent == null) {
                throw new IllegalArgumentException("未找到符号" + id);
            } else {
                return parent.check(id);
            }
        } else {
            return object;
        }
    }

    private void put(String id, IdentifiedObject object) {
        if (symbolTable.containsKey(id)) {
            throw new IllegalArgumentException("符号" + id + "已存在");
        }

        symbolTable.put(id, object);
        logger.trace("put symbol {} [{}]", id, object.getId());
    }

    public IdentifiedObject query(String id) {
        return symbolTable.get(id);
    }

    public void add(StateDesc desc) {
        put(id(desc), desc);
    }

    public StateDesc checkState(String namespace, String name) {
        return (StateDesc) check(stateId(namespace, name));
    }

    public BehaviorDesc checkBehavior(String namespace, String name) {
        return (BehaviorDesc) check(behaviorId(namespace, name));
    }

    public BehaviorModel checkModel(String namespace, String name) {
        return (BehaviorModel) check(modelId(namespace, name));
    }

    public static String modelId(String qualifiedName) {
        return "__model__" + qualifiedName;
    }

    public static String modelId(String namespace, String name) {
        return modelId(namespace + "." + name);
    }

    public static String stateId(String namespace, String name) {
        return "__state__" + namespace + "." + name;
    }

    public static String behaviorId(String namespace, String name) {
        return "__behavior__" + namespace + "." + name;
    }

    public static String outputId(String namespace, String name) {
        return "__output__" + namespace + "." + name;
    }

    public static String id(StateDesc state) {
        return stateId(state.getNameSpace(), state.getName());
    }

    public void add(BehaviorModel behaviorModel) {
        put(modelId(behaviorModel.getNameSpace(), behaviorModel.getName()), behaviorModel);
    }

    public void add(BehaviorDesc desc) {
        put(behaviorId(desc.getNameSpace(), desc.getName()), desc);
    }

    public StateDesc queryState(String namespace, String stateName) {
        return (StateDesc) query(stateId(namespace, stateName));
    }

    public BehaviorDesc queryBehavior(String namespace, String name) {
        return (BehaviorDesc) query(behaviorId(namespace, name));
    }

    public BehaviorModel queryModel(String qualifiedName) {
        return (BehaviorModel) query(modelId(qualifiedName));
    }

    public BehaviorModel queryModel(String namespace, String name) {
        return (BehaviorModel) query(modelId(namespace, name));
    }

    public void add(OutputDesc outputDesc) {
        put(outputId(outputDesc.getNameSpace(), outputDesc.getName()), outputDesc);
    }

    public Collection<String> names() {
        return symbolTable.keySet();
    }
}
