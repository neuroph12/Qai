package qube.qai.procedure.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.*;
import qube.qai.data.analysis.Statistics;
import qube.qai.main.QaiTestBase;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureChain;
import qube.qai.procedure.analysis.*;
import qube.qai.services.ProcedureSourceInterface;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by rainbird on 11/30/15.
 */
public class TestAnalysisProcedures extends QaiTestBase {

    Logger logger = LoggerFactory.getLogger("TestAnalysisProcedures");

    @Inject
    private ProcedureSourceInterface procedureSource;

    private boolean debug = true;

    /**
     * do the testing for the MatrixAnalysis class
     * @throws Exception
     */
    public void testMatrixStatistics() throws Exception {

        MatrixStatistics statistics = (MatrixStatistics) procedureSource.getProcedureWithName(MatrixStatistics.NAME);

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        assertTrue("input matrix is one of the arguments", arguments.getArgumentNames().contains(MatrixStatistics.INPUT_MATRIX));

        checkResultsOf(statistics);

        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
        log("results:" + statistics.getArguments().getResultNames());
        for (String name : statistics.getArguments().getResultNames()) {
            Object result = statistics.getArguments().getResult(name);
            if (result instanceof Metrics) {
                log("found metrics: " + name);
                log((Metrics) result);
            } else {
                log("result: " + result + " value: " + result);
            }
        }

    }

    public void testChangePointAnalysis() throws Exception {

        ChangePointAnalysis statistics = (ChangePointAnalysis) procedureSource.getProcedureWithName(ChangePointAnalysis.NAME);

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        assertTrue("input matrix is one of the arguments", arguments.getArgumentNames().contains(ChangePointAnalysis.INPUT_TIME_SEQUENCE));

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

        TimeSequenceAnalysis statistics = (TimeSequenceAnalysis) procedureSource.getProcedureWithName(TimeSequenceAnalysis.NAME);

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        checkResultsOf(statistics);

        log("results:" + statistics.getArguments().getResultNames());
        for (String name : statistics.getArguments().getResultNames()) {
            Object result = statistics.getArguments().getResult(name);
            if (result instanceof Metrics) {
                log("found metrics: " + name);
                log((Metrics) result);
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

        NetworkStatistics statistics = (NetworkStatistics) procedureSource.getProcedureWithName(NetworkStatistics.NAME);

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        checkResultsOf(statistics);

        log("results:" + statistics.getArguments().getResultNames());
        for (String name : statistics.getArguments().getResultNames()) {
            Object result = statistics.getArguments().getResult(name);
            if (result instanceof Metrics) {
                log("found metrics: " + name);
                log((Metrics) result);
            } else {
                log("result: " + result + " value: " + result);
            }
        }
    }

    /**
     * do the testing for neural-network analysis
     */
    public void restNeuralNetworkAnalysis() throws Exception {

        NeuralNetworkAnalysis statistics = (NeuralNetworkAnalysis) procedureSource.getProcedureWithName(NeuralNetworkAnalysis.NAME);

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        checkResultsOf(statistics);

        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
        log("results:" + statistics.getArguments().getResultNames());
        for (String name : statistics.getArguments().getResultNames()) {
            Object result = statistics.getArguments().getResult(name);
            if (result instanceof Metrics) {
                log("found metrics: " + name);
                log((Metrics)result);
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

        NeuralNetworkForwardPropagation statistics = (NeuralNetworkForwardPropagation) procedureSource.getProcedureWithName(NeuralNetworkForwardPropagation.NAME);

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        List<String> names = new ArrayList<String>();
        String[] nameStrings = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eightth", "nineth", "tenth"};
        for (String n : nameStrings) {
            names.add(n);
        }

        checkResultsOf(statistics);

        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
        log("results:" + statistics.getArguments().getResultNames());
        Map<String, TimeSequence> timeSeriesMap = (Map<String, TimeSequence>) arguments.getResult(NeuralNetworkForwardPropagation.MAP_OF_TIME_SEQUENCE);
        assertNotNull("time series map cannot be null", timeSeriesMap);

        for (String name : names) {
            TimeSequence timeSequence = timeSeriesMap.get(name);
            assertNotNull("time series for: " + name + " may not be null", timeSequence);
            log("time-series for: " + name + ": (" + t2String(timeSequence) + ")");
        }
    }



    /**
     * do the testing for the SortingPercentilesProcedure class
     * @throws Exception
     */
    public void testSortingPercentilesProcedure() throws Exception {

        SortingPercentilesProcedure statistics = (SortingPercentilesProcedure) procedureSource.getProcedureWithName(SortingPercentilesProcedure.NAME);

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        checkResultsOf(statistics);

        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
        log("results:" + statistics.getArguments().getResultNames());
        for (String name : statistics.getArguments().getResultNames()) {
            Object result = statistics.getArguments().getResult(name);
            int rank = 1;
            if (result instanceof Map) {
                Map<String, Statistics> statisticsMap = (Map<String, Statistics>) result;
                for (String key : statisticsMap.keySet()) {
                    Statistics stats = statisticsMap.get(key);
                    log("stats: " + key + " average: " + stats.getAverage() + " with rank: " + rank);
                    rank++;
                }
            } else if (result instanceof TimeSequence) {
                log("The average time series: " + ((TimeSequence)result).toArray());
            }
        }
    }

    /**
     * convert the sequence to a string
     * @param series
     * @return
     */
    private String t2String(TimeSequence series) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<Number> it = series.iterator(); it.hasNext(); ) {
            Number number = it.next();
            buffer.append(number.doubleValue());
            buffer.append(",");
        }

        buffer.deleteCharAt(buffer.length()-1);
        return buffer.toString();
    }

    /**
     * run the given procedure and check that every
     * result parameter mentioned in the procedure
     * is actually to be found in the arguments
     * @param statistics
     */
    private void checkResultsOf(Procedure statistics) {

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

    private void log(Metrics metrics) {
        if (debug) {
            for (String name : metrics.getNames()) {
                String line = name + ": " + metrics.getValue(name);
                System.out.println(line);
            }
        }
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
            //logger.info(message);
        }
    }

}
