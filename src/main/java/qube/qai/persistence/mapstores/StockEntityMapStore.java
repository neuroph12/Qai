package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockEntityId;
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
public class StockEntityMapStore implements MapStore<StockEntityId, StockEntity> {

    private static Logger logger = LoggerFactory.getLogger("StockEntityMapStore");

    private boolean debug = true;

    @Inject
    protected EntityManager entityManager;

    /**
     * this is a very interesting notion for creating a generic
     * database backed map-store really...
     */
    public StockEntityMapStore() {
    }

    public StockEntityMapStore(EntityManager manager) {
        this.entityManager = manager;
    }

    public void store(StockEntityId key, StockEntity value) {
        entityManager.persist(value);
    }

    public void storeAll(Map<StockEntityId, StockEntity> map) {
        for (StockEntityId uuid : map.keySet()) {
            StockEntity entity = map.get(uuid);
            store(uuid, entity);
        }
    }

    public void delete(StockEntityId key) {
        StockEntity entity = load(key);
        entityManager.remove(entity);
    }

    public void deleteAll(Collection<StockEntityId> keys) {
        for (StockEntityId uuid : keys) {
            delete(uuid);
        }
    }

    public StockEntity load(StockEntityId key) {
        StockEntity entity = entityManager.find(StockEntity.class, key);
        return entity;
    }

    public Map<StockEntityId, StockEntity> loadAll(Collection<StockEntityId> keys) {
        Map<StockEntityId, StockEntity> entityMap = new HashMap<StockEntityId, StockEntity>();
        for (StockEntityId uuid : keys) {
            StockEntity entity = load(uuid);
            entityMap.put(uuid, entity);
        }
        return entityMap;
    }

    public Iterable<StockEntityId> loadAllKeys() {
        List<StockEntityId> resultList = null;
        if (entityManager != null) {
            resultList = entityManager.createQuery("SELECT entity.id FROM StockEntity entity").getResultList();
        }
        return resultList;
    }
}
