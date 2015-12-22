package qube.qai.services.implementation;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.message.MessageListener;
import qube.qai.procedure.BaseProcedure;
import qube.qai.procedure.Procedure;
import qube.qai.services.ExecutionServiceInterface;
import qube.qai.services.ProcedureSource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainbird on 12/22/15.
 */
public class TestExecutionService extends QaiBaseTestCase {

    private Logger logger = LoggerFactory.getLogger("TestExecutionService");

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ExecutionServiceInterface executionService;

    @Inject
    private ProcedureSource procedureSource;

    public void testExecutionService() throws Exception {

        // create some procedures and see what happens
        List<String> uuidList = new ArrayList<String>();
        String[] procedureNames = procedureSource.getProcedureNames();
        for (String name : procedureNames) {
            Procedure procedure = procedureSource.getProcedureWithName(name);
            String uuid = procedure.getUuid();
            uuidList.add(uuid);
            executionService.submitProcedure(procedure);
        }

        logger.info("giving some time to the procedures to complete");
        Thread.sleep(5000);

        // now we want to see what happened with out procedures
        int procedureCount = 0;
        for (String uuid : uuidList) {
            ExecutionServiceInterface.STATE state = executionService.queryState(uuid);
            logger.info("procedure uuid " + uuid + " has state " + state);
            if (ExecutionServiceInterface.STATE.COMPLETE.equals(state)) {
                logger.info("procedure with uuid: '" + uuid + "' is already complete now checking results");
                IMap<String, Procedure> procedures = hazelcastInstance.getMap("PROCEDURES");
                Procedure procedure = procedures.get(uuid);
                assertNotNull("if actually done, there has to be a procedure", procedure);
                assertTrue("procedure state must be right", procedure.hasExecuted());
                procedureCount++;
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
            itopic.publish(BaseProcedure.PROCESS_INTERRUPTED);
        }

        // and now we really have to check the states
        for (String uuid : uuidList) {
            ExecutionServiceInterface.STATE state = executionService.queryState(uuid);
            logger.info("procedure uuid " + uuid + " has state " + state);
            assertTrue("if messaging is working state has to be interrupted", ExecutionServiceInterface.STATE.INTERRUPTED.equals(state));
        }
    }

    public void testBasicMessaging() throws Exception {

        MessageListener listener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("message received: " + message.getMessageObject());
            }
        };
        String uuid = UUIDService.uuidString();
        ITopic itopic = hazelcastInstance.getTopic(uuid);
        itopic.addMessageListener(listener);

        itopic.publish("talking with myself...");
    }
}
