package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/28/15.
 */
public class MatrixStatistics extends ProcedureChain {

    public static String NAME = "Matrix Statistics";

    /**
     * Runs statistical analysis on the given matrix
     */
    public MatrixStatistics() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments(INPUT_MATRIX);
    }

    @Override
    public void run() {

    }
}
