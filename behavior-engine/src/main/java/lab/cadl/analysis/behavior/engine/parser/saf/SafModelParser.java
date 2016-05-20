package lab.cadl.analysis.behavior.engine.parser.saf;

import ca.szc.configparser.Ini;
import lab.cadl.analysis.behavior.engine.model.*;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import lab.cadl.analysis.behavior.engine.model.state.StateDesc;
import lab.cadl.analysis.behavior.engine.parser.FileRuleRepository;
import lab.cadl.analysis.behavior.engine.parser.ModelParser;
import lab.cadl.analysis.behavior.engine.parser.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 *
 */
public class SafModelParser implements ModelParser {
    private static final Logger logger = LoggerFactory.getLogger(SafModelParser.class);
    public static final String HEADER_SECTION = "header";
    public static final String STATES_SECTION = "states";
    public static final String BEHAVIOR_SECTION = "behavior";
    public static final String OUTPUT_SECTION = "model";

    private StateLineParser stateLineParser = new StateLineParser();
    private BehaviorLineParser behaviorLineParser = new BehaviorLineParser();
    private OutputLineParser outputLineParser = new OutputLineParser();

    private SymbolTable symbolTable;
    private RuleRepository ruleRepository = new FileRuleRepository();

    public SafModelParser() {
        symbolTable = new SymbolTable();
    }

    public SafModelParser(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public SafModelParser setRuleRepository(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
        return this;
    }

    @Override
    public BehaviorModel parse(String path) {
        logger.debug("parsing model {}", path);
        try {
            return parseImport(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public BehaviorModel parse(InputStream inputStream) {
        try {
            BehaviorModel model = new BehaviorModel(symbolTable);
            Ini ini = new Ini();
            ini.setAllowInterpolation(false);
            ini.setIgnoreCase(false);
            Map<String, Map<String, String>> sections = ini.read(new BufferedReader(new InputStreamReader(inputStream))).getSections();
            parseHeader(model, checkEntry(sections, HEADER_SECTION));
            if (symbolTable.queryModel(model.getName()) == null) {
                parseStates(model, checkEntry(sections, STATES_SECTION));
                parseBehavior(model, checkEntry(sections, BEHAVIOR_SECTION));
                parseOutput(model, checkEntry(sections, OUTPUT_SECTION));
                symbolTable.add(model);
                return model;
            } else {
                logger.info("模型" + model.getId() + "已经加载，不再重复加载");
                return symbolTable.checkModel(model.getName());
            }

        } catch (IOException | BackingStoreException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void parseOutput(BehaviorModel model, Map<String, String> outputs) throws IOException {
        for (String header : outputs.keySet()) {
            try (InputStream inputStream = new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8))) {
                OutputDesc outputDesc = outputLineParser.parse(model.getName(), inputStream);

                String behaviorString = outputs.get(header);
                try (InputStream behaviorStream  = new ByteArrayInputStream(behaviorString.getBytes(StandardCharsets.UTF_8))) {
                    BehaviorDesc behaviorDesc = behaviorLineParser.parse(outputDesc.getQualifiedName(), model.getSymbolTable(), behaviorStream);
                    outputDesc.setBehavior(behaviorDesc);
                }

                symbolTable.add(outputDesc);
                model.addOutput(outputDesc);
            }
        }
    }

    private void parseStates(BehaviorModel model, Map<String, String> states) throws BackingStoreException, IOException {
        for (String name : states.keySet()) {
            String expr = states.get(name);
            try (InputStream inputStream = new ByteArrayInputStream(expr.getBytes(StandardCharsets.UTF_8))) {
                StateDesc state = stateLineParser.parse(new QualifiedName(model.getName(), name), model.getSymbolTable(), inputStream);
                model.addState(state);
            }
        }
    }

    private void parseBehavior(BehaviorModel model, Map<String, String> behaviors) throws BackingStoreException, IOException {
        for (String name : behaviors.keySet()) {
            String expr = behaviors.get(name);
            try (InputStream inputStream = new ByteArrayInputStream(expr.getBytes(StandardCharsets.UTF_8))) {
                BehaviorDesc behavior = behaviorLineParser.parse(new QualifiedName(model.getName(), name), model.getSymbolTable(), inputStream);
                model.addBehavior(behavior);
            }
        }
    }

    private void parseHeader(BehaviorModel model, Map<String, String> headers) throws IOException {
        String namespace = checkEntry(headers, "NAMESPACE");
        String name = checkEntry(headers, "NAME");
        model.setQualifiedName(new QualifiedName(namespace, name));

        String qualifierString = checkEntry(headers, "QUALIFIER");
        try (InputStream inputStream = new ByteArrayInputStream(qualifierString.getBytes(StandardCharsets.UTF_8))) {
            model.setQualifier(stateLineParser.parse(new QualifiedName(name, name + "_qualifier"), symbolTable, inputStream));
        }

        String importString = headers.get("IMPORT");
        if (importString != null) {
            for (String aImport : importString.split(",")) {
                model.getImportStrings().add(aImport.trim());
                parseImport(aImport);
            }
        }
    }

    private <T> T checkEntry(Map<String, T> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            throw new IllegalArgumentException("不存在键值为" + String.valueOf(key) + "的项");
        }
    }

    private BehaviorModel parseImport(String path) throws IOException {
        try (InputStream inputStream = ruleRepository.open(path)) {
            return parse(inputStream);
        }
    }

    public static void main(String[] args) {
        RuleRepository ruleRepository = new FileRuleRepository(new File("/Users/lirui/IdeaProjects/analysis-parent/behavior-engine/src/main/resources/lab/cadl/analysis/behavior/samples"));
        SafModelParser parser = new SafModelParser().setRuleRepository(ruleRepository);
        parse(parser, "usermodels.doshussain03");
        parse(parser, "net.attacks.dnskaminsky");
        parse(parser, "net.attacks.simpleportscan");
        parse(parser, "bscripts.example_states");
    }

    private static void parse(SafModelParser parser, String path) {
        BehaviorModel model = parser.parse(path);
        System.out.println("\n====");
        System.out.println(model);
        for (StateDesc state : model.getStates()) {
            System.out.println(state);
        }

        for (BehaviorDesc behavior : model.getBehaviors()) {
            System.out.println(behavior);
        }

        for (OutputDesc output : model.getOutputs()) {
            System.out.println(output);
        }

        System.out.println("----\n");
    }
}
