package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.BehaviorInstance;
import lab.cadl.analysis.behavior.engine.model.behavior.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BehaviorProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BehaviorProcessor.class);

    private StateProcessor stateProcessor;
    private AnalysisInstanceRegistry instanceRegistry;

    public BehaviorProcessor(StateProcessor stateProcessor, AnalysisInstanceRegistry instanceRegistry) {
        this.stateProcessor = stateProcessor;
        this.instanceRegistry = instanceRegistry;
    }

    public List<BehaviorInstance> process(BehaviorDesc desc) {
        return process(desc.getRoot());
    }

    private class Visitor implements BehaviorNodeVisitor<List<BehaviorInstance>> {
        @Override
        public List<BehaviorInstance> visit(AlwaysBehaviorNode node) {
            return null;
        }

        @Override
        public List<BehaviorInstance> visit(ConstraintBehaviorNode node) {
            return null;
        }

        @Override
        public List<BehaviorInstance> visit(LogicalBehaviorNode node) {
            return null;
        }

        @Override
        public List<BehaviorInstance> visit(NegationBehaviorNode node) {
            return null;
        }

        @Override
        public List<BehaviorInstance> visit(RefBehaviorNode node) {
            return null;
        }

        @Override
        public List<BehaviorInstance> visit(StateBehaviorNode node) {
            return null;
        }

        @Override
        public List<BehaviorInstance> visit(TimeBehaviorNode node) {
            return null;
        }
    }

    private List<BehaviorInstance> process(BehaviorNode root) {
        return root.accept(new Visitor());
    }
}
