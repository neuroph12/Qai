package qube.qai.services;

import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 12/16/15.
 */
public interface ProcedureSource {

    Procedure getProcedureWithName(String name);

    String[] getProcedureNames();
}
