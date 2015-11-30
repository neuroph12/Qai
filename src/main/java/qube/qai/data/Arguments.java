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

    public Arguments(Map<String, Selector> arguments) {
        this.arguments = arguments;
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
            throw new IllegalArgumentException("Argument list does not contain: " + name);
        }
    }

    public Set<String> getArgumentNames() {
        return arguments.keySet();
    }

    public boolean isSatisfied() {
        return arguments.keySet().containsAll(argumentNames);
    }

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
            buffer.append(name).append(": ").append(getSelector(name).getData()).append("\n");
        }
        return buffer.toString();
    }
}
