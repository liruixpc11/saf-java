package lab.cadl.analysis.behavior.engine.display;

import lab.cadl.analysis.behavior.engine.instance.AnalysisInstance;
import lab.cadl.analysis.behavior.engine.instance.BehaviorInstance;
import lab.cadl.analysis.behavior.engine.instance.StateInstance;
import lab.cadl.analysis.behavior.engine.model.output.OutputDesc;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;

public class ConsoleTextDisplay implements Display {
    private PrintStream out;

    public ConsoleTextDisplay() {
        this(System.out);
    }

    public ConsoleTextDisplay(PrintStream out) {
        this.out = out;
    }

    @Override
    public void display(OutputDesc desc, AnalysisInstance instance) {
        display(desc, instance, 0);
    }

    private void display(OutputDesc desc, AnalysisInstance instance, int level) {
        if (instance instanceof StateInstance) {
            StateInstance state = (StateInstance) instance;
            String formatString = "%s {" + StringUtils.join(desc.getFields().stream().map(f -> f + "=%s").toArray(), ", ") + "}";
            Object[] args = new Object[desc.getFields().size() + 1];
            args[0] = state.desc().getQualifiedName().toString();
            for (int i = 0; i < desc.getFields().size(); i++) {
                args[i + 1] = state.resolve(desc.getFields().get(i));
            }

            o(level, formatString, args);
        } else if (instance instanceof BehaviorInstance) {
            BehaviorInstance behavior = (BehaviorInstance) instance;
            o(level, "%s [%s, %s]", desc.getQualifiedName().toString(), instance.startTime(), instance.endTime());
            for (AnalysisInstance child : behavior.content()) {
                display(desc, child, level + 1);
            }
        } else {
            throw new IllegalArgumentException("不支持该实例类型: " + instance.getClass().getSimpleName());
        }
    }

    private void o(int level, String formatString, Object ...objects) {
        String s = "";
        for (int i = 0; i < level; i++) {
            s += "  ";
        }

        s += String.format(formatString, objects);
        out.println(s);
    }
}
