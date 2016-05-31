package qube.qai.persistence.search;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 6/1/16.
 */
public class TestStockQuoteSearchService extends TestCase {

    // copied from another test- TestStockQuoteMapStore which is suppsoed to insert the values as well
    private String[] names = {"HRS", "GGP", "CI", "LMT", "TAP"};

    public void testStockQuoteTestService() throws Exception {

        // begin with creating the search service
        Injector injector = Guice.createInjector(new JpaPersistModule("STOCKS"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        StockQuoteSearchService searchService = new StockQuoteSearchService();
        injector.injectMembers(searchService);

        for (int i = 0; i < names.length; i++) {
            Collection<SearchResult> results = searchService.searchInputString(names[i], "tickerSymbol", 100);
            assertNotNull(results);
            assertTrue(results.size() > 0);
            log("found " + results.size() + " quotes for the entity: " + names[i]);
            for (SearchResult result : results) {
                log("found: " + result.getFilename());
            }
        }
    }

    private void log(String message) {
        System.out.println(message);
    }
}
