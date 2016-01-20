package qube.qai.services.implementation;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rainbird on 12/22/15.
 */
@Singleton
public class ProcedureRunner implements ProcedureRunnerInterface {

    private static Logger logger = LoggerFactory.getLogger("ExecutionService");

    public static final String SERVICE_NAME = "ProcedureRunnerService";

    @Inject
    private HazelcastInstance hazelcastInstance;

    private Map<String, ProcedureState> procedures;

    public ProcedureRunner() {
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
        ITopic itopic = hazelcastInstance.getTopic(uuid);
        itopic.addMessageListener(state);
        procedures.put(uuid, state);

        IExecutorService executor = hazelcastInstance.getExecutorService(SERVICE_NAME);
        executor.execute(procedure);

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
