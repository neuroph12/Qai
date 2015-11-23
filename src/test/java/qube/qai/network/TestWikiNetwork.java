package qube.qai.network;

import com.google.inject.name.Named;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.network.semantic.SemanticNetwork;
import qube.qai.network.wiki.WikiNetwork;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.util.Collection;
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

    /**
     * this is in order to test the semantic network building algorithms
     * we'll see how long it take to build up a semantic network of
     * a given text's content.
     * @throws Exception
     */
    public void testSemanticNetwork() throws Exception {
        Collection<String> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next();
        log("name for the test case: " + filename);
        WikiArticle wikiArticle = wikipediaSearchService.retrieveDocumentContentFromZipFile(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        SemanticNetwork semanticNetwork = new SemanticNetwork();
        semanticNetwork.buildNetwork(wikiArticle);

        logNetwork(semanticNetwork);
    }

    /**
     * not a test which you want to do very often
     * creating network took: 786628 ms
     * Network experiment number of vertices: 185842
     * Network experiment number of edges: 783178
     * Network experiment average degree: 8.428428449973634
     * @throws Exception
     */
    public void restWikiNetwork() throws Exception {

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

    /**
     * basic test really- and checking out the equals method on vertex is actually used
     * the trick is to implement both hash and equals
     * @throws Exception
     */
    public void restQaiNetwork() throws Exception {
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
    }

    private void logNetwork(Network network) {
        log("Network number of vertices: " + network.getNumberOfVertices());
        log("Network number of edges: " + network.getNumberOfEdges());
        log("Network average degree: " + network.getAverageDegree());
        //log("Network clustering coefficient: " + network.getClusteringCoefficient());
        log("Network density: " + network.getDensity());
        //log("Network diameter: " + network.getDiameter());
    }


//    public void restGRPHNetwork() {
//        // this line is recommended by the authors of grph-library
//        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
//
//        ObjectGrph graph = new ObjectGrph<String, String>();
//        //IntSet vertices = graph.getVertices();
//        String[] vertices = {"paris", "london", "vienna", "luxemburg", "varsaw", };
//
//        for (String vertex : vertices) {
//            graph.addVertex(vertex);
//        }
//
//        graph.addSimpleEdge("paris", "paris-vienna", "vienna", false);
//        graph.addSimpleEdge("london", "london-vienna", "vienna", false);
//        graph.addSimpleEdge("paris", "paris-luxemburg", "luxemburg", false);
//        graph.addSimpleEdge("paris", "paris-varsaw", "varsaw", false);
//        graph.addSimpleEdge("vienna", "vienna-luxemburg", "luxemburg", false);
//        graph.addSimpleEdge("vienna", "vienna-varsaw", "varsaw", false);
//        graph.addSimpleEdge("varsaw", "varsaw-luxemburg", "luxemburg", false);
//
//        Collection<String> viennaEdges = graph.getIncidentEdges("vienna");
//        log("vienna's edges:");
//        for (String edge : viennaEdges) {
//            log(edge);
//        }
//
//        //graph.getD;
//    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
