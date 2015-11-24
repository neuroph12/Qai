package qube.qai.network;


import junit.framework.TestCase;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import qube.qai.matrix.Matrix;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by rainbird on 11/24/15.
 */
public class TestNetwork extends TestCase {

    public void testNetworkAdjacencyMatrix() throws Exception {
        Network network = Network.createTestNetwork();

        Matrix adjacencyMatrix = network.getAdjacencyMatrix();
        assertNotNull(adjacencyMatrix);

        // @TODO probably the most important test of the day- get it implemented
        fail("rest of the test is not implemented");


    }
}
