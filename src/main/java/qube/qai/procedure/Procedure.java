/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.procedure;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.AcceptsVisitors;
import qube.qai.data.Arguments;
import qube.qai.data.DataVisitor;
import qube.qai.data.SelectionOperator;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.Name;
import qube.qai.parsers.antimirov.nodes.Node;
import qube.qai.services.implementation.DataSelectorFactory;
import qube.qai.user.User;
import thewebsemantic.Namespace;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by rainbird on 11/27/15.
 */
@Namespace("http://www.qoan.org/procedures#")
public abstract class Procedure extends Node
        implements Serializable, Runnable, HazelcastInstanceAware, AcceptsVisitors {

    protected static Logger logger = LoggerFactory.getLogger("Procedure");

    public static String NAME = "Procedure";

    public static final String PROCEDURES = "PROCEDURES";

    public final static String PROCESS_ENDED = "PROCESS_ENDED";

    public final static String PROCESS_INTERRUPTED = "PROCESS_INTERRUPTED";

    public final static String PROCESS_ERROR = "PROCESS_ERROR";

    @Inject
    protected transient DataSelectorFactory selectorFactory;

    @Inject
    protected transient HazelcastInstance hazelcastInstance;

    protected User user;

    protected String description;

    protected long duration;

    protected double progressPercentage;

    protected boolean hasExecuted = false;

    protected Arguments arguments;

    public Procedure() {
        super(new Name(NAME));
        setFirstChild(new ProcedureDescription(description));
        buildArguments();
    }

    public Procedure(String name) {
        super(new Name(name));
        setFirstChild(new ProcedureDescription(description));
        buildArguments();
    }

    public Procedure(String name, BaseNode child) {
        this(name);
        getProcedureInputs().addInput(child);
    }

    public ProcedureInputs getProcedureInputs() {
        return (ProcedureInputs) getFirstChild().getFirstChild();
    }

    public ProcedureResults getProcedureResults() {
        return (ProcedureResults) getFirstChild().getSecondChild();
    }

    /**
     * this is what procedures do after all
     */
    public abstract void execute();

    /**
     * hook for visitors
     */
    public Object accept(DataVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * each procedure knows what inputs and outputs there will
     * be, and those have to be available in arguments-field
     * when the procedure is about to be called
     */
    public abstract void buildArguments();

    protected SelectionOperator createSelector(Object data) {
        return selectorFactory.buildSelector(NAME, uuid, data);
    }

    public final void run() {
        try {
            long start = System.currentTimeMillis();
            logger.info("Procedure " + getName() + " has been started, uuid: " + uuid);
            execute();
            duration = System.currentTimeMillis() - start;
            hasExecuted = true;
            logger.info("Procedure " + getName() + " has been ended normally in " + duration + "ms");
            sendMessageOK();

            // and the hat-trick now
            if (hazelcastInstance != null) {
                IMap procedures = hazelcastInstance.getMap(PROCEDURES);
                // @TODO consider how you really want to do this...
                //procedures.put(uuid, this);
                logger.info("procedure has been serialized in map with uuid: " + uuid);
            } else {
                logger.error("no hazelcast-instance to add a copy to- procedure " + uuid + " has not been serialized...");
            }
        } catch (Exception e) {
            logger.error("exception during execution of '" + uuid + "'. terminated...", e);
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
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Procedure) {
            Procedure other = (Procedure) obj;
            new EqualsBuilder().append(uuid, other.uuid).isEquals();
        }
        return false;
    }

    /**
     * so that we can actually remind the guy that this is the id which it needs seek
     *
     * @return
     */
    //@Override
    //@thewebsemantic.Id
//    public String getUuid() {
//        return this.uuid;
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @thewebsemantic.Id
    public abstract String getUuid();

}
