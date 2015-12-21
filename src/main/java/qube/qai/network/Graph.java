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

    private static String verticesOpen = "vertices:[";
    private static String verticesClose = "]";
    private static String edgesOpen = "edges:{";
    private static String edgesClose = "}";

    public Grph getBackingGrph() {
        return backingGrph;
    }

    protected int v2i(Network.Vertex vertex) {
        return super.v2i(vertex);
    }

    protected int e2i(Network.Edge edge) {
        return super.e2i(edge);
    }

    protected Network.Vertex i2v(int v) {
        return super.i2v(v);
    }

    protected Network.Edge i2e(int e) {
        return super.i2e(e);
    }

    public String encode() {
        return getBackingGrph().toGrphText();
    }

    public static String encode(Graph graph) {
        StringBuffer buffer = new StringBuffer(verticesOpen);

        for (Network.Vertex vertex : graph.getVertices()) {
            buffer.append(vertex.getName());
            buffer.append(", ");
        }
        // remove last two
        buffer.deleteCharAt(buffer.length()-1);
        buffer.deleteCharAt(buffer.length()-1);
        buffer.append(verticesClose);

        buffer.append(edgesOpen);
        for (Network.Edge edge : graph.getEdges()) {
            buffer.append("(");
            buffer.append(edge.getFrom().getName());
            buffer.append("|");
            buffer.append(edge.getTo().getName());
            buffer.append("|");
            buffer.append(edge.getWeight());
            buffer.append("), ");
        }
        buffer.deleteCharAt(buffer.length()-1);
        buffer.deleteCharAt(buffer.length()-1);
        buffer.append(edgesClose);

        return buffer.toString();
    }

    public static String encode(Network network) {
        StringBuffer buffer = new StringBuffer(verticesOpen);
        Graph graph = network.getGraph();
        return encode(graph);
    }

    public static Graph decode(String serialString) {

        Graph graph = new Graph();

        // check if there is anything to read at all
        if (StringUtils.isBlank(serialString)) {
            //logger.info("Cannot parse an empty string- returning an empty graph");
            return graph;
        }

        // rip the edges and vertices parts
        String vertexPart = StringUtils.substringBetween(serialString, verticesOpen, verticesClose);
        String edgesPart = StringUtils.substringBetween(serialString, edgesOpen, edgesClose);

        // create the vertices
        String[] vertexTokens = StringUtils.split(vertexPart, ",");
        if (vertexTokens != null) {
            for (int i = 0; i < vertexTokens.length; i++) {
                // remove the empty parts of string- this is quite important
                String name = vertexTokens[i].trim();
                Network.Vertex vertex = new Network.Vertex(name);
                if (!graph.getVertices().contains(vertex)) {
                    graph.addVertex(vertex);
                }
            }
        }

        // create the edges
        String[] edgeTokens = StringUtils.split(edgesPart, ",");
        if (edgeTokens != null) {
            for (int i = 0; i < edgeTokens.length; i++) {
                String edgeToken = edgeTokens[i];
                edgeToken = StringUtils.remove(edgeToken, "(");
                edgeToken = StringUtils.remove(edgeToken, ")");

                String[] innerTokens = StringUtils.split(edgeToken, "|");
                String fromToken = innerTokens[0].trim();
                String toToken = innerTokens[1].trim();
                double weight = Double.parseDouble(innerTokens[2]);

                Network.Vertex from = new Network.Vertex(fromToken);
                Network.Vertex to = new Network.Vertex(toToken);
                Network.Edge edge = new Network.Edge(from, to, weight);
                graph.addUndirectedSimpleEdge(from, edge, to);
            }
        }

        return graph;
    }
}
