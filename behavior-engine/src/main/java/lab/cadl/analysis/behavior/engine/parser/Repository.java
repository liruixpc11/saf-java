package lab.cadl.analysis.behavior.engine.parser;

import java.io.InputStream;

/**
 *
 */
public interface Repository {
    InputStream open(String path);
}
