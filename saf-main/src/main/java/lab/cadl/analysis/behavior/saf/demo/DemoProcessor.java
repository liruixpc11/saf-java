package lab.cadl.analysis.behavior.saf.demo;

import lab.cadl.analysis.behavior.engine.display.ConsoleTextDisplay;
import lab.cadl.analysis.behavior.engine.display.Display;
import lab.cadl.analysis.behavior.engine.event.EventRepository;
import lab.cadl.analysis.behavior.engine.event.repository.sqlite.SqliteEventRepository;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.AnalysisInstanceRegistry;
import lab.cadl.analysis.behavior.engine.model.BehaviorModel;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.engine.parser.FileRuleRepository;
import lab.cadl.analysis.behavior.engine.parser.RuleRepository;
import lab.cadl.analysis.behavior.engine.parser.saf.SafModelParser;
import lab.cadl.analysis.behavior.engine.processors.BehaviorProcessor;
import lab.cadl.analysis.behavior.engine.processors.ModelProcessor;
import lab.cadl.analysis.behavior.engine.processors.StateProcessor;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

abstract class DemoProcessor {
    SafModelParser parser;
    StateProcessor stateProcessor;
    BehaviorProcessor behaviorProcessor;
    ModelProcessor modelProcessor;
    PrintStream out = System.out;

    protected String dbPath() {
        return "data/sqlite/httpflows_1077rec.sqlite";
    }

    protected void process() {
        throw new IllegalStateException("未实现process()函数");
    }

    void run() {
        run(dbPath());
    }

    void run(String dbPath) {
        run(dbPath, this::process);
    }

    void run(String dbPath, Runnable runnable) {
        try {
            try (EventRepository repository = new SqliteEventRepository(dbPath)) {
                RuleRepository ruleRepository = new FileRuleRepository(new File("behavior-engine/src/main/resources/lab/cadl/analysis/behavior/samples"));
                parser = new SafModelParser().setRuleRepository(ruleRepository);
                AnalysisInstanceRegistry instanceRegistry = new AnalysisInstanceRegistry();

                stateProcessor = new StateProcessor(repository, instanceRegistry);
                stateProcessor.setDefaultEventType("PACKET_TCP");
                behaviorProcessor = new BehaviorProcessor(stateProcessor, instanceRegistry);
                modelProcessor = new ModelProcessor(behaviorProcessor);

                runnable.run();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void run(String dbPath, String... models) {
        run(dbPath, () -> Arrays.stream(models).forEach(this::processModel));
    }

    void processModel(String modelPath) {
        BehaviorModel model = parser.parse(modelPath);
        println("=== model parsed ===");
        println(model);

        Display display = new ConsoleTextDisplay();
        for (Pair<OutputDesc, List<AnalysisInstance>> entry : modelProcessor.process(model)) {
            display.display(entry.getLeft(), entry.getRight());
            println("+++ count " + entry.getRight().size() + " +++");
        }
    }

    void println(Object o) {
        out.println(o);
    }

    void println() {
        out.println();
    }
}
