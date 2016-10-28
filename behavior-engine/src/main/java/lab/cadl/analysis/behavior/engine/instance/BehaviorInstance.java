package lab.cadl.analysis.behavior.engine.instance;

import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorDesc;
import lab.cadl.analysis.behavior.engine.model.behavior.BehaviorNode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class BehaviorInstance implements AnalysisInstance<BehaviorDesc> {
    private BehaviorDesc desc;
    private BehaviorNode node;

    private List<AnalysisInstance> content;

    private Instant startTime;
    private Instant endTime;
    private long eventCount;

    public BehaviorInstance(BehaviorDesc desc, BehaviorNode node) {
        this.desc = desc;
        this.node = node;
        this.eventCount = 0;
    }

    public BehaviorInstance(BehaviorDesc desc, BehaviorNode node, List<AnalysisInstance> content) {
        this(desc, node);
        merge(content);
    }

    public BehaviorInstance(BehaviorDesc desc, BehaviorNode node, AnalysisInstance... instances) {
        this(desc, node, Arrays.asList(instances));
    }

    public BehaviorInstance merge(List<AnalysisInstance> instances) {
        if (content == null) {
            content = new ArrayList<>(instances);
        } else {
            content.addAll(instances);
        }

        update();
        return this;
    }

    private void update() {
        if (content != null) {
            content.sort((a, b) -> a.startTime().compareTo(b.startTime()));
            startTime = content.get(0).startTime();
            content.stream()
                    .map(AnalysisInstance::endTime)
                    .max(Instant::compareTo)
                    .ifPresent(c -> endTime = c);
            content.stream()
                    .map(AnalysisInstance::size)
                    .reduce((a, b) -> a + b)
                    .ifPresent(s -> eventCount = s);
        }
    }

    @Override
    public Instant startTime() {
        return startTime;
    }

    @Override
    public Instant endTime() {
        return endTime;
    }

    @Override
    public long size() {
        return content == null ? 0 : content.size();
    }

    public long eventCount() {
        return eventCount;
    }

    @Override
    public List<AnalysisInstance> content() {
        return content;
    }

    @Override
    public BehaviorDesc desc() {
        return desc;
    }

    public BehaviorNode node() {
        return node;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int indent) {
        String s = String.format("%s [%s, %s]\n", desc.getQualifiedName().toString(), startTime, endTime);
        for (AnalysisInstance child : content) {
            s += child.toString(indent + 1) + "\n";
        }

        return s;
    }
}
