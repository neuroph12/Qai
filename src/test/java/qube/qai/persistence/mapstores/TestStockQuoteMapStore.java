package qube.qai.persistence.mapstores;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.QuoteId;
import qube.qai.persistence.StockQuote;

import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 5/15/16.
 */
public class TestStockQuoteMapStore extends QaiTestBase {

    //private Injector injector;

//    @Override
//    protected void setUp() throws Exception {
//
//        //injector = Guice.createInjector(new JpaPersistModule("STOCKS"));
//
//
//    }

    public void testStockQuoteMapStore() throws Exception {

        this.injector = injector.createChildInjector(new JpaPersistModule("STOCKS"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        StockQuoteMapStore mapStore = new StockQuoteMapStore();
        injector.injectMembers(mapStore);

        Collection<QuoteId> keys = createKeys();
        assertNotNull(keys);
        Map<QuoteId, StockQuote> result = mapStore.loadAll(keys);
        assertNotNull(result);

    }

    private Collection<QuoteId> createKeys() {
        return null;
    }
}

