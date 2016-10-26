package lab.cadl.analysis.behavior.engine.model.behavior;

public interface BehaviorNodeVisitor<T> {
    T visit(AlwaysBehaviorNode node);
    T visit(ConstraintBehaviorNode node);
    T visit(LogicalBehaviorNode node);
    T visit(NegationBehaviorNode node);
    T visit(RefBehaviorNode node);
    T visit(StateBehaviorNode node);
    T visit(TimeBehaviorNode node);
}
