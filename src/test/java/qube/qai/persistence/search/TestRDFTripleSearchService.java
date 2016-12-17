package qube.qai.persistence.search;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import org.apache.jena.propertytable.graph.GraphCSV;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 6/1/16.
 */
public class TestRDFTripleSearchService extends TestCase {

    private String[][] rdfids = {{"http://dbpedia.org/resource/Aristotle", "region"},
            {"http://dbpedia.org/resource/Autism", "diseasesdb"},
            {"http://dbpedia.org/resource/Autism", "emedicineTopic"},
            {"http://dbpedia.org/resource/Aristotle", "deathYear"}};

    public void restSearchService() throws Exception {

        // begin with creating the search service
        Injector injector = Guice.createInjector(new JpaPersistModule("DBPEDIA"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        RDFTriplesSearchService searchService = new RDFTriplesSearchService();
        injector.injectMembers(searchService);

        // now we can proceed with the proper testing
        for (int i = 0; i < rdfids.length; i++) {
            String term = rdfids[i][0] + "|" + rdfids[i][1];
            Collection<SearchResult> results = searchService.searchInputString(term, null, 100);
            assertNotNull(results);
            assertTrue(!results.isEmpty());
            for (SearchResult result : results) {
                log("found uuid: " + result.getFilename());
            }
        }

    }


    private void log(String message) {
        System.out.println(message);
    }
}
