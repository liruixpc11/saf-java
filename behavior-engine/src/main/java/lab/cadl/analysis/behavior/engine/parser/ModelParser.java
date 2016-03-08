package lab.cadl.analysis.behavior.engine.parser;

import lab.cadl.analysis.behavior.engine.model.BehaviorModel;

import java.io.InputStream;

/**
 *
 */
public interface ModelParser {
    BehaviorModel parse(String path);
    BehaviorModel parse(InputStream inputStream);
}
