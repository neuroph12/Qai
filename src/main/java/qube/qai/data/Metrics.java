package qube.qai.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rainbird on 12/4/15.
 */
public class Metrics {

    private Map<String, Double> metrics;

    public Metrics() {
        metrics = new HashMap<String, Double>();
    }

    public Set<String> getNames() {
        return metrics.keySet();
    }

    public Double getValue(String name) {
        return metrics.get(name);
    }

    public void putValue(String name, double value) {
        metrics.put(name, value);
    }


    public void putValue(String name, Double value) {
        metrics.put(name, value);
    }
}
