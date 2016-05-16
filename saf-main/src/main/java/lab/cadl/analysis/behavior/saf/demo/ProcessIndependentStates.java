package lab.cadl.analysis.behavior.saf.demo;

import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.event.repository.sqlite.SqliteEventRepository;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.model.BehaviorModel;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.engine.parser.FileRuleRepository;
import lab.cadl.analysis.behavior.engine.parser.RuleRepository;
import lab.cadl.analysis.behavior.engine.parser.saf.SafModelParser;
import lab.cadl.analysis.behavior.engine.processors.StateProcessor;

import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 *
 */
public class ProcessIndependentStates {
    public static void main(String[] args) throws SQLException {
        try (EventRepository repository = new SqliteEventRepository(Paths.get("data/sqlite/tcpudpdns_mix_20rec.sqlite").toString())) {
            RuleRepository ruleRepository = new FileRuleRepository(new File("behavior-engine/src/main/resources/lab/cadl/analysis/behavior/samples"));
            SafModelParser parser = new SafModelParser().setRuleRepository(ruleRepository);
            BehaviorModel model = parser.parse("bscripts.codered");

            AnalysisInstanceRegistry instanceRegistry = new AnalysisInstanceRegistry();
            SqliteEventRepository eventRepository = new SqliteEventRepository("data/sqlite/codered.1.sqlite");
            StateProcessor stateProcessor = new StateProcessor(eventRepository, instanceRegistry);

            System.out.println("=======");
            for (StateDesc stateDesc : model.getStates()) {
                if (!stateDesc.isDependent()) {
                    for (AnalysisInstance instance : stateProcessor.process(stateDesc)) {
                        System.out.println(instance);
                    }
                }
            }
        }
    }
}
