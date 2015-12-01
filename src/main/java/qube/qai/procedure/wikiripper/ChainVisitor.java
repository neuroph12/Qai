package qube.qai.procedure.wikiripper;

import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 12/1/15.
 */
public interface ChainVisitor {

    public Object visit(Procedure chain, Object data);
}
