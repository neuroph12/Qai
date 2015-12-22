package qube.qai.services;

import qube.qai.procedure.Procedure;

import java.util.Set;

/**
 * Created by rainbird on 12/22/15.
 */
public interface ProcedureRunnerInterface {

    enum STATE {COMPLETE, RUNNING, ERROR, INTERRUPTED};

    void submitProcedure(Procedure procedure);

    STATE queryState(String uuid);

    Set<String> getStartedProcedures();
}
