package qube.qai.procedure.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.Selector;
import qube.qai.data.Statistics;
import qube.qai.matrix.Matrix;
import qube.qai.procedure.ProcedureChain;

import java.util.List;

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
        arguments.getResultNames().add(MATRIX_METRICS);
        arguments.getResultNames().add(MATRIX_DATA_METRICS);
    }

    @Override
    public void run() {

        // first get the selector
        Matrix matrix = (Matrix) arguments.getSelector(INPUT_MATRIX).getData();
        if (matrix == null || matrix.getMatrix() == null) {
            logger.error("Input matrix has not been initialized properly: null value");
            return;
        }

        List elements = matrix.getMatrix().toListOfElements();
        Statistics stats = new Statistics(elements.toArray());
        Metrics dataMetrics = stats.buildMetrics();
        log("adding '" + MATRIX_METRICS + "' and '" + MATRIX_DATA_METRICS + "' to return values");
        arguments.addResult(MATRIX_DATA_METRICS, dataMetrics);
        arguments.addResult(MATRIX_METRICS, matrix.buildMetrics());
    }
}
