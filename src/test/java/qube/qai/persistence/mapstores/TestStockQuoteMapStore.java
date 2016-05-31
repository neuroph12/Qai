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

    private String[] names = {"HRS", "GGP", "CI", "LMT", "TAP"};

    public void testStockQuoteMapStore() throws Exception {

        Injector injector = Guice.createInjector(new JpaPersistModule("STOCKS"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        injector.injectMembers(this);

        for (int i = 0; i < names.length; i++) {
            Collection<QuoteId> keys = createKeys(names[i]);
            assertNotNull(keys);

            int count = 0;
            for (Iterator<QuoteId> it = keys.iterator(); it.hasNext(); ) {
                QuoteId key = it.next();
                StockQuote quote = mapStore.load(key);
                assertNotNull("Quote shoud not be null", quote);
                String message = "Quote: " + quote.getTickerSymbol()
                        + " date: " + quote.getId().getDate()
                        + " adj-close: " + quote.getAdjustedClose();
                logger.info(message);
                count++;
            }

            logger.info("found: " + keys.size() + " listed: " + count);

            Map<QuoteId, StockQuote> result = mapStore.loadAll(keys);
            assertNotNull(result);

        }


    }

    private Collection<QuoteId> createKeys(String name) {
        Collection<QuoteId> keys = new ArrayList<>();

        StockQuoteDataStore dataStore = new StockQuoteDataStore();
        Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(name);

        for (StockQuote quote : quotes) {
            QuoteId id = quote.getId();
            if (id == null) {
                fail("QuoteId should not be null");
            }
            keys.add(id);
        }

        return keys;
    }
}

