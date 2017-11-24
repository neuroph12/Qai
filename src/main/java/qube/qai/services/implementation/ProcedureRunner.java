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

package qube.qai.services.implementation;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.ProcedureEvent;
import qube.qai.procedure.event.ProcedureEnded;
import qube.qai.procedure.event.ProcedureError;
import qube.qai.procedure.event.ProcedureInterrupted;
import qube.qai.procedure.event.ProcedureStarted;
import qube.qai.services.ProcedureManagerInterface;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by rainbird on 12/22/15.
 */
@Singleton
public class ProcedureRunner implements ProcedureRunnerInterface, QaiConstants, ProcedureConstants, MessageListener<ProcedureEvent> {

    private static Logger logger = LoggerFactory.getLogger("ProcedureRunner");



    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureManagerInterface procedureManager;

    private IMap<String, Procedure> procedures;

    private ITopic<ProcedureEvent> itopic;

    public ProcedureRunner() {
    }

    public void init() {

        if (hazelcastInstance == null) {
            throw new IllegalStateException("There has to be a HazelcastInstance available- aborting.");
        }

        procedures = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
        itopic = hazelcastInstance.getTopic(TOPIC_NAME);
        itopic.addMessageListener(this);

    }

    public void submitProcedure(Procedure procedure) {

        // before we submit we make note of the incoming request
        String startMessage = String.format("Start-request for procedure: '%s' with uuid: '%s' has been received",
                procedure.getProcedureName(), procedure.getUuid());
        logger.info(startMessage);

        String uuid = procedure.getUuid();
        if (uuid == null) {
            uuid = UUIDService.uuidString();
            procedure.setUuid(uuid);
            logger.error("procedure without uuid- assigning new: " + uuid);
        }

        if (!procedureManager.isProcedureAndUserAuthorized(procedure)) {
            String rightViolation = String.format("Procedure '%s' with '%s' does not have the required credentials for execution!",
                    procedure.getProcedureName(), procedure.getUuid());
            logger.info(rightViolation);
            return;
        }


        if (procedures.containsKey(uuid)) {
            procedures.replace(uuid, procedure);
        }

        IExecutorService executor = hazelcastInstance.getExecutorService(SERVICE_NAME);
        executor.execute(procedure);

        String allOK = String.format("Procedure '%s' with uuid: '%s' has been started successfully...", procedure.getProcedureName(), procedure.getUuid());
        logger.info(allOK);

    }

    @Override
    public void onMessage(Message<ProcedureEvent> message) {
        ProcedureEvent event = message.getMessageObject();

        String uuid = event.ofProcedure();
        if (!procedures.containsKey(uuid)) {
            logger.error("Unaccounted procedure has been detected! uuid: " + uuid);
            return;
        }

        Procedure procedure = procedures.get(uuid);
        processEvent(procedure, event);

    }

    public void processEvent(Procedure procedure, ProcedureInterrupted interrupted) {
        procedure.setState(ProcedureState.INTERRUPTED);
        logger.info("procedure uuid: " + interrupted.ofProcedure() + " has been interrupted");
    }

    public void processEvent(Procedure procedure, ProcedureError error) {
        procedure.setState(ProcedureConstants.ProcedureState.ERROR);
        logger.error("procedure uuid: " + error.ofProcedure() + " with error: " + error.getMessage());
    }

    public void processEvent(Procedure procedure, ProcedureStarted started) {
        procedure.setState(ProcedureConstants.ProcedureState.STARTED);
        logger.info("procedure uuid: " + started.ofProcedure() + " has been started");
    }

    public void processEvent(Procedure procedure, ProcedureEnded ended) {
        procedure.setState(ProcedureConstants.ProcedureState.ENDED);
        logger.info("procedure uuid: " + ended.ofProcedure() + " ended");
    }

    private void processEvent(Procedure procedure, ProcedureEvent event) {
        // should never be coming here
        logger.info("procedure uuid: " + event.ofProcedure() + " ended up in the wrong method call... how is this possible!?!");
    }

    public Set<String> getStartedProcedures() {
        return procedures.keySet();
    }

    @Override
    public ProcedureState queryState(String uuid) {

        Procedure procedure = procedures.get(uuid);
        if (procedure == null) {
            logger.error("procedure uuid: " + uuid + "' is ot registered");
            throw new IllegalStateException("procedure with uuid: '" + uuid + "' is not registered");

        }
        return procedure.getState();
    }
}
