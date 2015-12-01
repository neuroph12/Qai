package qube.qai.data;

import java.util.*;

/**
 * Created by rainbird on 11/27/15.
 */
public class Arguments {

    private Set<String> argumentNames;

    private Map<String, Selector> arguments;

    public Arguments() {
        argumentNames = new HashSet<String>();
        arguments = new HashMap<String, Selector>();
    }

    public Arguments(String... names) {
        this();
        putNames(names);
    }
//    public void addSelector(String name, Selector selector) {
//        arguments.put(name, selector);
//    }

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
            return null;
        }
    }

    public Set<String> getArgumentNames() {
        return argumentNames;
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

        StringBuffer buffer = new StringBuffer();
        //buffer.append("uuid: ").append(uuid);
        for (String name : getArgumentNames()) {
            buffer.append(name).append(": ");
            if (getSelector(name) != null) {
                buffer.append(getSelector(name).getData()).append(", ");
            } else {
                buffer.append("null, ");
            }
        }
        // remove the last of the ", "
        buffer.deleteCharAt(buffer.length()-1);
        buffer.deleteCharAt(buffer.length()-1);
        return buffer.toString();
    }
}
