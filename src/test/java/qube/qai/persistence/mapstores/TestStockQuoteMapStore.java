package qube.qai.persistence.mapstores;

import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.main.QaiTestBase;
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
public class TestStockQuoteMapStore extends QaiTestBase {

    @Inject
    private StockQuoteMapStore mapStore;

    public void restSilly() {
        assertTrue(true);
    }

    public void testStockQuoteMapStore() throws Exception {

//        Injector childInjector = injector.createChildInjector(new JpaPersistModule("STOCKS"));
//        PersistService service = childInjector.getInstance(PersistService.class);
//        service.start();
//
//        StockQuoteMapStore mapStore = new StockQuoteMapStore();
        injector.injectMembers(this);

        Collection<QuoteId> keys = createKeys();
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

    private Collection<QuoteId> createKeys() {
        Collection<QuoteId> keys = new ArrayList<>();

        StockQuoteDataStore dataStore = new StockQuoteDataStore();
        Collection<StockQuote> quotes = dataStore.retrieveQuotesFor("HRS");

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

