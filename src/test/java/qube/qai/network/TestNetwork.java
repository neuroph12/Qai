package qube.qai.network;


import junit.framework.TestCase;
import qube.qai.matrix.Matrix;

import java.util.Collection;

/**
 * Created by rainbird on 11/24/15.
 */
public class TestNetwork extends TestCase {

    private boolean debug = true;

    public void testNetworkBuildAdjacencyMatrix() throws Exception {
        Network network = Network.createTestNetwork();

        network.buildAdjacencyMatrix();
        Matrix adjacencyMatrix = network.getAdjacencyMatrix();
        assertNotNull(adjacencyMatrix);

        Collection<Network.Edge> edges = network.getAllEdges();
        for (Network.Edge edge : edges) {
            Network.Vertex from = edge.getFrom();
            Network.Vertex to = edge.getTo();

            int indexFrom = network.v2i(from);
            int indexTo = network.v2i(to);
            double value = adjacencyMatrix.getMatrix().get(indexFrom, indexTo).doubleValue();
            assertTrue("has to be a value set", value != 0);
        }
        log("all edges found to have values- now stats:");
        logNetwork(network);
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

        // this edge does not exist- test for negative as well...
        Network.Edge mersinTimbuktu = new Network.Edge(new Network.Vertex("mersin"), new Network.Vertex("timbuktu"));
        assertTrue("has to fail to find the edge", !network.getAllEdges().contains(mersinTimbuktu));
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
