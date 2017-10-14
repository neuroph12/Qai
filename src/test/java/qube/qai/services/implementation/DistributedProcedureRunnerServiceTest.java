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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import qube.qai.main.QaiConstants;
import qube.qai.main.QaiTestBase;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainbird on 1/11/16.
 */
public class DistributedProcedureRunnerServiceTest extends QaiTestBase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    public void testDistributedProcedures() throws Exception {

        DistributedProcedureListener procedureListener = new DistributedProcedureListener();
        injector.injectMembers(procedureListener);

        assertNotNull("initialization failed- no hazelcast-instance", procedureListener.hazelcastInstance);
        assertNotNull("initialization failed- no procedure-runner subclass", procedureListener.procedureRunner);

        List<String> uuidList = new ArrayList<String>();
        for (String name : ProcedureLibrary.getTemplateMap().keySet()) {
            Procedure procedure = ProcedureLibrary.getNamedProcedureTemplate(name).createProcedure();
            String uuid = procedure.getUuid();
            uuidList.add(uuid);
            logger.info("submitting procedure " + uuid);
            procedureListener.submitProcedure(procedure);
        }

        int procedureCount = 0;
        for (String uuid : uuidList) {
            ProcedureRunnerInterface.STATE state = procedureListener.queryState(uuid);
            logger.info("procedure uuid " + uuid + " has state " + state);
            if (ProcedureRunnerInterface.STATE.COMPLETE.equals(state)) {
                logger.info("procedure with uuid: '" + uuid + "' is already complete now checking results");
                IMap<String, Procedure> procedures = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
                Procedure procedure = procedures.get(uuid);
                assertNotNull("if actually done, there has to be a procedure", procedure);
                //assertTrue("procedure state must be right", procedure.hasExecuted());
                if (procedure != null) {
                    procedureCount++;
                }
            }
        }

        logger.info("of " + uuidList.size() + " procedures " + procedureCount + " are already accounted for");

        // and we try it another way as well
        int dryCount = 0;
        int endCount = 0;
        for (String uuid : uuidList) {
            IMap<String, Procedure> procedures = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
            Procedure procedure = procedures.get(uuid);
            if (procedure != null) {
                logger.info("procedure found in map?!? must have finished without telling...");
                dryCount++;
                if (procedure.hasExecuted()) {
                    endCount++;
                }
            }
        }

        logger.info("found " + dryCount + " of the procedures in map, " + endCount + " finished processing");

        // and yet another try- this time i will send the messages myself
        for (String uuid : uuidList) {
            ITopic itopic = hazelcastInstance.getTopic(uuid);
            logger.info("sending interrupted message to: " + uuid);
            itopic.publish(Procedure.PROCESS_INTERRUPTED);
        }

        // and now we really have to check the states
        for (String uuid : uuidList) {
            ProcedureRunnerInterface.STATE state = procedureListener.queryState(uuid);
            logger.info("procedure uuid " + uuid + " has state " + state);
            assertTrue("if messaging is working state has to be interrupted", ProcedureRunnerInterface.STATE.INTERRUPTED.equals(state));
        }
    }
}
