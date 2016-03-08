package lab.cadl.analysis.behavior.engine.model.attribute;

/**
 *
 */
public abstract class IndependentValue implements Value {
    @Override
    public final boolean isDependent() {
        return false;
    }
}
