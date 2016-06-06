package qube.qai.services.implementation;

import qube.qai.main.QaiTestBase;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

/**
 * Created by rainbird on 1/6/16.
 */
public class TestDistributedSearchServices extends QaiTestBase {

    @Inject @Named("Wikipedia_en")
    SearchServiceInterface wikiSearchService;

    @Inject @Named("Stock_Quotes")
    SearchServiceInterface quotesSearchService;

    @Inject @Named("Dbpedia_en")
    SearchServiceInterface dbpediaSearchService;

    public void testDistributedWikiSearch() throws Exception {

        // make an instance of the thing
        DistributedSearchListener searchListener = new DistributedSearchListener("Wikipedia_en");
        searchListener.setSearchService(wikiSearchService);
        injector.injectMembers(searchListener);

        searchListener.initialize();


        String topicName = "Wikipedia_en";
        DistributedSearchService distributedSearch = new DistributedSearchService(topicName);
        injector.injectMembers(distributedSearch);
        distributedSearch.initialize();

        Collection<SearchResult> results = distributedSearch.searchInputString("mouse", "title", 100);
        assertNotNull("have to return something", results);
        assertTrue("has to be something in there as well", !results.isEmpty());
        for (SearchResult result : results) {
            logger.info("found result: " + result.getTitle());
        }
    }

    public void testDistributedStockQuoteSearch() throws Exception {

        // make an instance of the thing
        DistributedSearchListener searchListener = new DistributedSearchListener("Stock_Quotes");
        searchListener.setSearchService(quotesSearchService);
        injector.injectMembers(searchListener);

        searchListener.initialize();

        String topicName = "Stock_Quotes";
        DistributedSearchService distributedSearch = new DistributedSearchService(topicName);
        injector.injectMembers(distributedSearch);
        distributedSearch.initialize();

        Collection<SearchResult> results = distributedSearch.searchInputString("HRS", "TICKERSYMBOL", 100);
        assertNotNull("have to return something", results);
        assertTrue("has to be something in there as well", !results.isEmpty());
        for (SearchResult result : results) {
            logger.info("found result: " + result.getTitle());
        }
    }

    public void testDistributedRdfTripleSearch() throws Exception {

        // make an instance of the thing
        DistributedSearchListener searchListener = new DistributedSearchListener("Dbpedia_en");
        searchListener.setSearchService(dbpediaSearchService);
        injector.injectMembers(searchListener);

        searchListener.initialize();

        String topicName = "Dbpedia_en";
        DistributedSearchService distributedSearch = new DistributedSearchService(topicName);
        injector.injectMembers(distributedSearch);
        distributedSearch.initialize();

        Collection<SearchResult> results = distributedSearch.searchInputString("Aristotle", "OBJECT", 100);
        assertNotNull("have to return something", results);
        assertTrue("has to be something in there as well", !results.isEmpty());
        for (SearchResult result : results) {
            logger.info("found result: " + result.getTitle());
        }
    }
}
