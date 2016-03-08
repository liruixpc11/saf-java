package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public abstract class DependentValue implements Value {
    @Override
    public final boolean isDependent() {
        return true;
    }
}
