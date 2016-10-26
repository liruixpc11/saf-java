package lab.cadl.analysis.behavior.engine.parser;

import java.io.InputStream;

/**
 *
 */
public interface RuleRepository {
    InputStream open(String qualifiedName);
}
