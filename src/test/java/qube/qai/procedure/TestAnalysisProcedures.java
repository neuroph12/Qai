package qube.qai.procedure;

import junit.framework.TestCase;
import qube.qai.data.Arguments;
import qube.qai.procedure.analysis.*;

/**
 * Created by rainbird on 11/30/15.
 */
public class TestAnalysisProcedures extends TestCase {

    /**
     * do the testing for the NeuralNetworkAnalysis class
     * @throws Exception
     */
    public void testNeuralNetworkAnalysis() throws Exception {

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
    public void testMatrixStatistics() throws Exception {

        MatrixStatistics statistics = new MatrixStatistics();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        assertTrue("input matrix is one of the arguments", arguments.getArgumentNames().contains("input matrix"));

        // @TODO test not yet implemented
        fail("test not complete");
    }

    /**
     * do the testing for the NetworkStatistics class
     * @throws Exception
     */
    public void testNetworkStatistics() throws Exception {

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
    public void testNeuralNetworkForwardPropagation() throws Exception {

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
    public void testTimeSeriesAnalysis() throws Exception {

        TimeSeriesAnalysis statistics = new TimeSeriesAnalysis();

        Arguments arguments = statistics.getArguments();
        assertNotNull("arguments may not be null", arguments);

        // @TODO test not yet implemented
        fail("test not complete");
    }

}
