package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/28/15.
 */
public class MatrixStatistics extends ProcedureChain {

    public static String NAME = "Matrix Statistics";

    public static String DESCRIPTION = "Analyses the distribution of the numbers in the matrix, " +
            "and eigenvalues and eigenvectors, as far as they exist, using eigenvalue decomposition";

    /**
     * Runs statistical analysis on the given matrix
     */
    public MatrixStatistics() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_MATRIX);
    }

    @Override
    public void run() {

    }
}
