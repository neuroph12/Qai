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
import org.slf4j.LoggerFactory;
import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.data.SelectionOperator;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.Name;
import qube.qai.parsers.antimirov.nodes.Node;
import qube.qai.procedure.analysis.*;
import qube.qai.procedure.archive.DirectoryIndexer;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.finance.StockEntityInitialization;
import qube.qai.procedure.finance.StockQuoteRetriever;
import qube.qai.procedure.nodes.ProcedureDescription;
import qube.qai.procedure.nodes.ProcedureInputs;
import qube.qai.procedure.nodes.ProcedureResults;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.procedure.utils.AttachProcedure;
import qube.qai.procedure.utils.SelectionProcedure;
import qube.qai.procedure.utils.SimpleProcedure;
import qube.qai.services.implementation.DataSelectorFactory;
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.User;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 11/27/15.
 */
public abstract class Procedure extends Node
        implements Serializable, Runnable, HazelcastInstanceAware, AcceptsVisitors, ProcedureConstants {

    public static String NAME = "Procedure";

    @Inject
    protected transient DataSelectorFactory selectorFactory;

    @Inject
    protected transient HazelcastInstance hazelcastInstance;

    protected User user;

    protected long duration;

    protected double progressPercentage;

    protected boolean hasExecuted = false;

    public Procedure() {
        super(new Name(NAME));
        if (uuid == null || uuid.length() == 0) {
            this.uuid = UUIDService.uuidString();
        }
        setFirstChild(new ProcedureDescription());
        buildArguments();
    }

    public Procedure(String name) {
        super(new Name(name));
        setFirstChild(new ProcedureDescription());
        buildArguments();
    }

    /**
     * All known implementing sub-classes
     *
     * @return
     */
    public static Collection<Class> knownSubClasses() {

        Collection<Class> classes = new ArrayList<>();

        // analysis
        classes.add(ChangePointAnalysis.class);
        classes.add(MarketNetworkBuilder.class);
        classes.add(MatrixStatistics.class);
        classes.add(NetworkStatistics.class);
        classes.add(NeuralNetworkAnalysis.class);
        classes.add(NeuralNetworkForwardPropagation.class);
        classes.add(SortingPercentilesProcedure.class);
        classes.add(TimeSequenceAnalysis.class);

        // archive
        classes.add(DirectoryIndexer.class);
        classes.add(WikiArchiveIndexer.class);

        // finance
        classes.add(StockEntityInitialization.class);
        classes.add(StockQuoteRetriever.class);

        // utils
        classes.add(AttachProcedure.class);
        classes.add(SelectionProcedure.class);
        classes.add(SimpleProcedure.class);

        return classes;
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

    protected void executeInputProcedures() {
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
    }

    public String getDescriptionText() {
        return ((ProcedureDescription) getFirstChild()).getDescription();
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
    protected abstract void buildArguments();

    protected SelectionOperator createSelector(Object data) {
        return selectorFactory.buildSelector(NAME, uuid, data);
    }

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
                // @TODO consider how you really want to do this...
                //procedures.put(uuid, this);
                info("procedure has been serialized in map with uuid: " + uuid);
            } else {
                error("no hazelcast-instance to add a copy to- procedure " + uuid + " has not been serialized...");
            }
        } catch (Exception e) {
            error("exception during execution of '" + uuid + "'. terminated...", e);
        }
    }

    private void sendMessage(String message) {
        if (hazelcastInstance != null) {
            ITopic itopic = hazelcastInstance.getTopic(uuid);
            itopic.publish(message);
            info("message has been successfully sent: " + message);
        } else {
            error("no hazelcast-instance to send ok message to");
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
