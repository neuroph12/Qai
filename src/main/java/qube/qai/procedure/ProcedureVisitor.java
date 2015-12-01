package qube.qai.procedure;

import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 12/1/15.
 */
public interface ProcedureVisitor {

    public Object visit(Procedure chain, Object data);
}
