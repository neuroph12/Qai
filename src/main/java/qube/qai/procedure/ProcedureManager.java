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

import com.hazelcast.core.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.message.QaiMessageListener;
import qube.qai.services.ProcedureManagerInterface;
import qube.qai.services.implementation.UUIDService;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/27/15.
 */
public class ProcedureManager extends QaiMessageListener implements ProcedureManagerInterface {

    private Logger logger = LoggerFactory.getLogger("ProcedureMaanager");

    @Inject
    private UUIDService uuidService;

    @Inject
    private HazelcastInstance hazelcastInstance;

    private static ProcedureManager procedureManager;

    private IMap<String, Procedure> procedures;

    private IExecutorService executorService;

    private ITopic<String> topic;

    private String messageSubmitted = "Procedure %s with uuid: '%s' is ready to be started";

    private String messageStarted = "Procedure %s with uuid: '%s' has been started";

    public ProcedureManager(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public String registerProcedure(Procedure procedure) {

        String uuid = procedure.getUuid();
        // this should never really happen- just to make sure
        if (StringUtils.isBlank(uuid)) {
            uuid = uuidService.createUUIDString();
            procedure.setUuid(uuid);
        }

        procedures = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
        // check is there is already a copy in the map, put one if neccessary
        if (!procedures.containsKey(uuid)) {
            procedures.put(uuid, procedure);
        }

        executorService.submit(procedure);

        procedure.setState(Procedure.ProcedureState.READY);
        procedures.replace(uuid, procedure);

        logger.info(String.format(messageSubmitted, procedure.getProcedureName(), procedure.getUuid()));

        return uuid;
    }

    /*@Override
    public void startProcedure(String uuid) {

        if (!procedures.containsKey(uuid)) {
            throw new IllegalArgumentException("Procedure: " + uuid + " has never been registered");
        }

        // submit the procedure for execution
        Procedure procedure = procedures.get(uuid);
        procedure.setState(ProcedureConstants.ProcedureState.STARTED);
        procedures.replace(uuid, procedure);

        executorService.executeOnAllMembers(procedure);

        // log submission of the procedure
        String message = String.format(messageStarted, procedure.getProcedureName(), procedure.getUuid());
        logger.info(message);
        topic.publish(message);

    }*/

    @Override
    public boolean isProcedureAndUserAuthorized(Procedure procedure) {
        return false;
    }

    @Override
    public void onMessage(Message message) {
        //message.
    }

    /**
     * Singleton instance of the class
     *
     * @return procedureManager
     */
    public static ProcedureManager getInstance(HazelcastInstance hazelcastInstance) {

        if (procedureManager != null) {
            return procedureManager;
        }

        procedureManager = new ProcedureManager(hazelcastInstance);
        procedureManager.initialize();

        return procedureManager;
    }

    @Override
    public void initialize() {
        if (hazelcastInstance == null) {
            throw new IllegalStateException("No HazealcastInstance to work with- aborting");
        }
        procedures = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
        executorService = hazelcastInstance.getExecutorService(procedureTopicName);
        topic = hazelcastInstance.getTopic(QaiConstants.PROCEDURES);
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
