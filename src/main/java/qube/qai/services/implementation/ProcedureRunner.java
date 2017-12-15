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

package qube.qai.services.implementation;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.ProcedureEvent;
import qube.qai.services.ProcedureManagerInterface;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by rainbird on 12/22/15.
 */
@Singleton
public class ProcedureRunner implements ProcedureRunnerInterface, ProcEventHandlerInterface, QaiConstants, ProcedureConstants, MessageListener<ProcedureEvent> {

    private static Logger logger = LoggerFactory.getLogger("ProcedureRunner");

    private static String startMessageTemplate = "Start-request for procedure: '%s' with uuid: '%s' has been received";

    private static String unauthorizedMessageTemplate = "Procedure '%s' with '%s' does not have the required credentials for execution!";

    private static String procStartedMessageTemplate = "Procedure '%s' with uuid: '%s' has been started successfully...";

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
        String startMessage = String.format(startMessageTemplate,
                procedure.getProcedureName(), procedure.getUuid());
        logger.info(startMessage);

        String uuid = procedure.getUuid();
        if (uuid == null) {
            uuid = UUIDService.uuidString();
            procedure.setUuid(uuid);
            logger.error("procedure without uuid- assigning new: " + uuid);
        }

        if (!procedureManager.isProcedureAndUserAuthorized(procedure)) {
            String rightViolation = String.format(unauthorizedMessageTemplate,
                    procedure.getProcedureName(), procedure.getUuid());
            logger.info(rightViolation);
            return;
        }


        if (procedures.containsKey(uuid)) {
            procedures.replace(uuid, procedure);
        }

        IExecutorService executor = hazelcastInstance.getExecutorService(SERVICE_NAME);
        executor.execute(procedure);

        String allOK = String.format(procStartedMessageTemplate, procedure.getProcedureName(), procedure.getUuid());
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
        procedureManager.processEvent(procedure, event);
        //processEvent(procedure, event);

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
