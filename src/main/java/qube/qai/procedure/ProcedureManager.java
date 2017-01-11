package qube.qai.procedure;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Message;
import qube.qai.message.MessageListener;
import qube.qai.message.MessageQueue;
import qube.qai.services.implementation.UUIDService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 11/27/15.
 */
public class ProcedureManager extends MessageListener {

    private static String QAI_PROCEDURES= "QAI_PROCEDURES";

    @Inject
    private MessageQueue messageQueue;

    @Inject
    private UUIDService uuidService;

    @Inject @Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    private static ProcedureManager procedureManager;

    private Map<String, ProcedureWithState> procedures;

    private enum ProcedureStates {READY, STARTED, ENDED, ERROR}

    public static ProcedureManager getInstance() {
        if (procedureManager != null) {
            return procedureManager;
        }

        procedureManager = new ProcedureManager();

        return procedureManager;
    }

    public ProcedureManager() {
        procedures = new HashMap<String, ProcedureWithState>();
    }

    public String addProcedure(Procedure procedure) {
        String uuid = uuidService.createUUIDString();

        IExecutorService executorService = hazelcastInstance.getExecutorService(QAI_PROCEDURES);
        executorService.submit(procedure);

        procedures.put(uuid, new ProcedureWithState(ProcedureStates.READY, procedure));

        return uuid;
    }

    public void startProcedure(String uuid) {
        if (!procedures.containsKey(uuid)) {
            throw new IllegalArgumentException("Procedure: " + uuid + " has never been registered");
        }

        // send the message to the procedure to start running
        messageQueue.sendMessage(uuid, "Begin");
        procedures.get(uuid).setState(ProcedureStates.STARTED);

    }

    @Override
    public void onMessage(Message message) {
        //message.
    }

    class ProcedureWithState {

        ProcedureStates state;

        Procedure procedure;

        public ProcedureWithState(ProcedureStates state, Procedure procedure) {
            this.state = state;
            this.procedure = procedure;
        }

        public ProcedureStates getState() {
            return state;
        }

        public void setState(ProcedureStates state) {
            this.state = state;
        }

        public Procedure getProcedure() {
            return procedure;
        }

        public void setProcedure(Procedure procedure) {
            this.procedure = procedure;
        }
    }
}
