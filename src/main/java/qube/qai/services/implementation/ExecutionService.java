package qube.qai.services.implementation;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.message.MessageQueueInterface;
import qube.qai.procedure.BaseProcedure;
import qube.qai.procedure.Procedure;
import qube.qai.services.ExecutionServiceInterface;
import qube.qai.services.UUIDServiceInterface;

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
    private UUIDServiceInterface uuidService;

    @Inject
    private HazelcastInstance hazelcastInstance;

    private Map<String, ProcedureState> procedures = new HashMap<String, ProcedureState>();

    public ExecutionService() {
    }

    public void submitProcedure(Procedure procedure) {

        // before we submit we note down the
        String uuid = procedure.getUuid();
        if (uuid == null) {
            uuid = uuidService.createUUIDString();
            procedure.setUuid(uuid);
        }

        ProcedureState state = new ProcedureState(STATE.RUNNING);

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
            if (BaseProcedure.PROCESS_ENDED.equals(content)) {
                state = STATE.COMPLETE;
            } else if (BaseProcedure.PROCESS_INTERRUPTED.equals(content)) {
                state = STATE.INTERRUPTED;
            } else if (BaseProcedure.PROCESS_ERROR.equals(content)) {
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
