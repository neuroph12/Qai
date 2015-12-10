package qube.qai.procedure;

import junit.framework.TestCase;
import org.ojalgo.random.Normal;
import org.ojalgo.random.RandomNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.Selector;
import qube.qai.data.TimeSeries;
import qube.qai.data.selectors.DataSelector;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.TestTimeSeries;
import qube.qai.procedure.analysis.*;

import java.util.Date;
import java.util.Iterator;
import org.joda.time.DateTime;

/**
 * Created by rainbird on 11/30/15.
 */
public class TestAnalysisProcedures extends TestCase {

    private boolean debug = true;
    Logger logger = LoggerFactory.getLogger("TestAnalysisProcedures");

    /**
     * do the testing for the NeuralNetworkAnalysis class
     * @throws Exception
     */
    public void restNeuralNetworkAnalysis() throws Exception {

        NeuralNetworkAnalysis statistics = new NeuralNetworkAnalysis();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        // @TODO test not yet implemented
        fail("test not complete");

    }

    /**
     * do the testing for the MatrixAnalysis class
     * @throws Exception
     */
    public void restMatrixStatistics() throws Exception {

        MatrixStatistics statistics = new MatrixStatistics();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        assertTrue("input matrix is one of the arguments", arguments.getArgumentNames().contains(MatrixStatistics.INPUT_MATRIX));

        // create a matrix and run the analysis
        Matrix matrix = Matrix.createMatrix(true, 100, 100);
        Selector<Matrix> selector = new DataSelector<Matrix>(matrix);
        statistics.getArguments().setArgument(MatrixStatistics.INPUT_MATRIX, selector);

        long start = System.currentTimeMillis();
        statistics.run();

        long duration = System.currentTimeMillis() - start;
        log("Process: " + MatrixStatistics.NAME + " took " + duration + "ms with 100x100 elements");
        log("results:" + statistics.getArguments().getResultNames());
        for (String name : statistics.getArguments().getResultNames()) {
            Object result = statistics.getArguments().getResult(name);
            if (result instanceof Metrics) {
                log("found metrics: " + name);
                log(((Metrics)result).toString());
            } else {
                log("result: " + result + " value: " + result);
            }


        }

    }

    /**
     * do the testing for the NetworkStatistics class
     * @throws Exception
     */
    public void restNetworkStatistics() throws Exception {

        NetworkStatistics statistics = new NetworkStatistics();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        // @TODO test not yet implemented
        fail("test not complete");
    }

    /**
     * do the testing for the NeuralNetworkForwardPropagation class
     * @throws Exception
     */
    public void restNeuralNetworkForwardPropagation() throws Exception {

        NeuralNetworkForwardPropagation statistics = new NeuralNetworkForwardPropagation();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        // @TODO test not yet implemented
        fail("test not complete");
    }

    /**
     * do the testing for the NeuralNetworkForwardPropagation class
     * @throws Exception
     */
    public void restTimeSeriesAnalysis() throws Exception {

        TimeSeriesAnalysis statistics = new TimeSeriesAnalysis();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        Date start = null;
        Date end = null;
        TimeSeries<Double> timeSeries = TestTimeSeries.createTimeSeries(start, end);

        // @TODO test not yet implemented
        fail("test not complete");
    }

    private void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.info(message);
        }
    }

}
