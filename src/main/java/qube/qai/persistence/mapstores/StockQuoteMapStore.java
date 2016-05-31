package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.persistence.QuoteId;
import qube.qai.persistence.StockQuote;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.*;

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

    // StockQuoteDataStore doesn't have any setting or anything- easy :-)
    private StockQuoteDataStore dataStore;

    public StockQuoteMapStore() {
        dataStore = new StockQuoteDataStore();
    }

    @Override
    public void store(QuoteId key, StockQuote value) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(value);
        entityManager.getTransaction().commit();
    }

    @Override
    public void storeAll(Map<QuoteId, StockQuote> map) {

        Set<QuoteId> keySet = map.keySet();
        for (Iterator<QuoteId> it = keySet.iterator(); it.hasNext(); ) {
            QuoteId key = it.next();
            StockQuote quote = map.get(key);
            store(key, quote);
        }
    }

    @Override
    public void delete(QuoteId key) {
        StockQuote quote = load(key);
        if (quote != null) {
            entityManager.remove(quote);
        }
    }

    @Override
    public void deleteAll(Collection<QuoteId> keys) {
        for (Iterator<QuoteId> it = keys.iterator(); it.hasNext(); ) {
            delete(it.next());
        }
    }

    @Override
    public StockQuote load(QuoteId key) {

        StockQuote quote = entityManager.find(StockQuote.class, key);
        if (quote == null
                && !dataStore.isProvided(key.getTickerSymbol())) {
            Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(key.getTickerSymbol());
            for (StockQuote current : quotes) {
                if (current.getId().equals(key)) {
                    quote = current;
                }
                store(current.getId(), current);
            }
        }

        return quote;
    }

    @Override
    public Map<QuoteId, StockQuote> loadAll(Collection<QuoteId> keys) {
        Map<QuoteId, StockQuote> results = new HashMap<>();

        for (QuoteId key : keys) {
            StockQuote quote = load(key);
            if (quote != null) {
                results.put(key, quote);
            }
        }

        return results;
    }

    @Override
    public Iterable<QuoteId> loadAllKeys() {
        return null;
    }
}
