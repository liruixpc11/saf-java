package lab.cadl.analysis.behavior.engine.parser.saf;

import lab.cadl.analysis.behavior.engine.model.QualifiedName;
import lab.cadl.analysis.behavior.engine.model.SymbolTable;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import lab.cadl.analysis.behavior.grammar.BehaviorBaseVisitor;
import lab.cadl.analysis.behavior.grammar.BehaviorLexer;
import lab.cadl.analysis.behavior.grammar.BehaviorParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class OutputLineParser {
    private static final Logger logger = LoggerFactory.getLogger(OutputLineParser.class);

    private static class Visitor extends BehaviorBaseVisitor<OutputDesc> {
        private String namespace;

        public Visitor(String namespace) {
            this.namespace = namespace;
        }

        @Override
        public OutputDesc visitModelOutput(BehaviorParser.ModelOutputContext ctx) {
            String name = ctx.modelId().getText();
            List<String> fields = ctx.ID().stream().map(ParseTree::getText).collect(Collectors.toList());
            return new OutputDesc(new QualifiedName(namespace, name), fields);
        }
    }

    public OutputDesc parse(String namespace, InputStream inputStream) throws IOException {
        logger.debug("parsing output for {}", namespace);
        BehaviorLexer lexer = new BehaviorLexer(new ANTLRInputStream(inputStream));
        BehaviorParser parser = new BehaviorParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new ErrorStrategy());

        Visitor visitor = new Visitor(namespace);
        OutputDesc desc = visitor.visit(parser.modelOutput());

        if (desc == null) {
            throw new IllegalArgumentException("parsing output for " + namespace + " failed");
        } else {
            return desc;
        }
    }
}
