package qube.qai.network;

import grph.oo.ObjectGrph;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class Network extends ObjectGrph<Network.Vertex, Network.Edge> implements Serializable, MetricTyped {

    private Logger logger = LoggerFactory.getLogger("Network");

    protected boolean makeMatrix = true;

    protected boolean pruneWeakLinks = true;

    protected double PRUNE_TRESHOLD = 0.1;

    protected Matrix adjacencyMatrix;

    public Network() {
        super();
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
        try {
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
        }

        return metrics;
    }

    public void buildAdjacencyMatrix() {
        // this is when we refuse to create an adjacency matrix
        if (!makeMatrix) {
            return;
        }

        int size = getNumberOfVertices();
        Access2D.Builder<PrimitiveMatrix> builder = PrimitiveMatrix.getBuilder(size, size);

        for (Edge edge : getAllEdges()) {
            Vertex from = edge.getFrom();
            Vertex to = edge.getTo();
            double value = edge.getWeight();
            if (value == 0)  {
                value = 1.0;
            }

            int indexFrom = v2i(from);
            int indexTo = v2i(to);
            builder.set(indexFrom, indexTo, value);
        }

        adjacencyMatrix = new Matrix(builder.build());
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

        boolean useVertexIndices = false;
        if (getVertices() != null) {
            if (adjacencyMatrix.getMatrix().countColumns() == getVertices().size()) {
                useVertexIndices = true;
            }
        }

        PhysicalStore store = adjacencyMatrix.getMatrix().toPrimitiveStore();
        for (int i = 0; i < store.countRows(); i++) {
            for (int j = 0; j < store.countColumns(); j++) {
                Vertex from = null;
                Vertex to = null;
                if (useVertexIndices) {
                    from = i2v(i);
                    to = i2v(j);
                } else {
                    from = new Vertex("vertex " + i);
                    to = new Vertex("vertex " + j);
                }
                Number value = store.get(i, j);
                // check if we need to add an edge at all
                if (value == null || value.doubleValue() == 0) {
                    continue;
                }

                // prune weak links
                if (pruneWeakLinks && value.doubleValue() < PRUNE_TRESHOLD) {
                    continue;
                }

                // create the edge
                Edge edge = new Edge(from, to);
                edge.setWeight(value.doubleValue());
                if (!getAllEdges().contains(edge)) {
                    addUndirectedSimpleEdge(from, edge, to);
                }
            }
        }
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
        return super.backingGrph.getNumberOfVertices();
    }

    public int getNumberOfEdges() {
        return super.backingGrph.getNumberOfEdges();
    }

    @Override
    protected int v2i(Vertex vertex) {
        return super.v2i(vertex);
    }

    @Override
    protected int e2i(Edge edge) {
        return super.e2i(edge);
    }

    @Override
    protected Vertex i2v(int v) {
        return super.i2v(v);
    }

    @Override
    protected Edge i2e(int e) {
        return super.i2e(e);
    }

    @Override
    public Collection<Vertex> getVertices() {
        return super.getVertices();
    }

    @Override
    public Set<Edge> getIncidentEdges(Vertex vertex) {
        return super.getIncidentEdges(vertex);
    }

    public Collection<Edge> getAllEdges() {
        return super.getEdges();
    }

    public double getAverageDegree() {
        return super.backingGrph.getAverageDegree();
    }

    public double getClusteringCoefficient() {
        return super.backingGrph.getClusteringCoefficient();
    }

    public double getDensity() {
        return super.backingGrph.getDensity();
    }

    public double getDiameter() {
        return super.backingGrph.getDiameter();
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
        return super.backingGrph.toString();
    }

    /**
     * a silly method to create a tiny network for playing around
     * @return
     */
    public static Network createTestNetwork() {
        Network network = new Network();

        Vertex vienna = new Vertex("vienna");
        network.addVertex(vienna);

        Vertex london = new Vertex("london");
        network.addVertex(london);

        Vertex paris = new Vertex("paris");
        network.addVertex(paris);

        Vertex bergen = new Vertex("bergen");
        network.addVertex(bergen);

        Vertex timbuktu = new Vertex("timbuktu");
        network.addVertex(timbuktu);

        Vertex mersin = new Vertex("mersin");
        network.addVertex(mersin);

        Vertex helsinki = new Vertex("helsinki");
        network.addVertex(helsinki);

        Vertex amsterdam = new Vertex("amsterdam");
        network.addVertex(amsterdam);

        Vertex copenhagen = new Vertex("copenhagen");
        network.addVertex(copenhagen);

        network.addUndirectedSimpleEdge(vienna, new Edge(vienna, london), london);
        network.addUndirectedSimpleEdge(vienna, new Edge(vienna, mersin), mersin);
        network.addUndirectedSimpleEdge(vienna, new Edge(vienna, bergen), bergen);

        network.addUndirectedSimpleEdge(london, new Edge(london, paris), paris);
        network.addUndirectedSimpleEdge(london, new Edge(london, bergen), bergen);
        network.addUndirectedSimpleEdge(london, new Edge(london, timbuktu), timbuktu);
        network.addUndirectedSimpleEdge(london, new Edge(london, helsinki), helsinki);
        network.addUndirectedSimpleEdge(london, new Edge(london, amsterdam), amsterdam);

        network.addUndirectedSimpleEdge(copenhagen, new Edge(copenhagen, amsterdam), amsterdam);
        network.addUndirectedSimpleEdge(copenhagen, new Edge(copenhagen, helsinki), helsinki);
        network.addUndirectedSimpleEdge(copenhagen, new Edge(copenhagen, bergen), bergen);

        network.addUndirectedSimpleEdge(bergen, new Edge(bergen, amsterdam), amsterdam);
        network.addUndirectedSimpleEdge(bergen, new Edge(bergen, timbuktu), timbuktu);

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

        public Edge(Network.Vertex from, Network.Vertex to) {
            this.from = from;
            this.to = to;
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
