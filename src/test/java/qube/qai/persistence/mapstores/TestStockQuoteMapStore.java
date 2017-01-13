package qube.qai.persistence.mapstores;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.persistence.QuoteId;
import qube.qai.persistence.StockQuote;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rainbird on 5/15/16.
 */
public class TestStockQuoteMapStore extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("TestStockQuoteMapStore");

    @Inject
    private StockQuoteMapStore mapStore;

    //private String[] names = {"HRS", "GGP", "CI", "LMT", "TAP"};
    private String[] names = {"HRS"};

    public void testStockQuoteMapStore() throws Exception {

        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_STOCKS"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        injector.injectMembers(this);

        for (int i = 0; i < names.length; i++) {
            Collection<String> keys = createKeys(names[i]);
            assertNotNull(keys);

            int count = 0;
            for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
                String key = it.next();
                StockQuote quote = mapStore.load(key);
                assertNotNull("Quote shoud not be null", quote);
                String message = "Quote: " + quote.getTickerSymbol()
                        + " date: " + quote.getQuoteDate()
                        + " adj-close: " + quote.getAdjustedClose();
                logger.info(message);
                count++;
            }

            logger.info("found: " + keys.size() + " listed: " + count);

            Map<String, StockQuote> result = mapStore.loadAll(keys);
            assertNotNull(result);

        }

    }

    private Collection<String> createKeys(String name) {
        Collection<String> keys = new ArrayList<>();

        StockQuoteDataStore dataStore = new StockQuoteDataStore();
        Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(name);

        for (StockQuote quote : quotes) {
            keys.add(quote.getUuid());
        }

        return keys;
    }
}

