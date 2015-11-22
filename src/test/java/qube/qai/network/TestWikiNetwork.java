package qube.qai.network;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import grph.oo.ObjectGrph;
import org.junit.Test;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.main.QaiTestModule;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.util.Collection;
import junit.framework.TestCase;
/**
 * Created by rainbird on 11/21/15.
 */
public class TestWikiNetwork extends QaiBaseTestCase {

    private boolean debug = true;

    @Inject @Named("Wiktionary_en")
    private SearchServiceInterface wikipediaSearchService;


    @Override
    protected void setUp() throws Exception {
       super.setUp();
    }

    public void testWikiNetwork() throws Exception {


        Collection<String> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next();
        log("name for the test case: " + filename);
        WikiArticle wikiArticle = wikipediaSearchService.retrieveDocumentContentFromZipFile(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        // now feed it to wiki-network class and build a network
        WikiNetwork network = new WikiNetwork();
        injector.injectMembers(network);

        long start = System.currentTimeMillis();
        log("started network-building process...");
        network.buildNetwork(wikiArticle);
        long duration = System.currentTimeMillis() - start;
        log("creating network took: " + duration + " ms");
        logNetwork(network);
    }

    public void restQaiNetwork() throws Exception {
        // this line is recommended by the authors of grph-library
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        QaiNetwork network = QaiNetwork.createTestNetwork();

        logNetwork(network);

        Collection<QaiNetwork.Vertex> vertices = network.getVertices();
        for (QaiNetwork.Vertex vertex : vertices) {
            String name = vertex.getName();
            QaiNetwork.Vertex searchVertex = new QaiNetwork.Vertex(name);
            assertTrue("has to use equals!!!", network.containsVertex(searchVertex));
        }
    }

    private void logNetwork(QaiNetwork network) {
        log("Network experiment number of vertices: " + network.getNumberOfVertices());
        log("Network experiment number of edges: " + network.getNumberOfEdges());
        log("Network experiment average degree: " + network.getAverageDegree());
        //log("Network experiment clustering coefficient: " + network.getClusteringCoefficient());
        log("Network experiment density: " + network.getDensity());
        //log("Network experiment diameter: " + network.getDiameter());
    }


    public void restGRPHNetwork() {
        // this line is recommended by the authors of grph-library
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        ObjectGrph graph = new ObjectGrph<String, String>();
        //IntSet vertices = graph.getVertices();
        String[] vertices = {"paris", "london", "vienna", "luxemburg", "varsaw", };

        for (String vertex : vertices) {
            graph.addVertex(vertex);
        }

        graph.addSimpleEdge("paris", "paris-vienna", "vienna", false);
        graph.addSimpleEdge("london", "london-vienna", "vienna", false);
        graph.addSimpleEdge("paris", "paris-luxemburg", "luxemburg", false);
        graph.addSimpleEdge("paris", "paris-varsaw", "varsaw", false);
        graph.addSimpleEdge("vienna", "vienna-luxemburg", "luxemburg", false);
        graph.addSimpleEdge("vienna", "vienna-varsaw", "varsaw", false);
        graph.addSimpleEdge("varsaw", "varsaw-luxemburg", "luxemburg", false);

        Collection<String> viennaEdges = graph.getIncidentEdges("vienna");
        log("vienna's edges:");
        for (String edge : viennaEdges) {
            log(edge);
        }

        //graph.getD;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
