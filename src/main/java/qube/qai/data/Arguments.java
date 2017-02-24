package qube.qai.data;

import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rainbird on 11/27/15.
 */
public class Arguments implements Serializable, MetricTyped {

    private Map<String, Object> results;

    private Map<String, SelectionOperator> arguments;

    public Arguments() {
        arguments = new HashMap<String, SelectionOperator>();
        results = new HashMap<String, Object>();
    }

    public Arguments(String... names) {
        this();
        putArgumentNames(names);
    }

    public Arguments mergeArguments(Arguments toMerge) {

        for (String name : toMerge.getArgumentNames()) {
            arguments.put(name, toMerge.getArgument(name));
        }

        for (String name : toMerge.getResultNames()) {
            putResultNames(name);
        }

        return this;
    }

    public SelectionOperator<T> getArgument(String name) {
        if (arguments.containsKey(name)) {
            return arguments.get(name);
        }
        return null;
    }

    /**
     * result name must have been configured in advance to take a
     * result with the name, otherwise the result will be ignored
     *
     * @param name
     * @param result
     */
    public void addResult(String name, Object result) {
        results.put(name, result);
    }

    public Object getResult(String name) {
        return results.get(name);
    }

    public boolean hasResult(String name) {
        Object result = results.get(name);
        return result != null;
    }

    public void putResultNames(String... names) {
        for (String name : names) {
            results.put(name, null);
        }
    }

    public void putArgumentNames(String... names) {
        for (String name : names) {
            arguments.put(name, null);
        }
    }

    public void setArgument(String name, SelectionOperator value) {
        arguments.put(name, value);
    }

    public SelectionOperator getSelector(String name) {
        return arguments.get(name);
    }

    public Set<String> getArgumentNames() {
        return arguments.keySet();
    }

    public Set<String> getResultNames() {
        return results.keySet();
    }

    /**
     * checks whether all of the arguments have already
     * been assigned a value
     *
     * @return
     */
    public boolean isSatisfied() {

        boolean satisfied = false;

        for (String name : getArgumentNames()) {
            satisfied = hasArgumentValue(name);
            if (!satisfied) {
                break;
            }
        }

        return satisfied;
    }

    /**
     * checks whether the argument with the given name
     * has already been assigned a value
     *
     * @param name
     * @return
     */
    public boolean hasArgumentValue(String name) {
        if (arguments.get(name) != null) {
            return true;
        }
        return false;
    }

    public Metrics buildMetrics() {
        Metrics metrics = new Metrics();
        for (String name : getArgumentNames()) {
            double value = Double.NaN;
            if (hasArgumentValue(name)) {
                Object data = getSelector(name).getData();
                if (data instanceof Number) {
                    value = ((Number) data).doubleValue();
                }
            }

            metrics.putValue(name, value);
        }

        return metrics;
    }

    @Override
    public String toString() {

        boolean hasArguments = false;
        StringBuffer buffer = new StringBuffer();
        //buffer.append("uuid: ").append(uuid);
        for (String name : getArgumentNames()) {
            buffer.append(name).append(": ");
            if (hasArgumentValue(name)) {
                buffer.append(getSelector(name).getData()).append(", ");
            } else {
                buffer.append("null, ");
            }
            hasArguments = true;
        }

        // remove the last of the ", "
        if (hasArguments) {
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.deleteCharAt(buffer.length() - 1);
        }

        return buffer.toString();
    }
}
