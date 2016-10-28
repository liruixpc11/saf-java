package lab.cadl.analysis.behavior.engine.model.constraint;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorNode;

import java.util.List;

/**
 *
 */
public interface BehaviorConstraint {
    List<AnalysisInstance> apply(BehaviorDesc desc, BehaviorNode node, List<AnalysisInstance> instances);
}
