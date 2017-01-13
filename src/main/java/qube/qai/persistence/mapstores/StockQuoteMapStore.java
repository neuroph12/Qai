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
public class StockQuoteMapStore implements MapStore<String, StockQuote> {

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
    public void store(String key, StockQuote value) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(value);
        entityManager.getTransaction().commit();
    }

    @Override
    public void storeAll(Map<String, StockQuote> map) {

        for (String uuid : map.keySet()) {
            StockQuote quote = map.get(uuid);
            store(uuid, quote);
        }
    }

    @Override
    public void delete(String uuid) {
        StockQuote quote = load(uuid);
        if (quote != null) {
            entityManager.remove(quote);
        }
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        for (String uuid : keys) {
            delete(uuid);
        }
    }

    @Override
    public StockQuote load(String uuid) {

        StockQuote quote = entityManager.find(StockQuote.class, uuid);
        return quote;
    }

    @Override
    public Map<String, StockQuote> loadAll(Collection<String> keys) {
        Map<String, StockQuote> results = new HashMap<>();

        for (String key : keys) {
            StockQuote quote = load(key);
            if (quote != null) {
                results.put(key, quote);
            }
        }

        return results;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        return null;
    }
}
