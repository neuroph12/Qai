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

import org.openrdf.annotations.Iri;
import org.slf4j.LoggerFactory;
import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
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
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.User;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import static qube.qai.main.QaiConstants.BASE_URL;

/**
 * Created by zenpunk on 11/27/15.
 */
@Iri(BASE_URL + "Procedure")
public abstract class Procedure extends ConcatenationNode
        implements Serializable, Runnable, AcceptsVisitors, ProcedureConstants {

    @Iri(BASE_URL + "uuid")
    private String uuid;

    @Iri(BASE_URL + "NAME")
    public String NAME = "Procedure";

    @Iri(BASE_URL + "DESCRIPTION")
    public String DESCRIPTION = "Description of the procedure";

    @Iri(BASE_URL + "inputs")
    protected QaiDataProvider[] inputs;

    @Iri(BASE_URL + "userUUID")
    protected String userUUID;

    protected long duration;

    protected double progressPercentage;

    @Iri(BASE_URL + "hasExecuted")
    protected boolean hasExecuted = false;

    @Iri(BASE_URL + "state")
    protected ProcedureState state = ProcedureState.TEMPLATE;

    public Procedure() {
        super(new ProcedureDescription(), new EmptyNode());
        this.name = new Name(NAME);
        this.uuid = UUIDService.uuidString();
        //this.inputs = new ArrayList<>();
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

    public void run() {
        try {
            long start = System.currentTimeMillis();
            info("Procedure " + getName() + " has been started, uuid: " + uuid);
            execute();
            duration = System.currentTimeMillis() - start;
            if (!hasExecuted) {
                hasExecuted = true;
            }
            info("Procedure " + getName() + " has been ended normally in " + duration + "ms");
            sendMessageOK();

            // and the hat-trick now
            /*if (hazelcastInstance != null) {
                IMap procedures = hazelcastInstance.getMap(PROCEDURES);
                if (procedures.get(uuid) != null) {
                    procedures.replace(uuid, this);
                }
                info("procedure has been serialized in map with uuid: " + uuid);
            } else {
                error("no hazelcast-instance to add a copy to- procedure " + uuid + " has not been serialized...");
            }*/
        } catch (Exception e) {
            hasExecuted = true;
            String message = "exception during execution of '" + uuid + "'. executed with error...";
            sendMessageError(message);
            error(message, e);
        }
    }

    private void sendMessage(ProcedureEvent event) {
        /*if (hazelcastInstance != null) {
            ITopic<ProcedureEvent> itopic = hazelcastInstance.getTopic(ProcedureRunnerInterface.TOPIC_NAME);
            itopic.publish(event);
            info("messageOf: '" + event.ofState() + "' event has been successfully sent");
        } else {
            error("no hazelcast-instance to send ok message to");
        }*/
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

    /*protected void setResultValueOf(String name, Object value) {
        BaseNode nameNode = getProcedureResults().getNamedResult(name);
        if (nameNode == null) {
            throw new IllegalArgumentException("No result value with name: '" + name + "'");
        }
        nameNode.setFirstChild(new ValueNode(name, value));
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

    public boolean haveChildren() {
        return true;
    }

    public void addInputs(QaiDataProvider... providers) {

        if (providers == null) {
            return;
        } else {
            inputs = providers;
        }
    }

    /*public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }*/

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

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String name) {
        NAME = name;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String description) {
        DESCRIPTION = description;
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

    public ProcedureState getState() {
        return state;
    }

    public void setState(ProcedureState state) {
        this.state = state;
    }

    public Collection<QaiDataProvider> getInputs() {
        if (inputs != null) {
            return Arrays.asList(inputs);
        }
        return null;
    }

    public void setInputs(Collection<QaiDataProvider> inputcollections) {
        if (inputcollections != null) {
            this.inputs = new QaiDataProvider[inputcollections.size()];
            inputcollections.toArray(this.inputs);
        }
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public void setUser(User user) {
        if (user != null) {
            this.userUUID = user.getUuid();
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
