package qube.qai.procedure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Selector;
import qube.qai.services.implementation.DataSelectorFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
/**
 * Created by rainbird on 11/27/15.
 */
public abstract class ProcedureChain extends Procedure {

    public static String INPUT_TIME_SEQUENCE = "time-sequence";

    public static String INPUT_NETWORK = "network";

    public static String INPUT_NEURAL_NETWORK = "neural-network";

    public static String INPUT_MATRIX = "matrix";

    public static String MATRIX_METRICS = "matrix metrics";

    public static String MATRIX_DATA_METRICS = "matrix data metrics";

    public static String NETWORK_METRICS = "network metrics";

    public static String TIME_SEQUENCE_METRICS = "time-sequence metrics";

    public static String FROM = "from";

    public static String CRITERIA = "criteria";

    public static String AVERAGE_TIME_SEQUENCE = "average time-sequence";

    public static String CHANGE_POINTS = "change points";

    public static String MAP_OF_TIME_SEQUENCE = "time-sequence map";

    public static String SORTED_ITEMS = "sorted items";

    public static String INPUT_NAMES = "input names";

    protected Logger logger = LoggerFactory.getLogger("Procedure Logger");

    protected boolean debug = true;

    protected Procedure parent;

    protected Collection<Procedure> children;

    public ProcedureChain() {
        super();
        children = new ArrayList<Procedure>();
    }

    public ProcedureChain(String name) {
        super(name);
        children = new ArrayList<Procedure>();
    }

    public ProcedureChain(String name, ProcedureChain parent) {
        this(name);
        this.parent = parent;
    }

    public ProcedureChain(ProcedureChain parent) {
        this();
        this.parent = parent;
    }

    /**
     * Visitor-pattern
     * @param visitor
     * @param data
     * @return
     */
    public Object childrenAccept(ProcedureVisitor visitor, Object data) {
        for (Procedure child : children) {
            child.accept(visitor, data);
        }
        return data;
    }

    public boolean haveChildren() {
        return !children.isEmpty();
    }

    /**
     * returns the first child with the name- names are not unique
     * if you need the unique child, use uuid instead
     * @param name
     * @return
     */
    public Procedure getChild(String name) {
        Procedure child = null;
        for (Procedure ch : children) {
            if (name.equals(ch.name)) {
                child = ch;
                break;
            }
        }
        return child;
    }

    /**
     * returns the child with the uuid
     * @param uuid
     * @return
     */
    public Procedure getChildWithUUID(String uuid) {
        Procedure child = null;
        for (Procedure ch : children) {
            if (uuid.equals(ch.uuid)) {
                child = ch;
                break;
            }
        }
        return child;
    }

    public void addChild(Procedure child) {
        child.setParent(this);
        children.add(child);
    }

    @Override
    public Procedure getParent() {
        return parent;
    }

    @Override
    public void setParent(Procedure parent) {
        this.parent = parent;
    }

    @Override
    public Collection<Procedure> getChildren() {
        return children;
    }

    @Override
    public void setChildren(Collection<Procedure> children) {
        this.children = children;
    }
}
