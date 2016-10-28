package lab.cadl.analysis.behavior.engine.processors;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.BehaviorInstance;
import lab.cadl.analysis.behavior.engine.model.BehaviorModel;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelProcessor {
    private BehaviorProcessor behaviorProcessor;

    public ModelProcessor(BehaviorProcessor behaviorProcessor) {
        this.behaviorProcessor = behaviorProcessor;
    }

    public List<Pair<OutputDesc, List<AnalysisInstance>>> process(BehaviorModel model) {
        return model.getOutputs().stream()
                .map(desc -> new ImmutablePair<>(desc, process(desc)))
                .collect(Collectors.toList());
    }

    public List<AnalysisInstance> process(OutputDesc output) {
        return behaviorProcessor.process(output.getBehavior());
    }
}
