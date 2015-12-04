package qube.qai.procedure;

/**
 * Created by rainbird on 12/1/15.
 */
public interface ProcedureVisitor {

    Object visit(Procedure procedure, Object data);
}
