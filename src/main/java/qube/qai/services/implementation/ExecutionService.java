package qube.qai.services.implementation;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.Procedure;
import qube.qai.services.ExecutionServiceInterface;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 12/22/15.
 */
public class ExecutionService implements ExecutionServiceInterface {

    private static Logger logger = LoggerFactory.getLogger("ExecutionService");

    public static final String SERVICE_NAME = "QaiExecutorService";

    @Inject
    private HazelcastInstance hazelcastInstance;

    private Map<String, ProcedureState> procedures;

    public ExecutionService() {
        this.procedures = new HashMap<String, ProcedureState>();
    }

    public void submitProcedure(Procedure procedure) {

        // before we submit we note down the
        String uuid = procedure.getUuid();
        if (uuid == null) {
            uuid = UUIDService.uuidString();
            procedure.setUuid(uuid);
            logger.error("procedure without uuid- assigning new: " + uuid);
        }

        ProcedureState state = new ProcedureState(STATE.RUNNING);

        // out of some reason the map-store is asked to load the thing- and can't
        // this is therefore in order to try that out and see what happens if the procedure
        // has actually been added to the map... maybe then an update?
//        IMap<String,Procedure> procedureIMap = hazelcastInstance.getMap("PROCEDURES");
//        procedureIMap.put(uuid, procedure);

        ITopic itopic = hazelcastInstance.getTopic(uuid);
        itopic.addMessageListener(state);
        procedures.put(uuid, state);

        IExecutorService executor = hazelcastInstance.getExecutorService(SERVICE_NAME);
        executor.submit(procedure);
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
