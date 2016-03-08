package lab.cadl.analysis.behavior.engine.model.behavior;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.IdentifiedObject;
import lab.cadl.analysis.behavior.engine.model.QualifiedName;

/**
 *
 */
public class BehaviorDesc extends IdentifiedObject implements AnalysisDesc {
    private BehaviorNode root;

    public BehaviorDesc(QualifiedName qualifiedName, BehaviorNode root) {
        super(qualifiedName);
        this.root = root;
    }

    public BehaviorNode getRoot() {
        return root;
    }

    public void setRoot(BehaviorNode root) {
        this.root = root;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BehaviorDesc");
        sb.append("[").append(getId()).append("]{");
        sb.append("root=").append(root);
        sb.append('}');
        return sb.toString();
    }
}
