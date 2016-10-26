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
public class ProcessStates {
    public static void main(String[] args) throws SQLException {
        try (EventRepository repository = new SqliteEventRepository(Paths.get("data/sqlite/tcpudpdns_mix_20rec.sqlite").toString())) {
            RuleRepository ruleRepository = new FileRuleRepository(new File("behavior-engine/src/main/resources/lab/cadl/analysis/behavior/samples"));
            SafModelParser parser = new SafModelParser().setRuleRepository(ruleRepository);
//            BehaviorModel model = parser.parse("bscripts.codered");
            BehaviorModel model = parser.parse("net.app_proto.httpdemo");

            AnalysisInstanceRegistry instanceRegistry = new AnalysisInstanceRegistry();
            SqliteEventRepository eventRepository = new SqliteEventRepository("data/sqlite/httpflows_1077rec.sqlite");
            StateProcessor stateProcessor = new StateProcessor(eventRepository, instanceRegistry);
            stateProcessor.setDefaultEventType("PACKET_TCP");

            for (StateDesc stateDesc : model.getStates()) {
                System.out.println("=======");
                System.out.println("processing state " + stateDesc);
                List<StateInstance> states = stateProcessor.process(stateDesc);
//                for (AnalysisInstance instance : states) {
//                    System.out.println(instance);
//                }
                if (!states.isEmpty()) {
                    StateInstance instance = states.get(0);
                    System.out.println(instance);
                }

                System.out.println("++");
                System.out.println("total events: " + states.size());
            }
        }
    }
}
