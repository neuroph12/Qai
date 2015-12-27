package qube.qai.network;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.data.MetricTyped;
import qube.qai.data.Metrics;
import qube.qai.matrix.Matrix;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Created by rainbird on 11/21/15.
 * this is simply an extension of the ObjectGrph class, in order to be able
 * to get a handle on the backingGrph on which the missing algorithms
 * can be called
 */
public class Network implements Serializable, MetricTyped, AcceptsVisitors {

    private Logger logger = LoggerFactory.getLogger("Network");

    protected boolean makeMatrix = true;

    protected boolean pruneWeakLinks = true;

    protected double PRUNE_TRESHOLD = 0.1;

    protected Matrix adjacencyMatrix;

    private String encodedGraph;

    // normally we use non-directed graphs
    protected boolean directed = false;

    public Network() {
        //this.graph = new Graph();
    }

    public Network(Graph graph) {
        this.encodedGraph = Graph.encode(graph);
    }

    public Graph getGraph() {
        return graph();
    }

    public Object accept(DataVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Metrics buildMetrics() {

        Metrics metrics = null;

        if (makeMatrix) {
            if (adjacencyMatrix == null) {
                buildAdjacencyMatrix();
            }
            metrics = adjacencyMatrix.buildMetrics();
        } else {
            metrics = new Metrics();
        }

        metrics.putValue("number of vertices", getNumberOfVertices());
        metrics.putValue("number of edges", getNumberOfEdges());

        // unfortunately not all of the properties are to be found in all graphs
        // those which don't make sense- usually these below, throw exceptions
        // and therefore ignored in the metrics of the graph (or network)
        /*try {
            metrics.putValue("average degree", getAverageDegree());
        } catch (Exception e) {
            logger.error("error while calculating 'average degree': " + e.getMessage());
        }
        try {
            metrics.putValue("clustering coefficient", getClusteringCoefficient());
        } catch (Exception e) {
            logger.error("error while calculating 'clustering coefficient': " + e.getMessage());
        }
        try {
            metrics.putValue("density", getDensity());
        } catch (Exception e) {
            logger.error("error while calculating 'density': " + e.getMessage());
        }
        try {
            metrics.putValue("diameter", getDiameter());
        } catch (Exception e) {
            logger.error("error while calculating 'diameter': " + e.getMessage());
        }*/

        return metrics;
    }

    public void buildAdjacencyMatrix() {
        // this is when we refuse to create an adjacency matrix
        if (!makeMatrix) {
            return;
        }

        int size = getNumberOfVertices();
        adjacencyMatrix = new Matrix(size, size);

        for (Edge edge : getEdges()) {
            Vertex from = edge.getFrom();
            Vertex to = edge.getTo();
            double value = edge.getWeight();
            if (value == 0)  {
                value = 1.0;
            }

            int indexFrom = v2i(from);
            int indexTo = v2i(to);
            //builder.set(indexFrom, indexTo, value);
            adjacencyMatrix.setValueAt(indexTo, indexFrom, value);
        }
    }

    /**
     * Caution if you are trying to instantiate a Neural-Network this will not work
     * Neural-Networks cannot be built from an external model
     * @param adjacencyMatrix
     */
    public void buildFromAdjacencyMatrix(Matrix adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        buildFromAdjacencyMatrix();
    }

    public void buildFromAdjacencyMatrix() {

        if (adjacencyMatrix == null) {
            throw new IllegalArgumentException("Adjacency matrix has not been initialized or set, can't construct network.");
        }

        Graph graph = graph();
        graph.setDirected(true);
        int size = adjacencyMatrix.getColumns();
        // we first insert the vertices if the underlying graph doesn't have the vertices first
        if (graph.getVertices() != null && size != graph.getVertices().size()) {
            for (int i = 0; i < adjacencyMatrix.getColumns(); i++) {
                Vertex vertex = new Vertex("vertex " + i);
                graph.addVertex(vertex);
            }
        }
        int added = 0;
        int skips = 0;
        int pruned = 0;
        for (int i = 0; i < adjacencyMatrix.getRows(); i++) {
            for (int j = 0; j < adjacencyMatrix.getColumns(); j++) {
                Number value = adjacencyMatrix.getValueAt(i, j);
                // check if we need to add an edge at all
                if (value == null || value.doubleValue() == 0) {
                    skips++;
                    continue;
                }
                // prune weak links
                if (pruneWeakLinks && value.doubleValue() < PRUNE_TRESHOLD) {
                    pruned++;
                    continue;
                }
                //
                Vertex from = graph.i2v(i);
                Vertex to = graph.i2v(j);
                // create the edge
                Edge edge = new Edge(from, to);
                if (!graph.containsEdge(edge)) {
                    edge.setWeight(value.doubleValue());
                    graph.addDirectedSimpleEdge(from, edge, to);
                    added++;
                }
            }
        }
        String message = "creating network from adjacency matrix completed #vertices:" + size + " #edges: " + added + " #skipped:" + skips + " #pruned: " + pruned;
        logger.debug(message);

        record(graph);
    }

    protected Graph graph() {
        return Graph.decode(encodedGraph);
    }

    protected void record(Graph graph) {
        encodedGraph = Graph.encode(graph);
    }

    public boolean isMakeMatrix() {
        return makeMatrix;
    }

    public void setMakeMatrix(boolean makeMatrix) {
        this.makeMatrix = makeMatrix;
    }

    public Matrix getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public int getNumberOfVertices() {
        return graph().getVertices().size();
    }

    public int getNumberOfEdges() {
        return graph().getEdges().size();
    }

    protected int v2i(Vertex vertex) {
        return graph().v2i(vertex);
    }

    protected int e2i(Edge edge) {
        return graph().e2i(edge);
    }

    protected Vertex i2v(int v) {
        return graph().i2v(v);
    }

    protected Edge i2e(int e) {
        return graph().i2e(e);
    }

    public Collection<Vertex> getVertices() {
        return graph().getVertices();
    }

    public Set<Edge> getIncidentEdges(Vertex vertex) {
        return graph().getIncidentEdges(vertex);
    }

    public Collection<Edge> getEdges() {
        return graph().getEdges();
    }

    public double getAverageDegree() {
        return graph().getBackingGrph().getAverageDegree();
    }

    public double getClusteringCoefficient() {
        return graph().getBackingGrph().getClusteringCoefficient();
    }

    public double getDensity() {
        return graph().getBackingGrph().getDensity();
    }

    public double getDiameter() {
        return graph().getBackingGrph().getDiameter();
    }

    public double getPRUNE_TRESHOLD() {
        return PRUNE_TRESHOLD;
    }

    public void setPRUNE_TRESHOLD(double PRUNE_TRESHOLD) {
        this.PRUNE_TRESHOLD = PRUNE_TRESHOLD;
    }

    public boolean isPruneWeakLinks() {
        return pruneWeakLinks;
    }

    public void setPruneWeakLinks(boolean pruneWeakLinks) {
        this.pruneWeakLinks = pruneWeakLinks;
    }

    @Override
    public String toString() {
        return graph().getBackingGrph().toString();
    }

    public void addVertex(Vertex vertex) {
        graph().addVertex(vertex);
    }

    public void removeVertex(Vertex vertex) {
        graph().removeVertex(vertex);
    }

    public boolean containsVertex(Vertex vertex) {
        return graph().containsVertex(vertex);
    }

    public void addSimpleEdge(Vertex from, Edge edge, Vertex to) {
        graph().addUndirectedSimpleEdge(from, edge, to);
    }

    public String getEncodedGraph() {
        return encodedGraph;
    }

    public void setEncodedGraph(String encodedGraph) {
        this.encodedGraph = encodedGraph;
    }

    public void setGraph(Graph graph) {
        this.encodedGraph = Graph.encode(graph);
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    /**
     * a silly method to create a tiny network for playing around
     * @return
     */
    public static Network createTestNetwork() {
        Network network = new Network();
        Graph graph = new Graph();

        Vertex vienna = new Vertex("vienna");
        graph.addVertex(vienna);

        Vertex london = new Vertex("london");
        graph.addVertex(london);

        Vertex paris = new Vertex("paris");
        graph.addVertex(paris);

        Vertex bergen = new Vertex("bergen");
        graph.addVertex(bergen);

        Vertex timbuktu = new Vertex("timbuktu");
        graph.addVertex(timbuktu);

        Vertex mersin = new Vertex("mersin");
        graph.addVertex(mersin);

        Vertex helsinki = new Vertex("helsinki");
        graph.addVertex(helsinki);

        Vertex amsterdam = new Vertex("amsterdam");
        graph.addVertex(amsterdam);

        Vertex copenhagen = new Vertex("copenhagen");
        graph.addVertex(copenhagen);

        graph.addUndirectedSimpleEdge(vienna, new Edge(vienna, london), london);
        graph.addUndirectedSimpleEdge(vienna, new Edge(vienna, mersin), mersin);
        graph.addUndirectedSimpleEdge(vienna, new Edge(vienna, bergen), bergen);

        graph.addUndirectedSimpleEdge(london, new Edge(london, paris), paris);
        graph.addUndirectedSimpleEdge(london, new Edge(london, bergen), bergen);
        graph.addUndirectedSimpleEdge(london, new Edge(london, timbuktu), timbuktu);
        graph.addUndirectedSimpleEdge(london, new Edge(london, helsinki), helsinki);
        graph.addUndirectedSimpleEdge(london, new Edge(london, amsterdam), amsterdam);

        graph.addUndirectedSimpleEdge(copenhagen, new Edge(copenhagen, amsterdam), amsterdam);
        graph.addUndirectedSimpleEdge(copenhagen, new Edge(copenhagen, helsinki), helsinki);
        graph.addUndirectedSimpleEdge(copenhagen, new Edge(copenhagen, bergen), bergen);

        graph.addUndirectedSimpleEdge(bergen, new Edge(bergen, amsterdam), amsterdam);
        graph.addUndirectedSimpleEdge(bergen, new Edge(bergen, timbuktu), timbuktu);

        network.setGraph(graph);

        return network;
    }

    public static class Vertex implements Serializable {

        private String name;

        private int diagramId;

        public Vertex(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDiagramId() {
            return diagramId;
        }

        public void setDiagramId(int diagramId) {
            this.diagramId = diagramId;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Vertex){
                final Vertex other = (Vertex) obj;
                return new EqualsBuilder()
                        .append(name, other.name)
                        .isEquals();
            } else{
                return false;
            }
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(name)
                    .toHashCode();
        }
    }

    public static class Edge implements Serializable {

        private Network.Vertex from;

        private Network.Vertex to;

        private double weight = 0;

        /**
         * create an edge between the given vertices with weight 1.0
         * @param from
         * @param to
         */
        public Edge(Network.Vertex from, Network.Vertex to) {
            this(from, to, 1.0);
        }

        /**
         * create an edge between vertices with given weight
         * @param from
         * @param to
         * @param weight
         */
        public Edge(Vertex from, Vertex to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Edge) {
                Edge other = (Edge) obj;
                return new EqualsBuilder().
                        append(from, other.from).
                        append(to, other.to).
                        isEquals();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(from)
                    .append(to)
                    .toHashCode();
        }

        public void incrementWeight() {
            weight++;
        }

        public Network.Vertex getFrom() {
            return from;
        }

        public void setFrom(Network.Vertex from) {
            this.from = from;
        }

        public Network.Vertex getTo() {
            return to;
        }

        public void setTo(Network.Vertex to) {
            this.to = to;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }
}
