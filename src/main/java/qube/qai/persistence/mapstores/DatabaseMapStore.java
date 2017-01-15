package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import qube.qai.persistence.StockQuote;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 1/14/17.
 */
public class DatabaseMapStore implements MapStore {

    @Inject
    private EntityManager entityManager;

    private Class baseClass;

    public DatabaseMapStore(Class baseClass) {
        this.baseClass = baseClass;
    }

    public DatabaseMapStore(Class baseClass, EntityManager entityManager) {
        this(baseClass);
        this.entityManager = entityManager;
    }

    @Override
    public void store(Object key, Object value) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(baseClass.cast(value));
        entityManager.getTransaction().commit();
    }

    @Override
    public void storeAll(Map map) {
        for (Object uuid : map.keySet()) {
            Object object = map.get(uuid);
            store(uuid, object);
        }
    }

    @Override
    public void delete(Object key) {
        Object target = load(key);
        if (target != null) {
            entityManager.remove(baseClass.cast(target));
        }
    }

    @Override
    public void deleteAll(Collection keys) {
        for (Object key : keys) {
            delete(key);
        }
    }

    @Override
    public Object load(Object key) {
        return entityManager.find(baseClass, key);
    }

    @Override
    public Map loadAll(Collection keys) {
        Map results = new HashMap();
        for (Object key : keys) {
            Object value = load(key);
            results.put(key, value);
        }
        return results;
    }

    @Override
    public Iterable loadAllKeys() {
        return null;
    }

    public Class getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(Class baseClass) {
        this.baseClass = baseClass;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
