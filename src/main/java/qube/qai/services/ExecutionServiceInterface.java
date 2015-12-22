package qube.qai.services;

import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 12/22/15.
 */
public interface ExecutionServiceInterface {

    enum STATE {COMPLETE, RUNNING, ERROR, INTERRUPTED};

    void submitProcedure(Procedure procedure);

    STATE queryState(String uuid);
}
