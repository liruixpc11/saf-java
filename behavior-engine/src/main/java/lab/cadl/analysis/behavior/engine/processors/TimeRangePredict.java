package lab.cadl.analysis.behavior.engine.processors;

@FunctionalInterface
interface TimeRangePredict {
    boolean test(long ls, long le, long rs, long re);
}
