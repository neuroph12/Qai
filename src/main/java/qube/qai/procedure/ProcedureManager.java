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
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.procedure.event.ProcedureEnded;
import qube.qai.procedure.event.ProcedureError;
import qube.qai.procedure.event.ProcedureInterrupted;
import qube.qai.procedure.event.ProcedureStarted;
import qube.qai.services.ProcedureManagerInterface;
import qube.qai.services.implementation.UUIDService;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/27/15.
 */
public class ProcedureManager implements ProcedureManagerInterface {

    private Logger logger = LoggerFactory.getLogger("ProcedureMaanager");

    private String messageSubmitted = "Procedure '%s' with uuid: '%s' is ready to be started";

    private String messageStarted = "Procedure '%s' with uuid: '%s' has been started";

    private String messageInterrupted = "Procedure '%s' with uuid: '%s' has been interrupted!";

    private String messageError = "Procedure '%s' with uuid: '%s' stopped with error: '%s'";

    private String messageEnded = "Procedure '%s' with uuid: '%s' has ended successfully";

    @Inject
    private UUIDService uuidService;

    @Inject
    private HazelcastInstance hazelcastInstance;

    private static ProcedureManager procedureManager;

    private IMap<String, Procedure> procedures;

    /**
     * ProcedureManager is responsible for the security of the procedures and follows their states
     * deciding what should be done about them and all that.
     *
     * @param hazelcastInstance
     */
    @Inject
    public ProcedureManager(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.procedures = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
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

        procedure.setState(Procedure.ProcedureState.READY);
        procedures.replace(uuid, procedure);

        logger.info(String.format(messageSubmitted, procedure.getProcedureName(), procedure.getUuid()));

        return uuid;
    }

    @Override
    public boolean isProcedureAndUserAuthorized(Procedure procedure) {
        return false;
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureInterrupted interrupted) {
        procedure.setState(ProcedureConstants.ProcedureState.INTERRUPTED);
        procedures.replace(procedure.getUuid(), procedure);
        logger.info(String.format(messageInterrupted, procedure.getProcedureName(), interrupted.ofProcedure()));
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureError error) {
        procedure.setState(ProcedureConstants.ProcedureState.ERROR);
        procedures.replace(procedure.getUuid(), procedure);
        logger.error(String.format(messageError, procedure.getProcedureName(), error.ofProcedure(), error.getMessage()));
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureStarted started) {
        procedure.setState(ProcedureConstants.ProcedureState.STARTED);
        procedures.replace(procedure.getUuid(), procedure);
        logger.info(String.format(messageStarted, procedure.getProcedureName(), started.ofProcedure()));
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureEnded ended) {
        procedure.setState(ProcedureConstants.ProcedureState.ENDED);
        procedures.replace(procedure.getUuid(), procedure);
        logger.info(String.format(messageEnded, procedure.getProcedureName(), ended.ofProcedure()));
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureEvent event) {
        // should never be coming here
        logger.info("procedure uuid: " + event.ofProcedure() + " ended up in the wrong method call... how is this possible!?!");
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

        return procedureManager;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
