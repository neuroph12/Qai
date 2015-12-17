package qube.qai.procedure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.data.analysis.Statistics;
import qube.qai.services.implementation.DataSelectorFactory;
import qube.qai.services.implementation.UUIDService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
/**
 * Created by rainbird on 11/27/15.
 */
public abstract class ProcedureChain extends BaseProcedure {

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

    protected String uuid;

    protected String name;

    protected ProcedureChain parent;

    protected Collection<ProcedureChain> children;

    protected Arguments arguments;

    @Inject
    protected DataSelectorFactory selectorFactory;

    public ProcedureChain() {
        name = "AbstractProcedure";
        uuid = UUIDService.uuidString();
        children = new ArrayList<ProcedureChain>();
    }

    public ProcedureChain(String name) {
        this();
        this.name = name;
        buildArguments();
    }

    public ProcedureChain(String name, ProcedureChain parent) {
        this(name);
        this.parent = parent;
    }

    public ProcedureChain(ProcedureChain parent) {
        this();
        this.parent = parent;
    }

    protected void log(String message) {
        if (debug) {
            logger.info(message);
        }
    }

    /**
     * Visitor-pattern
     * @param visitor
     * @param data
     * @return
     */
    public Object childrenAccept(ProcedureVisitor visitor, Object data) {
        for (ProcedureChain child : children) {
            child.accept(visitor, data);
        }
        return data;
    }

    public boolean haveChildren() {
        return !children.isEmpty();
    }

    protected Selector createSelector(Object data) {
        return selectorFactory.buildSelector(name, uuid, data);
    }

    /**
     * each procesudre knows what inputs and outputs there will
     * be, and those have to be available in arguments-field
     * when the procedure is about to be called
     */
    public abstract void buildArguments();

    /**
     * returns the first child with the name- names are not unique
     * if you need the unique child, use uuid instead
     * @param name
     * @return
     */
    public ProcedureChain getChild(String name) {
        ProcedureChain child = null;
        for (ProcedureChain ch : children) {
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
    public ProcedureChain getChildWithUUID(String uuid) {
        ProcedureChain child = null;
        for (ProcedureChain ch : children) {
            if (uuid.equals(ch.uuid)) {
                child = ch;
                break;
            }
        }
        return child;
    }

    public void addChild(ProcedureChain child) {
        child.setParent(this);
        children.add(child);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(uuid.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProcedureChain) {
            ProcedureChain other = (ProcedureChain) obj;
            new EqualsBuilder().append(uuid, other.uuid).isEquals();
        }
        return false;
    }



    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    public ProcedureChain getParent() {
        return parent;
    }

    public void setParent(ProcedureChain parent) {
        this.parent = parent;
    }

    public Collection<ProcedureChain> getChildren() {
        return children;
    }

    public void setChildren(Collection<ProcedureChain> children) {
        this.children = children;
    }
}
