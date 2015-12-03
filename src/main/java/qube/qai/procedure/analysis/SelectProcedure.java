package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 12/2/15.
 */
public class SelectProcedure extends ProcedureChain {

    public static String NAME = "Select Procedure";

    public SelectProcedure() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments("from collection", "criteria");
    }

    @Override
    public void run() {

    }
}
