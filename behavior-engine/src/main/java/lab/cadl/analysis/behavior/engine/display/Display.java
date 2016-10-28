package lab.cadl.analysis.behavior.engine.display;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.model.AnalysisDesc;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;

import java.util.List;

public interface Display {
    void display(OutputDesc desc, AnalysisInstance instance);

    default void display(OutputDesc desc, List<AnalysisInstance> instances) {
        for (AnalysisInstance instance : instances) {
            display(desc, instance);
        }
    }
}
