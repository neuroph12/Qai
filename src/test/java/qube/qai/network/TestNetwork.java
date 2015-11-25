package qube.qai.network;


import junit.framework.TestCase;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import qube.qai.matrix.Matrix;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

/**
 * Created by rainbird on 11/24/15.
 */
public class TestNetwork extends TestCase {

    private boolean debug = true;

    public void testNetworkAdjacencyMatrix() throws Exception {
        Network network = Network.createTestNetwork();

        Matrix adjacencyMatrix = network.getAdjacencyMatrix();
        assertNotNull(adjacencyMatrix);

        // @TODO probably the most important test of the day- get it implemented
        fail("rest of the test is not implemented");


    }

    /**
     * basic test really- and checking out the equals method on vertex is actually used
     * the trick is to implement both hash and equals
     * @throws Exception
     */
    public void testNetworkVerticesAndEdges() throws Exception {
        // this line is recommended by the authors of grph-library
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        Network network = Network.createTestNetwork();

        logNetwork(network);

        log(network.toString());
        Collection<Network.Vertex> vertices = network.getVertices();
        for (Network.Vertex vertex : vertices) {
            String name = vertex.getName();
            Network.Vertex searchVertex = new Network.Vertex(name);
            assertTrue("has to use equals!!!", network.containsVertex(searchVertex));
        }

        // now check that the edges are working right
        Network.Edge viennaLondon = new Network.Edge(new Network.Vertex("vienna"), new Network.Vertex("london"));
        assertTrue("has to find the edge", network.getAllEdges().contains(viennaLondon));

        Network.Edge copenhagenHelsinki = new Network.Edge(new Network.Vertex("copenhagen"), new Network.Vertex("helsinki"));
        assertTrue("has to find the edge", network.getAllEdges().contains(copenhagenHelsinki));
    }

    private void logNetwork(Network network) {
        log("Network number of vertices: " + network.getNumberOfVertices());
        log("Network number of edges: " + network.getNumberOfEdges());
        log("Network average degree: " + network.getAverageDegree());
        //log("Network clustering coefficient: " + network.getClusteringCoefficient());
        log("Network density: " + network.getDensity());
        //log("Network diameter: " + network.getDiameter());
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
