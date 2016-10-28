package lab.cadl.analysis.behavior.saf.demo;

import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.event.repository.sqlite.SqliteEventRepository;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.BehaviorModel;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.engine.parser.FileRuleRepository;
import lab.cadl.analysis.behavior.engine.parser.RuleRepository;
import lab.cadl.analysis.behavior.engine.parser.saf.SafModelParser;
import lab.cadl.analysis.behavior.engine.processors.StateProcessor;

import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class ProcessStates extends DemoProcessor {
    public static void main(String[] args) {
        new ProcessStates().run();
    }

    @Override
    protected String dbPath() {
        return super.dbPath();
    }

    @Override
    protected void process() {
        BehaviorModel model = parser.parse("net.app_proto.httpdemo");
        for (StateDesc stateDesc : model.getStates()) {
            System.out.println("=======");
            System.out.println("processing state " + stateDesc);
            List<AnalysisInstance> states = stateProcessor.process(stateDesc);
            if (!states.isEmpty()) {
                AnalysisInstance instance = states.get(0);
                System.out.println(instance);
            }

            System.out.println("++");
            System.out.println("total events: " + states.size());
        }
    }
}
