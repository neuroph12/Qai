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
import qube.qai.main.QaiTestBase;
import qube.qai.message.MessageListener;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.MatrixStatistics;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.ProcedureSourceInterface;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainbird on 12/22/15.
 */
public class TestProcedureRunnerService extends QaiTestBase {

    private Logger logger = LoggerFactory.getLogger("TestExecutionService");

    @Inject //@Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    @Inject
    private ProcedureSourceInterface procedureSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // we do in the meanwhile yet another experiment
        TestEventListener listener = new TestEventListener();
        hazelcastInstance.addDistributedObjectListener(listener);
    }

    public void restProcedureExecution() throws Exception {

        // pick a procedure... any procedure
        Procedure procedure = procedureSource.getProcedureWithName(MatrixStatistics.NAME);
        String uuid = procedure.getUuid();
        logger.info("starting procedure: " + uuid);

        ITopic itopic = hazelcastInstance.getTopic(uuid);
        itopic.addMessageListener(new com.hazelcast.core.MessageListener() {
            public void onMessage(Message message) {
                logger.info("HALLELUJAH: " + message.getMessageObject());
            }
        });

        procedureRunner.submitProcedure(procedure);

        // now we wait some
        Thread.sleep(5000);

        // ok, we are now assuming that something might have happened
        ProcedureRunnerInterface.STATE state = procedureRunner.queryState(uuid);
        logger.info("procedure state is: " + state);
        assertNotNull("there has to be at least a state", state);
        assertTrue("this usually works", ProcedureRunnerInterface.STATE.COMPLETE.equals(state));

        // we then try to read the procedure from map
        IMap<String, Procedure> procedureMap = hazelcastInstance.getMap("PROCEDURES");
        Procedure stored = procedureMap.get(uuid);
        // @TODO this is not ok... have to figure a way out
        logger.info("stored procedure: " + stored);
        //assertNotNull("there has to be a copy of procedure somewhere", stored);

        // now changing the state manually...
        itopic.publish(Procedure.PROCESS_INTERRUPTED);
        Thread.sleep(1000); // maybe if we wait a bit...
        state = procedureRunner.queryState(uuid);
        logger.info("after changing the state has become: " + state);
        assertTrue("state has been changed to interrupted", ProcedureRunnerInterface.STATE.INTERRUPTED.equals(state));
    }

    public void testExecutionService() throws Exception {

        // create some procedures and see what happens
        List<String> uuidList = new ArrayList<String>();
        String[] procedureNames = procedureSource.getProcedureNames();
        for (String name : procedureNames) {
            Procedure procedure = procedureSource.getProcedureWithName(name);
            String uuid = procedure.getUuid();
            uuidList.add(uuid);
            logger.info("submitting procedure " + uuid);
            procedureRunner.submitProcedure(procedure);
        }

        // now we want to see what happened with out procedures
        int procedureCount = 0;
        for (String uuid : uuidList) {
            ProcedureRunnerInterface.STATE state = procedureRunner.queryState(uuid);
            logger.info("procedure uuid " + uuid + " has state " + state);
            if (ProcedureRunnerInterface.STATE.COMPLETE.equals(state)) {
                logger.info("procedure with uuid: '" + uuid + "' is already complete now checking results");
                IMap<String, Procedure> procedures = hazelcastInstance.getMap("PROCEDURES");
                Procedure procedure = procedures.get(uuid);
                // @TODO this is a point, you know...
                //assertNotNull("if actually done, there has to be a procedure", procedure);
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
            IMap<String, Procedure> procedures = hazelcastInstance.getMap("PROCEDURES");
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
            ProcedureRunnerInterface.STATE state = procedureRunner.queryState(uuid);
            logger.info("procedure uuid " + uuid + " has state " + state);
            assertTrue("if messaging is working state has to be interrupted", ProcedureRunnerInterface.STATE.INTERRUPTED.equals(state));
        }
    }

    public void restBasicMessaging() throws Exception {

        MessageListener listener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("message received: " + message.getMessageObject());
            }
        };
        String uuid = UUIDService.uuidString();
        ITopic itopic = hazelcastInstance.getReliableTopic(uuid);
        itopic.addMessageListener(listener);

        itopic.publish("talking with myself...");
    }

    /**
     * well, this is interesting to see happening, really
     * and gives me ideas as to how i could make use of it,
     * or better, it is interesting to play around with
     */
    class TestEventListener implements DistributedObjectListener {

        public void distributedObjectCreated(DistributedObjectEvent distributedObjectEvent) {

            DistributedObjectEvent.EventType eventType = distributedObjectEvent.getEventType();

            Object id = distributedObjectEvent.getObjectId();
            String name = distributedObjectEvent.getDistributedObject().getClass().getName();
            String message = "received event: " + eventType + " created: '" + name + "' with id: " + id;
            logger.info(message);

        }

        public void distributedObjectDestroyed(DistributedObjectEvent distributedObjectEvent) {

            DistributedObjectEvent.EventType eventType = distributedObjectEvent.getEventType();

            Object id = distributedObjectEvent.getObjectId();
            String message = "received event: " + eventType + " for object with id: " + id;
            logger.info(message);
        }
    }
}
