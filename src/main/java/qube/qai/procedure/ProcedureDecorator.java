package qube.qai.procedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 12/27/15.
 */
public abstract class ProcedureDecorator extends Procedure implements ProcedureConstants {

    protected Procedure toDecorate;

    protected Logger logger = LoggerFactory.getLogger("Procedure Logger");

    protected boolean debug = true;

    public ProcedureDecorator(Procedure toDecorate) {
        super();
        this.toDecorate = toDecorate;
    }

    @Override
    public void execute() {
        toDecorate.execute();
    }

}
