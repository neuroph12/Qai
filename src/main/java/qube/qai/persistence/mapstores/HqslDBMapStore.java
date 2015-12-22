package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 11/26/15.
 */
public class HqslDBMapStore implements MapStore<String, StockEntity> {

    private static Logger logger = LoggerFactory.getLogger("HsqlDBMapStore");

    private boolean debug = true;

    @Inject
    protected EntityManager entityManager;

    /**
     * @TODO a more generic implementation of the class is required
     * @TODO and its test, obviously.
     * this is a very interesting notion for creating a generic
     * database backed map-store really...
     */
    public HqslDBMapStore() { }

    public void store(String key, StockEntity value) {
        entityManager.persist(value);
    }

    public void storeAll(Map<String, StockEntity> map) {
        for (String uuid : map.keySet()) {
            StockEntity entity = map.get(uuid);
            store(uuid, entity);
        }
    }

    public void delete(String key) {
        StockEntity entity = load(key);
        entityManager.remove(entity);
    }

    public void deleteAll(Collection<String> keys) {
        for (String uuid : keys) {
            delete(uuid);
        }
    }

    public StockEntity load(String key) {
        StockEntity entity = entityManager.find(StockEntity.class, key);
        return entity;
    }

    public Map<String, StockEntity> loadAll(Collection<String> keys) {
        Map<String, StockEntity> entityMap = new HashMap<String, StockEntity>();
        for (String uuid : keys) {
            StockEntity entity = load(uuid);
            entityMap.put(uuid, entity);
        }
        return entityMap;
    }

    public Iterable<String> loadAllKeys() {
        List<String> resultList = null;
        if (entityManager != null) {
            resultList = entityManager.createQuery("select uuid from StockQuote").getResultList();
        }
        return resultList;
    }
}
