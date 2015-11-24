package qube.qai.network;

import grph.oo.ObjectGrph;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
public class Network extends ObjectGrph<Network.Vertex, Network.Edge> implements Serializable {


    public Network() {
        super();
    }

    public Matrix getAdjacencyMatrix() {
        Matrix matrix = new Matrix();

        return matrix;
    }

    public int getNumberOfVertices() {
        return super.backingGrph.getNumberOfVertices();
    }

    public int getNumberOfEdges() {
        return super.backingGrph.getNumberOfEdges();
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

        network.addSimpleEdge(vienna, new Edge(vienna, london), london, false);
        network.addSimpleEdge(vienna, new Edge(vienna, mersin), mersin, false);
        network.addSimpleEdge(vienna, new Edge(vienna, bergen), bergen, false);

        network.addSimpleEdge(london, new Edge(london, paris), paris, false);
        network.addSimpleEdge(london, new Edge(london, bergen), bergen, false);
        network.addSimpleEdge(london, new Edge(london, timbuktu), timbuktu, false);
        network.addSimpleEdge(london, new Edge(london, helsinki), helsinki, false);
        network.addSimpleEdge(london, new Edge(london, amsterdam), amsterdam, false);

        network.addSimpleEdge(copenhagen, new Edge(copenhagen, amsterdam), amsterdam, false);
        network.addSimpleEdge(copenhagen, new Edge(copenhagen, helsinki), helsinki, false);
        network.addSimpleEdge(copenhagen, new Edge(copenhagen, bergen), bergen, false);

        network.addSimpleEdge(bergen, new Edge(bergen, amsterdam), amsterdam, false);
        network.addSimpleEdge(bergen, new Edge(bergen, timbuktu), timbuktu, false);

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

        private double weight;

        public Edge(Network.Vertex from, Network.Vertex to) {
            this.from = from;
            this.to = to;
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
