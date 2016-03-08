package lab.cadl.analysis.behavior.engine.model;

/**
 *
 */
public class QualifiedName {
    private String nameSpace;
    private String name;

    public QualifiedName(String nameSpace, String name) {
        this.nameSpace = nameSpace;
        this.name = name;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return nameSpace + "." + name;
    }

    @Override
    public String toString() {
        return getId();
    }
}
