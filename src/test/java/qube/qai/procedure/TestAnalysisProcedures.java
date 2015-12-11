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
import qube.qai.network.Network;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.analysis.*;

import java.util.Collection;
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
     * do the testing for the MatrixAnalysis class
     * @throws Exception
     */
    public void testMatrixStatistics() throws Exception {

        MatrixStatistics statistics = new MatrixStatistics();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        assertTrue("input matrix is one of the arguments", arguments.getArgumentNames().contains(MatrixStatistics.INPUT_MATRIX));

        // create a matrix and run the analysis
        Matrix matrix = Matrix.createMatrix(true, 100, 100);
        Selector<Matrix> selector = new DataSelector<Matrix>(matrix);
        statistics.getArguments().setArgument(MatrixStatistics.INPUT_MATRIX, selector);

        checkResultsOf(statistics);

        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
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

    public void testChangePointAnalysis() throws Exception {

        ChangePointAnalysis statistics = new ChangePointAnalysis();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        assertTrue("input matrix is one of the arguments", arguments.getArgumentNames().contains(ChangePointAnalysis.INPUT_TIME_SERIES));
        Date start = DateTime.parse("2000-1-1").toDate();
        Date end = DateTime.now().toDate();
        TimeSeries<Double> timeSeries = TestTimeSeries.createTimeSeries(start, end);
        Selector<TimeSeries> selector = new DataSelector<TimeSeries>(timeSeries);
        statistics.getArguments().setArgument(ChangePointAnalysis.INPUT_TIME_SERIES, selector);

        checkResultsOf(statistics);

        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
        log("results:" + statistics.getArguments().getResultNames());
        for (String name : statistics.getArguments().getResultNames()) {
            Object result = statistics.getArguments().getResult(name);

            if (result instanceof Collection) {
                Collection<ChangePointAnalysis.ChangePointMarker> changePoints = (Collection<ChangePointAnalysis.ChangePointMarker>) result;
                log("the resulting collection isEmpty(): " + changePoints.isEmpty());
                for (ChangePointAnalysis.ChangePointMarker marker : changePoints) {
                    log("Change point at: " + marker.getDate() + " with " + marker.getProbability());
                }
            }
        }
    }

    /**
     * do the testing for the NeuralNetworkForwardPropagation class
     * @throws Exception
     */
    public void testTimeSeriesAnalysis() throws Exception {

        TimeSeriesAnalysis statistics = new TimeSeriesAnalysis();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        Date startDate = DateTime.parse("2015-1-1").toDate();
        Date endDate = DateTime.now().toDate();
        TimeSeries<Double> timeSeries = TestTimeSeries.createTimeSeries(startDate, endDate);
        Selector<TimeSeries> selector = new DataSelector<TimeSeries>(timeSeries);

        statistics.getArguments().setArgument(TimeSeriesAnalysis.INPUT_TIME_SERIES, selector);

        checkResultsOf(statistics);

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
    public void testNetworkStatistics() throws Exception {

        NetworkStatistics statistics = new NetworkStatistics();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        Matrix matrix = Matrix.createMatrix(true, 100, 100);
        Network network = Network.createTestNetwork();
        Selector<Network> selector = new DataSelector<Network>(network);
        statistics.getArguments().setArgument(NetworkStatistics.INPUT_NETWORK, selector);

        checkResultsOf(statistics);

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
     * do the testing for the NeuralNetworkForwardPropagation class
     * @throws Exception
     */
    public void testNeuralNetworkForwardPropagation() throws Exception {

        NeuralNetworkForwardPropagation statistics = new NeuralNetworkForwardPropagation();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        // @TODO test not yet implemented
        fail("test not complete");
    }

    /**
     * run the given procedure and check that every
     * result parameter mentioned in the procedure
     * is actually to be found in the arguments
     * @param statistics
     */
    private void checkResultsOf(ProcedureChain statistics) {

        long start = System.currentTimeMillis();

        statistics.run();
        long duration = System.currentTimeMillis() - start;

        log("Process: " + statistics.getName() + " took " + duration + "ms");

        assertTrue("there has to be result names", !statistics.getArguments().getResultNames().isEmpty());
        // assert also that the given results can actually be accessed
        for (String resultName : statistics.getArguments().getResultNames()) {
            assertTrue("result: '" + resultName + "' missing", statistics.getArguments().getResult(resultName) != null);
        }
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
            //logger.info(message);
        }
    }

}
