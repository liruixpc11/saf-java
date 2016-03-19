package lab.cadl.analysis.behavior.saf.demo;

import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.event.repository.sqlite.SqliteEventRepository;
import lab.cadl.analysis.behavior.engine.model.BehaviorModel;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.engine.parser.FileRuleRepository;
import lab.cadl.analysis.behavior.engine.parser.RuleRepository;
import lab.cadl.analysis.behavior.engine.parser.saf.SafModelParser;

import java.io.File;
import java.sql.SQLException;

/**
 *
 */
public class ProcessIndependentStates {
    public static void main(String[] args) throws SQLException {
        try (EventRepository repository = new SqliteEventRepository("/Users/lirui/IdeaProjects/analysis-parent/data/sqlite/tcpudpdns_mix_20rec.sqlite")) {
            RuleRepository ruleRepository = new FileRuleRepository(new File("/Users/lirui/IdeaProjects/analysis-parent/behavior-engine/src/main/resources/lab/cadl/analysis/behavior/samples"));
            SafModelParser parser = new SafModelParser().setRuleRepository(ruleRepository);
            BehaviorModel model = parser.parse("bscripts.ft_constraints_1");

            System.out.println("=======");
            for (StateDesc stateDesc : model.getStates()) {
                if (!stateDesc.isDependent()) {
                    System.out.println(stateDesc);
                }
            }
        }
    }
}
