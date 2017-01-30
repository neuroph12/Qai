package qube.qai.procedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.DataVisitor;

/**
 * Created by rainbird on 12/27/15.
 */
public abstract class ProcedureDecorator extends Procedure implements ProcedureConstants {

    protected Procedure toDecorate;

    protected Logger logger = LoggerFactory.getLogger("Procedure Logger");

    protected boolean debug = true;

    public ProcedureDecorator(String name, Procedure toDecorate) {
        super(name, toDecorate);
        this.toDecorate = toDecorate;
    }

    @Override
    public void execute() {
        toDecorate.execute();
    }

    @Override
    public Object accept(DataVisitor visitor, Object data) {
        data = toDecorate.accept(visitor, data);
        return visitor.visit(this, data);
    }
}
