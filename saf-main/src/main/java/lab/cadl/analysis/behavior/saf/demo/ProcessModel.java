package lab.cadl.analysis.behavior.saf.demo;

import lab.cadl.analysis.behavior.engine.display.ConsoleTextDisplay;
import lab.cadl.analysis.behavior.engine.display.Display;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.model.BehaviorModel;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ProcessModel extends DemoProcessor {
    public static void main(String[] args) {
        new ProcessModel().run();
    }

    @Override
    protected void process() {
        BehaviorModel model = parser.parse("net.app_proto.httpdemo");
        System.out.println("=== model parsed ===");
        System.out.println(model);

        Display display = new ConsoleTextDisplay();
        for (Pair<OutputDesc, List<AnalysisInstance>> entry : modelProcessor.process(model)) {
            display.display(entry.getLeft(), entry.getRight());
            System.out.println("+++ count " + entry.getRight().size() + " +++");
        }

    }
}
