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
import lab.cadl.analysis.behavior.engine.processors.BehaviorProcessor;
import lab.cadl.analysis.behavior.engine.processors.ModelProcessor;
import lab.cadl.analysis.behavior.engine.processors.StateProcessor;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

abstract class DemoProcessor {
    SafModelParser parser;
    StateProcessor stateProcessor;
    BehaviorProcessor behaviorProcessor;
    ModelProcessor modelProcessor;

    protected String dbPath() {
        return "data/sqlite/httpflows_1077rec.sqlite";
    }

    protected abstract void process();

    void run() {
        try {
            try (EventRepository repository = new SqliteEventRepository(dbPath())) {
                RuleRepository ruleRepository = new FileRuleRepository(new File("behavior-engine/src/main/resources/lab/cadl/analysis/behavior/samples"));
                parser = new SafModelParser().setRuleRepository(ruleRepository);
                AnalysisInstanceRegistry instanceRegistry = new AnalysisInstanceRegistry();

                stateProcessor = new StateProcessor(repository, instanceRegistry);
                stateProcessor.setDefaultEventType("PACKET_TCP");
                behaviorProcessor = new BehaviorProcessor(stateProcessor, instanceRegistry);
                modelProcessor = new ModelProcessor(behaviorProcessor);

                process();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
