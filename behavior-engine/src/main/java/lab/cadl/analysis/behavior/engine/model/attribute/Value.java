package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public interface Value {
    boolean isDependent();
    default boolean isAssignment() {
        return false;
    }
}
