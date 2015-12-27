package qube.qai.procedure;

import qube.qai.data.Arguments;

/**
 * Created by rainbird on 12/27/15.
 */
public class SelectionProcedure extends Procedure {

    public static String NAME = "Selection Procedure";

    /**
     * this is mainly to pass the children the argument
     * represents user preparing, or choosing a certain
     * input for the children to process
     */
    public SelectionProcedure() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments();
    }

    @Override
    public void execute() {
        // do nothing as well...
    }
}
