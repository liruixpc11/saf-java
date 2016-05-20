package lab.cadl.analysis.behavior.engine.model;

/**
 *
 */
public class IdentifiedObject {
    private QualifiedName qualifiedName;
    private BehaviorModel model;

    public IdentifiedObject(QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public IdentifiedObject(String nameSpace, String name) {
        this.qualifiedName = new QualifiedName(nameSpace, name);
    }

    public IdentifiedObject() {
    }

    public QualifiedName getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getId() {
        return this.qualifiedName.getId();
    }

    public String getNameSpace() {
        return this.qualifiedName.getNameSpace();
    }

    public String getName() {
        return this.qualifiedName.getName();
    }

    public BehaviorModel getModel() {
        return model;
    }

    public void setModel(BehaviorModel model) {
        this.model = model;
    }
}
