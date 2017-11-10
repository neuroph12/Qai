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
import qube.qai.security.QaiSecurity;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.user.Permission;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rainbird on 12/22/15.
 */
@Singleton
public class ProcedureRunner implements ProcedureRunnerInterface {

    private static Logger logger = LoggerFactory.getLogger("ProcedureRunner");

    public static final String SERVICE_NAME = "ProcedureRunner";

    public static Permission CAN_EXECUTE = new Permission("Can Execute");

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private QaiSecurity security;

    private Map<String, ProcedureState> procedures;

    public ProcedureRunner() {
        this.procedures = new HashMap<String, ProcedureState>();
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

        User user = procedure.getUser();
        if (!security.hasPermission(user, CAN_EXECUTE)) {
            String rightViolation = String.format("User: '%s' does not have the required crenential for running procedure '%s', execution interrupted!",
                    user, procedure.getProcedureName());
            logger.info(rightViolation);
            return;
        }

        ProcedureState state = new ProcedureState(STATE.RUNNING);
        ITopic itopic = hazelcastInstance.getTopic(uuid);
        itopic.addMessageListener(state);
        procedures.put(uuid, state);

        IMap<String, Procedure> procMap = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
        if (procMap.get(uuid) == null) {
            procMap.put(uuid, procedure);
        }

        IExecutorService executor = hazelcastInstance.getExecutorService(SERVICE_NAME);
        executor.execute(procedure);

        String allOK = String.format("Procedure '%s' with uuid: '%s' has been started successfully...", procedure.getProcedureName(), procedure.getUuid());
        logger.info(allOK);

    }

    public Set<String> getStartedProcedures() {
        return procedures.keySet();
    }

    public STATE queryState(String uuid) {
        ProcedureState state = procedures.get(uuid);
        if (state != null) {
            return state.getState();
        }
        return null;
    }

    class ProcedureState implements MessageListener {

        public STATE state;
        public String message;

        public ProcedureState(STATE state) {
            this.state = state;
        }

        public void onMessage(Message message) {
            String content = (String) message.getMessageObject();
            if (Procedure.PROCESS_ENDED.equals(content)) {
                state = STATE.COMPLETE;
            } else if (Procedure.PROCESS_INTERRUPTED.equals(content)) {
                state = STATE.INTERRUPTED;
            } else if (Procedure.PROCESS_ERROR.equals(content)) {
                state = STATE.ERROR;
            }
            logger.info("message received: " + content);
        }

        public STATE getState() {
            return state;
        }

        public void setState(STATE state) {
            this.state = state;
        }
    }
}
