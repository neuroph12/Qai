package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import qube.qai.persistence.QuoteId;
import qube.qai.persistence.StockQuote;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 5/15/16.
 */
public class StockQuoteMapStore implements MapStore<QuoteId, StockQuote> {

    /**
     * this is the mapstore for the Stock-quotes which as well as having a
     * table in the local database with some of the data. this map-store is supposed to
     * first check if the data is available in the database, and if it is not already
     * in the database, it fetches it from Yahoo-finance web-site and persists a copy
     * in the local database as well, for future references... this way, we will end up
     * only with data which we actually use.
     *
     * stock-quote id's are the combination of ticker-name (parent-id as well?)
     */

    @Inject
    private EntityManager entityManager;

    @Override
    public void store(QuoteId key, StockQuote value) {

    }

    @Override
    public void storeAll(Map<QuoteId, StockQuote> map) {

    }

    @Override
    public void delete(QuoteId key) {

    }

    @Override
    public void deleteAll(Collection<QuoteId> keys) {

    }

    @Override
    public StockQuote load(QuoteId key) {
        return null;
    }

    @Override
    public Map<QuoteId, StockQuote> loadAll(Collection<QuoteId> keys) {
        return null;
    }

    @Override
    public Iterable<QuoteId> loadAllKeys() {
        return null;
    }
}
