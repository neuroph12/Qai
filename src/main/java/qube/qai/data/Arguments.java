package qube.qai.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by rainbird on 11/27/15.
 */
public class Arguments {

    private Set<String> argumentNames;

    private Set<String> resultNames;

    private Map<String, Object> results;

    private Map<String, Selector> arguments;

    public Arguments() {
        argumentNames = new HashSet<String>();
        resultNames = new HashSet<String>();
        arguments = new HashMap<String, Selector>();
    }

    public Arguments(String... names) {
        this();
        putNames(names);
    }

    /**
     * result name must have been configured in advance to take a
     * result with the name, otherwise the result will be ignored
     * @param name
     * @param result
     */
    public void addResult(String name, Object result) {
        if (resultNames.contains(name)) {
            results.put(name, result);
        }
    }

    public boolean hasResult(String name) {
        Object result = results.get(name);
        return result != null;
    }

    public void putResultNames(String... names) {
        for (String name : names) {
            resultNames.add(name);
        }
    }

    public void putNames(String... names) {
        for (String name : names) {
            argumentNames.add(name);
        }
    }

    public void setArgument(String name, Selector value) {
        if (argumentNames.contains(name)) {
            arguments.put(name, value);
        } else {
            throw new IllegalArgumentException("Argument list does not contain: " + name);
        }
    }

    public Selector getSelector(String name) {
        if (argumentNames.contains(name) && arguments.containsKey(name)) {
            return arguments.get(name);
        } else {
            //throw new IllegalArgumentException("Argument list does not contain: " + name);
            // forget about the exception, just return null
            return null;
        }
    }

    public Set<String> getArgumentNames() {
        return argumentNames;
    }

    public Set<String> getResultNames() {
        return resultNames;
    }

    public void setResultNames(Set<String> resultNames) {
        this.resultNames = resultNames;
    }

    /**
     * checks whether all of the arguments have already
     * been assigned a value
     * @return
     */
    public boolean isSatisfied() {

        boolean satisfied = false;

        for (String name : getArgumentNames()) {
            satisfied = hasValue(name);
            if (!satisfied) {
                break;
            }
        }

        return satisfied;
    }

    /**
     * checks whether the argument with the given name
     * has already been assigned a value
     * @param name
     * @return
     */
    public boolean hasValue(String name) {

        if (argumentNames.contains(name)) {
            if (arguments.get(name) != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {

        boolean hasArguments = false;
        StringBuffer buffer = new StringBuffer();
        //buffer.append("uuid: ").append(uuid);
        for (String name : getArgumentNames()) {
            buffer.append(name).append(": ");
            if (hasValue(name)) {
                buffer.append(getSelector(name).getData()).append(", ");
            } else {
                buffer.append("null, ");
            }
            hasArguments = true;
        }

        // remove the last of the ", "
        if (hasArguments) {
            buffer.deleteCharAt(buffer.length()-1);
            buffer.deleteCharAt(buffer.length()-1);
        }

        return buffer.toString();
    }
}
