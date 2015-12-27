package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.analysis.Statistics;
import qube.qai.matrix.Matrix;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureDecorator;

import java.util.List;

/**
 * Created by rainbird on 11/28/15.
 */
public class MatrixStatistics extends ProcedureDecorator {

    public static String NAME = "Matrix Statistics";

    public static String DESCRIPTION = "Analyses the distribution of the numbers in the matrix, " +
            "and eigenvalues and eigenvectors, as far as they exist, using eigenvalue decomposition";

    /**
     * Runs statistical analysis on the given matrix
     */
    public MatrixStatistics(Procedure procedure) {
        super(procedure);
    }

    @Override
    public void buildArguments() {
        name = NAME;
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_MATRIX);
        arguments.putResultNames(MATRIX_METRICS, MATRIX_DATA_METRICS);
    }

    @Override
    public void execute() {

        toDecorate.execute();

        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(toDecorate.getArguments());
        }

        // first get the selector
        Matrix matrix = (Matrix) arguments.getSelector(INPUT_MATRIX).getData();
        if (matrix == null || matrix.getMatrix() == null) {
            logger.error("Input matrix has not been initialized properly: null value");
            return;
        }

        List elements = matrix.getMatrix().toListOfElements();
        Statistics stats = new Statistics(elements.toArray());
        Metrics dataMetrics = stats.buildMetrics();
        Metrics matrixMetrics = matrix.buildMetrics();

        logger.info("adding '" + MATRIX_METRICS + "' and '" + MATRIX_DATA_METRICS + "' to return values");
        arguments.addResult(MATRIX_DATA_METRICS, dataMetrics);
        arguments.addResult(MATRIX_METRICS, matrixMetrics);
    }
}
