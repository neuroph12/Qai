package qube.qai.network;


import com.google.inject.name.Named;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.network.semantic.SemanticNetwork;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by rainbird on 11/24/15.
 */
public class TestSemanticNetwork extends QaiBaseTestCase {

    private boolean debug = true;

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

        SemanticNetwork semanticNetwork = new SemanticNetwork();
        semanticNetwork.buildNetwork(wikiArticle);

        logNetwork(semanticNetwork);
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
