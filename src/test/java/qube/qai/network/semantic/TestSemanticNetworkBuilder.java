package qube.qai.network.semantic;


import com.google.inject.name.Named;
import qube.qai.data.Metrics;
import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.main.QaiTestBase;
import qube.qai.network.Network;
import qube.qai.network.semantic.SemanticNetwork;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by rainbird on 11/24/15.
 */
public class TestSemanticNetworkBuilder extends QaiTestBase {

    @Inject
    @Named("Wiktionary_en")
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
        Collection<SearchResult> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next().getFilename();
        log("name for the test case: " + filename);
        WikiArticle wikiArticle = wikipediaSearchService.retrieveDocumentContentFromZipFile(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        SemanticNetworkBuilder builder = new SemanticNetworkBuilder();
        Selector<WikiArticle> selector = new DataSelector<WikiArticle>(wikiArticle);
        SemanticNetwork semanticNetwork = (SemanticNetwork) builder.buildNetwork(selector);

        logNetwork(semanticNetwork);
    }

    /**
     * check that the basic properties of the networks are the same
     */
    public void testAdjacencyMatrix() throws Exception {
        Collection<SearchResult> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next().getFilename();
        log("name for the test case: " + filename);
        WikiArticle wikiArticle = wikipediaSearchService.retrieveDocumentContentFromZipFile(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        SemanticNetworkBuilder builder = new SemanticNetworkBuilder();
        Selector<WikiArticle> selector = new DataSelector<WikiArticle>(wikiArticle);
        SemanticNetwork semanticNetwork = (SemanticNetwork) builder.buildNetwork(selector);
        semanticNetwork.buildAdjacencyMatrix();
        logNetwork(semanticNetwork);

        // now we try to build another network from the adjacency matrix
        Network network = new Network();
        network.buildFromAdjacencyMatrix(semanticNetwork.getAdjacencyMatrix());

        Metrics sematicMetrics = semanticNetwork.buildMetrics();
        Metrics copyMetrics = network.buildMetrics();

        double semVertexCount = (Double) sematicMetrics.getValue("number of vertices");
        double copyVertexCount = (Double) copyMetrics.getValue("number of vertices");
        log("vertex counts, original: " + semVertexCount + " copy: " + copyVertexCount);
        assertTrue("number of vertices must be same",  semVertexCount == copyVertexCount);

        double semEdgeCount = (Double) sematicMetrics.getValue("number of edges");
        double copyEdgeCount = (Double) copyMetrics.getValue("number of edges");
        log("edge counts, original: " + semEdgeCount + " copy: " + copyEdgeCount);
        assertTrue("number of edges", semEdgeCount == copyEdgeCount);
    }

    private void logNetwork(Network network) {
        log("Network number of vertices: " + network.getNumberOfVertices());
        log("Network number of edges: " + network.getNumberOfEdges());
        //log("Network average degree: " + network.getAverageDegree());
        //log("Network clustering coefficient: " + network.getClusteringCoefficient());
        //log("Network density: " + network.getDensity());
        //log("Network diameter: " + network.getDiameter());
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
            //logger.info(message);
        }
    }
}