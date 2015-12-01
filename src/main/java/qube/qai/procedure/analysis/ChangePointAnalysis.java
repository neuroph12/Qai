package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/28/15.
 */
public class ChangePointAnalysis extends ProcedureChain {

    public static String NAME = "Change-Point Analysis";

    /**
     * runs change-point analysis of a given time-series
     */
    public ChangePointAnalysis() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments(INPUT_TIME_SERIES);
    }

    @Override
    public void run() {

    }
}
