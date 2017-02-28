/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.network;


import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.matrix.Matrix;

import java.util.Collection;

/**
 * Created by rainbird on 11/24/15.
 */
public class TestNetwork extends TestCase {

    private Logger logger = LoggerFactory.getLogger("TestNetwork");

    private boolean debug = true;

    public void testNetworkSerialization() throws Exception {

        Network network = Network.createTestNetwork();
        //Graph graph = network.getGraph();
        String serialString = Graph.encode(network);
        log("Serial string of graph: " + serialString);

        Graph graph = Graph.decode(serialString);
        assertNotNull("network may not be null", graph);

        String serialStringCopy = Graph.encode(graph);
        log("created graph serial: " + serialStringCopy);

        boolean containsAllEdges = graph.getEdges().containsAll(network.getEdges());
        log("all edges have to be contained: " + containsAllEdges);
        assertTrue("all edges have to be contained", containsAllEdges);
        boolean containsAllVertices = graph.getVertices().containsAll(network.getVertices());
        log("all vertices have to be contained: " + containsAllVertices);
        assertTrue("all vertices have to be contained", containsAllVertices);

    }

    public void testNetworkBuildAdjacencyMatrix() throws Exception {
        Network network = Network.createTestNetwork();

        network.buildAdjacencyMatrix();
        Matrix adjacencyMatrix = network.getAdjacencyMatrix();
        assertNotNull(adjacencyMatrix);

        Collection<Network.Edge> edges = network.getEdges();
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
     *
     * @throws Exception
     */
    public void testNetworkVerticesAndEdges() throws Exception {
        // this line is recommended by the authors of graph-library
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
        assertTrue("has to find the edge", network.getEdges().contains(viennaLondon));

        Network.Edge copenhagenHelsinki = new Network.Edge(new Network.Vertex("copenhagen"), new Network.Vertex("helsinki"));
        assertTrue("has to find the edge", network.getEdges().contains(copenhagenHelsinki));

        // this edge does not exist- test for negative as well...
        Network.Edge mersinTimbuktu = new Network.Edge(new Network.Vertex("mersin"), new Network.Vertex("timbuktu"));
        assertTrue("has to fail to find the edge", !network.getEdges().contains(mersinTimbuktu));
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
