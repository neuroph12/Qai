package qube.qai.procedure;

import junit.framework.TestCase;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;

/**
 * Created by rainbird on 11/28/15.
 */
public class TestNeuralNetworkAnalysis extends TestCase {

    public void testMatrixAnalysis() throws Exception {
        NeuralNetworkAnalysis neuralNetworkAnalysis = (NeuralNetworkAnalysis) NeuralNetworkAnalysis.Factory.constructProcedure();

        assertNotNull("Factory created no object at all", neuralNetworkAnalysis);

        fail("test not yet implemented");
    }
}
