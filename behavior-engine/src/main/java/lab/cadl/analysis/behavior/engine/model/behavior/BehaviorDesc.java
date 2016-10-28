package lab.cadl.analysis.behavior.engine.model.behavior;

import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.IdentifiedObject;
import lab.cadl.analysis.behavior.engine.model.QualifiedName;

import java.util.Stack;

/**
 *
 */
public class BehaviorDesc extends IdentifiedObject implements AnalysisDesc {
    private BehaviorNode root;
    private boolean containsRecursive;

    public BehaviorDesc(QualifiedName qualifiedName) {
        super(qualifiedName);
    }

    public BehaviorDesc(QualifiedName qualifiedName, BehaviorNode root) {
        super(qualifiedName);
        setRoot(root);
    }

    public BehaviorNode getRoot() {
        return root;
    }

    public void setRoot(BehaviorNode root) {
        this.root = root;
        containsRecursive = this.root != null && checkRecursive();
    }

    private boolean checkRecursive() {
        return checkRecursive(new Stack<>(), this.root);
    }

    private boolean checkRecursive(Stack<BehaviorDesc> behaviorStack, BehaviorNode current) {
        if (current instanceof RefBehaviorNode) {
            RefBehaviorNode ref = (RefBehaviorNode) current;
            if (ref.isRecursive()) {
                return true;
            }

            behaviorStack.push(ref.getBehavior());
            if (checkRecursive(behaviorStack, ref.getBehavior().getRoot())) {
                return true;
            } else {
                behaviorStack.pop();
                return false;
            }
        } else {
            if (current.children() != null) {
                for (BehaviorNode node : current.children()) {
                    if (checkRecursive(behaviorStack, node)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean containsRecursive() {
        return containsRecursive;
    }

    @Override
    public String toString() {
        return getName() + " = " + root;
    }
}
