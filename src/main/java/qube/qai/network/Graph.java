/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

import grph.Grph;
import grph.oo.ObjectGrph;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 12/20/15.
 */
public class Graph extends ObjectGrph<Network.Vertex, Network.Edge> {

    private static Logger logger = LoggerFactory.getLogger("Graph");

    private static String directedOpen = "<directed=";
    private static String directedClose = ">";
    private static String verticesOpen = "vertices:[";
    private static String verticesClose = "]";
    private static String edgesOpen = "edges:{";
    private static String edgesClose = "}";
    private static String edgeSeperate = "§";

    private boolean directed = false;

    public Grph getBackingGrph() {
        return backingGrph;
    }

    public int v2i(Network.Vertex vertex) {
        return super.v2i(vertex);
    }

    public int e2i(Network.Edge edge) {
        return super.e2i(edge);
    }

    public Network.Vertex i2v(int v) {
        return super.i2v(v);
    }

    public Network.Edge i2e(int e) {
        return super.i2e(e);
    }

    public static String encode(Graph graph) {

        int vertexCount = 0;
        int edgeCount = 0;
        StringBuffer buffer = new StringBuffer();
        buffer.append(directedOpen);
        buffer.append(graph.isDirected());
        buffer.append(directedClose);
        buffer.append(verticesOpen);
        for (Network.Vertex vertex : graph.getVertices()) {
            buffer.append(vertex.getName());
            buffer.append("|");
            vertexCount++;
        }
        // remove last one
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append(verticesClose);

        buffer.append(edgesOpen);
        for (Network.Edge edge : graph.getEdges()) {
            buffer.append("(");
            buffer.append(edge.getFrom().getName());
            buffer.append("|");
            buffer.append(edge.getTo().getName());
            buffer.append("|");
            buffer.append(edge.getWeight());
            buffer.append(")");
            buffer.append(edgeSeperate);
            edgeCount++;
        }
        // remove last one
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append(edgesClose);

        logger.debug("encoded graph with #vertex: " + vertexCount + " #edges: " + edgeCount);

        return buffer.toString();
    }

    public static String encode(Network network) {
        Graph graph = network.getGraph();
        return encode(graph);
    }

    public static Graph decode(String serialString) {

        int vertexCount = 0;
        int edgeCount = 0;
        int skips = 0;
        Graph graph = new Graph();

        // check if there is anything to read at all
        if (StringUtils.isBlank(serialString)) {
            logger.debug("Cannot parse an empty string- returning an empty graph");
            return graph;
        }

        // rip the edges and vertices parts
        String directedPart = StringUtils.substringBetween(serialString, directedOpen, directedClose);
        if ("true".equals(directedPart)) {
            graph.setDirected(true);
        } else {
            graph.setDirected(false);
        }

        // create the vertices
        String vertexPart = StringUtils.substringBetween(serialString, verticesOpen, verticesClose);
        String[] vertexTokens = StringUtils.split(vertexPart, "|");
        if (vertexTokens != null) {
            for (int i = 0; i < vertexTokens.length; i++) {
                // remove the empty parts of string- this is quite important
                String name = vertexTokens[i].trim();
                Network.Vertex vertex = new Network.Vertex(name);
                if (!graph.getVertices().contains(vertex)) {
                    graph.addVertex(vertex);
                    vertexCount++;
                }
            }
        }

        // create the edges
        String edgesPart = StringUtils.substringBetween(serialString, edgesOpen, edgesClose);
        String[] edgeTokens = StringUtils.split(edgesPart, edgeSeperate);
        if (edgeTokens != null) {
            for (int i = 0; i < edgeTokens.length; i++) {
                String edgeToken = edgeTokens[i];
                edgeToken = StringUtils.remove(edgeToken, "(");
                edgeToken = StringUtils.remove(edgeToken, ")");

                String[] innerTokens = StringUtils.split(edgeToken, "|");
                if (innerTokens == null || innerTokens.length != 3) {
                    skips++;
                    logger.error("couldn't split: '" + edgeToken + "' token... skipping");
                    continue;
                }
                String fromToken = innerTokens[0].trim();
                String toToken = innerTokens[1].trim();
                double weight = 0;
                try {
                    weight = Double.parseDouble(innerTokens[2]);
                } catch (NumberFormatException e) {
                    logger.error("un-parsable token: " + innerTokens[2]);
                }

                Network.Vertex from = new Network.Vertex(fromToken);
                Network.Vertex to = new Network.Vertex(toToken);
                Network.Edge edge = new Network.Edge(from, to, weight);
                if (graph.isDirected()) {
                    graph.addUndirectedSimpleEdge(from, edge, to);
                } else {
                    graph.addDirectedSimpleEdge(from, edge, to);
                }
                edgeCount++;
            }
        }

        logger.debug("decoded graph with #vertex: " + vertexCount + " #edge: " + edgeCount);

        return graph;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }
}
