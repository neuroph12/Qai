package qube.qai.network;

import grph.Grph;
import grph.io.GraphBuildException;
import grph.io.GrphTextReader;
import grph.io.ParseException;
import grph.oo.ObjectGrph;
import opennlp.tools.tokenize.Tokenizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.Net;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;

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

    public String serialString() {
        return getBackingGrph().toGrphText();
    }

    public static String serialString(Network network) {
        StringBuffer buffer = new StringBuffer(verticesOpen);

        for (Network.Vertex vertex :network.getVertices()) {
            buffer.append(vertex.getName());
            buffer.append(", ");
        }
        // remove last two
        buffer.deleteCharAt(buffer.length()-1);
        buffer.deleteCharAt(buffer.length()-1);
        buffer.append(verticesClose);

        buffer.append(edgesOpen);
        for (Network.Edge edge : network.getAllEdges()) {
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

    public static Network fromSerialString(String serialString) {

        String vertexPart = StringUtils.substringBetween(serialString, verticesOpen, verticesClose);
        String edgesPart = StringUtils.substringBetween(serialString, edgesOpen, edgesClose);

        Graph graph = new Graph();
        // create the vertices
        String[] vertexTokens = StringUtils.split(vertexPart, ",");
        for (int i = 0; i < vertexTokens.length; i++) {
            // remove the empty parts of string- this is quite important
            String name = vertexTokens[i].trim();
            Network.Vertex vertex = new Network.Vertex(name);
            if (!graph.getVertices().contains(vertex)) {
                graph.addVertex(vertex);
            }
        }

        // create the edges
        String[] edgeTokens = StringUtils.split(edgesPart, ",");
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

        return new Network(graph);
    }
}
