package lab.cadl.analysis.behavior.engine.parser.saf;

import org.antlr.v4.runtime.*;

/**
 *
 */
public class ErrorStrategy extends DefaultErrorStrategy {
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        throw new IllegalArgumentException(e);
    }
}
