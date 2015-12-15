package qube.qai.procedure;

import junit.framework.TestCase;

/**
 * Created by rainbird on 11/28/15.
 */
public class TestNeuralNetworkAnalysis extends TestCase {

    // code repeated in TestAnalysisProcedures
    /*private boolean debug = true;

    public void testNeuralNetworkAnalysis() throws Exception {

        NeuralNetworkAnalysis statistics = new NeuralNetworkAnalysis();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        Matrix matrix = Matrix.createMatrix(true, 100, 100);
        NeuralNetwork network = new NeuralNetwork();
        network.buildFromAdjacencyMatrix(matrix);

        Selector<NeuralNetwork> selector = new DataSelector<NeuralNetwork>(network);
        statistics.getArguments().setArgument(NeuralNetworkAnalysis.INPUT_NEURAL_NETWORK, selector);

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

    *//**
     * run the given procedure and check that every
     * result parameter mentioned in the procedure
     * is actually to be found in the arguments
     * @param statistics
     *//*
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
    }*/
}
