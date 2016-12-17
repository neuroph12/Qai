package qube.qai.procedure;

import qube.qai.data.Arguments;

/**
 * Created by rainbird on 12/27/15.
 */
public class SimpleProcedure extends Procedure {

    @Override
    public void execute() {
        // do nothing
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments();
    }
}
