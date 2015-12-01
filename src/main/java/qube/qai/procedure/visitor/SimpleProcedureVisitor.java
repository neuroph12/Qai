package qube.qai.procedure.visitor;

import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureVisitor;

/**
 * Created by rainbird on 12/1/15.
 */
public class SimpleProcedureVisitor implements ProcedureVisitor {

    private StringBuffer buffer;

    public SimpleProcedureVisitor() {
        buffer = new StringBuffer();
    }

    public Object visit(Procedure chain, Object data) {

        buffer.append("(")
                .append(chain.getName())
//                .append("[").append(chain.getUuid()).append("] (")
                .append("[").append(chain.getArguments().toString()).append("]")
                .append(")");

        if (chain.haveChildren()) {
            buffer.append("(");
            chain.childrenAccept(this, data);
            buffer.append(")");
        }


        return buffer.toString();
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
