package qube.qai.procedure;

import com.hazelcast.core.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.message.MessageListener;
import qube.qai.message.MessageQueue;
import qube.qai.services.implementation.DataSelectorFactory;
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.User;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by rainbird on 11/27/15.
 */
public abstract class Procedure implements Serializable, Runnable, HazelcastInstanceAware {

    protected static Logger logger = LoggerFactory.getLogger("Procedure");

    public static final String PROCEDURES = "PROCEDURES";

    public final static String PROCESS_ENDED = "PROCESS_ENDED";

    public final static String PROCESS_INTERRUPTED = "PROCESS_INTERRUPTED";

    public final static String PROCESS_ERROR = "PROCESS_ERROR";

    @Inject
    protected transient DataSelectorFactory selectorFactory;

    protected transient HazelcastInstance hazelcastInstance;

    protected User user;

    protected String uuid;

    protected String name;

    protected String description;

    protected long duration;

    protected double progressPercentage;

    protected boolean hasExecuted = false;

    protected Arguments arguments;

    public Procedure() {
        uuid = UUIDService.uuidString();
        buildArguments();
    }

    public Procedure(String name) {
        this();
        this.name = name;
    }

    /**
     * this is what procedures do after all
     */
    public abstract void execute();

    /**
     * each procesudre knows what inputs and outputs there will
     * be, and those have to be available in arguments-field
     * when the procedure is about to be called
     */
    public abstract void buildArguments();

    /**
     * Visitor-pattern
     * @param visitor
     * @param data
     * @return
     */
    public Object accept(ProcedureVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    protected Selector createSelector(Object data) {
        return selectorFactory.buildSelector(name, uuid, data);
    }

    /**
     * Visitor-pattern
     * @param visitor
     * @param data
     * @return
     */
    public Object childrenAccept(ProcedureVisitor visitor, Object data) {
        // nothing to do, simply return the data
        return data;
    }

    public final void run() {
        long start = System.currentTimeMillis();
        logger.info("Procedure " + getName() + " has been started, uuid: " + uuid);
        execute();
        duration = System.currentTimeMillis() - start;
        hasExecuted  = true;
        logger.info("Procedure " + getName() + " has been ended normally in " + duration + "ms");
        sendMessageOK();

        // and the hat-trick now
        if (hazelcastInstance != null) {
            IMap procedures = hazelcastInstance.getMap(PROCEDURES);
            procedures.put(uuid, this);
            logger.info("procedure has been serialized in map with uuid: " + uuid);
        } else {
            logger.error("no hazelcast-instance to add a copy to- procedure " + uuid + " has not been serialized...");
        }

    }

    private void sendMessage(String message) {
        if (hazelcastInstance != null) {
            ITopic itopic = hazelcastInstance.getTopic(uuid);
            itopic.publish(message);
            logger.info("message has been successfully sent: " + message);
        } else {
            logger.error("no hazelcast-instance to send ok message to");
        }
    }

    protected void sendMessageOK() {
        sendMessage(PROCESS_ENDED);
    }

    protected void sendMessageError(String message) {
        sendMessage(PROCESS_ERROR);
    }

    protected void sendMessageInterrupted() {
        sendMessage(PROCESS_INTERRUPTED);
    }



    public boolean haveChildren() {
        return false;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    public boolean hasExecuted() {
        return hasExecuted;
    }

    public void hasExecuted(boolean hasExecuted) {
        this.hasExecuted = hasExecuted;
    }

    public Procedure getParent() {
        return null;
    }

    public void setParent(Procedure parent) {

    }

    public Collection<Procedure> getChildren() {
        return null;
    }

    public void setChildren(Collection<Procedure> children) {

    }
}
