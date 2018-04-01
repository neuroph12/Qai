/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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
import org.slf4j.LoggerFactory;
import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.data.SelectionOperator;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.ConcatenationNode;
import qube.qai.parsers.antimirov.nodes.EmptyNode;
import qube.qai.parsers.antimirov.nodes.Name;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.procedure.event.ProcedureEnded;
import qube.qai.procedure.event.ProcedureError;
import qube.qai.procedure.event.ProcedureInterrupted;
import qube.qai.procedure.nodes.ProcedureDescription;
import qube.qai.procedure.nodes.ProcedureInputs;
import qube.qai.procedure.nodes.ProcedureResults;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.implementation.DataSelectorFactory;
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.User;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by rainbird on 11/27/15.
 */
public abstract class Procedure extends ConcatenationNode
        implements Serializable, Runnable, HazelcastInstanceAware, AcceptsVisitors, ProcedureConstants {

    public static String NAME = "Procedure";

    public static String DESCRIPTION = "Description of the procedure";

    @Inject
    protected transient DataSelectorFactory selectorFactory;

    @Inject
    protected QaiDataProvider<User> userProvider;

    @Inject
    protected transient HazelcastInstance hazelcastInstance;

    //protected User user;
    protected String userUUID;

    protected long duration;

    protected double progressPercentage;

    protected boolean hasExecuted = false;

    protected ProcedureState state = ProcedureState.TEMPLATE;

    public Procedure() {
        super(new ProcedureDescription(), new EmptyNode());
        this.name = new Name(NAME);
        this.uuid = UUIDService.uuidString();
        buildArguments();
    }

    public Procedure(String name) {
        this();
        this.name = new Name(name);
    }

    /**
     * this is what procedures do after all
     */
    public abstract void execute();

    public abstract Procedure createInstance();

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
    protected abstract void buildArguments();

    public final void run() {
        try {
            long start = System.currentTimeMillis();
            info("Procedure " + getName() + " has been started, uuid: " + uuid);
            execute();
            duration = System.currentTimeMillis() - start;
            hasExecuted = true;
            info("Procedure " + getName() + " has been ended normally in " + duration + "ms");
            sendMessageOK();

            // and the hat-trick now
            if (hazelcastInstance != null) {
                IMap procedures = hazelcastInstance.getMap(PROCEDURES);
                if (procedures.get(uuid) != null) {
                    procedures.replace(uuid, this);
                }
                info("procedure has been serialized in map with uuid: " + uuid);
            } else {
                error("no hazelcast-instance to add a copy to- procedure " + uuid + " has not been serialized...");
            }
        } catch (Exception e) {
            hasExecuted = true;
            String message = "exception during execution of '" + uuid + "'. executed with error...";
            sendMessageError(message);
            error(message, e);
        }
    }

    private void sendMessage(ProcedureEvent event) {
        if (hazelcastInstance != null) {
            ITopic<ProcedureEvent> itopic = hazelcastInstance.getTopic(ProcedureRunnerInterface.TOPIC_NAME);
            itopic.publish(event);
            info("messageOf: '" + event.ofState() + "' event has been successfully sent");
        } else {
            error("no hazelcast-instance to send ok message to");
        }
    }

    protected void sendMessageOK() {
        sendMessage(new ProcedureEnded(uuid));
    }

    protected void sendMessageError(String message) {
        sendMessage(new ProcedureError(uuid, message));
    }

    protected void sendMessageInterrupted() {
        sendMessage(new ProcedureInterrupted(uuid));
    }


    protected Object getInputValueOf(String name) {
        Object value = null;
        ValueNode nameNode = getProcedureInputs().getNamedInput(name);
        if (nameNode != null) {
            value = nameNode.getValue();
        }
        return value;
    }

    protected void setResultValueOf(String name, Object value) {
        BaseNode nameNode = getProcedureResults().getNamedResult(name);
        if (nameNode == null) {
            throw new IllegalArgumentException("No result value with name: '" + name + "'");
        }
        nameNode.setFirstChild(new ValueNode(name, value));
    }

    /*protected void executeInputProcedures() {
        Collection<String> names = getProcedureInputs().getInputNames();
        for (String name : names) {
            ValueNode node = getProcedureInputs().getNamedInput(name);
            BaseNode child = node.getFirstChild();
            if (child != null && child instanceof Procedure) {
                Procedure procedure = (Procedure) child;
                if (!procedure.hasExecuted()) {
                    procedure.execute();
                }
            }
        }
    }*/

    public String getDescriptionText() {
        return ((ProcedureDescription) getFirstChild()).getDescription();
    }

    public ProcedureInputs getProcedureInputs() {
        return (ProcedureInputs) getFirstChild().getFirstChild();
    }

    public ProcedureResults getProcedureResults() {
        return (ProcedureResults) getFirstChild().getSecondChild();
    }

    protected SelectionOperator createSelector(Object data) {
        return selectorFactory.buildSelector(NAME, uuid, data);
    }


    public boolean haveChildren() {
        return true;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public boolean equals(BaseNode obj) {
        if (obj instanceof Procedure) {
            Procedure other = (Procedure) obj;
            if (uuid.equals(other.uuid)) {
                return true;
            }
        }
        return false;
    }

    public ProcedureDescription getProcedureDescription() {
        return (ProcedureDescription) getFirstChild();
    }

    public static String getNAME() {
        return NAME;
    }

    public static void setNAME(String name) {
        Procedure.NAME = name;
    }

    public static String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public static void setDESCRIPTION(String description) {
        Procedure.DESCRIPTION = description;
    }

    public User getUser() {
        return userProvider.getData(userUUID);
    }

    public void setUser(User user) {
        this.userUUID = user.getUuid();
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
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

    protected void info(String message) {
        LoggerFactory.getLogger("Procedure").info(message);
    }

    protected void error(String message) {
        LoggerFactory.getLogger("Procedure").error(message);
    }

    protected void error(String message, Exception e) {
        LoggerFactory.getLogger("Procedure").error(message, e);
    }

    protected void debug(String message) {
        LoggerFactory.getLogger("Procedure").debug(message);
    }

    public String getProcedureName() {
        return this.getName().getName();
    }

    public String getUserName() {
        if (userUUID != null) {
            return userProvider.getData().getUsername();
        } else {
            return "n/a";
        }
    }

    public ProcedureState getState() {
        return state;
    }

    public void setState(ProcedureState state) {
        this.state = state;
    }

}
