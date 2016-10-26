package lab.cadl.analysis.behavior.engine.model.behavior;

import java.util.List;

/**
 *
 */
public interface BehaviorNode {
    BehaviorNode parent();
    List<BehaviorNode> children();
    BehaviorNode setParent(BehaviorNode parent);

    <T> T accept(BehaviorNodeVisitor<T> visitor);
}
