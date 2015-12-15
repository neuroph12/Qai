package qube.qai.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rainbird on 12/4/15.
 */
public class Metrics implements MetricTyped {

    private Map<String, Object> metrics;

    public Metrics() {
        metrics = new HashMap<String, Object>();
    }

    public Set<String> getNames() {
        return metrics.keySet();
    }

    public Object getValue(String name) {
        return metrics.get(name);
    }

    public void putValue(String name, double value) {
        metrics.put(name, value);
    }

    public void putValue(String name, Object value) {
        metrics.put(name, value);
    }

    public Metrics buildMetrics() {
        return this;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        for (String name : metrics.keySet()) {
            buffer.append("[");
            buffer.append(name).append(": ");
            Object value = metrics.get(name);
            buffer.append(value).append("]");
        }

        return buffer.toString();
    }
}
