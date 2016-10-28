package lab.cadl.analysis.behavior.saf.demo;

import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.model.BehaviorModel;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.parser.saf.SafModelParser;
import lab.cadl.analysis.behavior.engine.processors.BehaviorProcessor;
import lab.cadl.analysis.behavior.engine.processors.StateProcessor;

import java.util.List;

public class ProcessBehaviors extends DemoProcessor {
    public static void main(String[] args) {
        new ProcessBehaviors().run();
    }

    @Override
    protected void process() {
        BehaviorModel model = parser.parse("net.app_proto.httpdemo");
        for (BehaviorDesc behavior : model.getBehaviors()) {
            System.out.println("=== process " + behavior + " ===");
            List<AnalysisInstance> instances = behaviorProcessor.process(behavior);
            instances.forEach(System.out::println);
            System.out.println("+++ count " + instances.size() + " +++");
        }
    }
}
