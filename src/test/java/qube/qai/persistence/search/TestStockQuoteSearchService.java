package qube.qai.persistence.search;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.persistence.StockQuote;
import qube.qai.services.implementation.SearchResult;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * Created by rainbird on 6/1/16.
 */
public class TestStockQuoteSearchService extends TestCase {

    // copied from another test- TestStockQuoteMapStore which is supposed to insert the values as well
    private String[] names = {"HRS"};

    private EntityManager manager;

    public void testStockQuoteSearchService() throws Exception {

        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_STOCKS"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        manager = injector.getInstance(EntityManager.class);

        for (int i = 0; i < names.length; i++) {
            createTestData(names[i]);
        }

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

    private void createTestData(String name) {
        StockQuoteDataStore dataStore = new StockQuoteDataStore();
        Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(name);

        manager.getTransaction().begin();
        for (StockQuote quote : quotes) {
            manager.persist(quote);
        }

        manager.getTransaction().commit();
    }

    private void log(String message) {
        System.out.println(message);
    }
}
