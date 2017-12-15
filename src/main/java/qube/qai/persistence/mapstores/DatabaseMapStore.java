/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 1/14/17.
 */
public class DatabaseMapStore implements MapStore {

    private Logger logger = LoggerFactory.getLogger("DatabaseMapStore");

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
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(baseClass.cast(value));
            entityManager.getTransaction().commit();
            logger.info("Stored object: " + value.toString() + " successfully");
        } catch (Exception e) {
            logger.error("error while storing " + value, e);
        }
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
        try {
            Object target = load(key);
            if (target != null) {
                entityManager.getTransaction().begin();
                entityManager.remove(baseClass.cast(target));
                entityManager.getTransaction().commit();
                logger.info("Deleted object: " + key + " successfully");
            }
        } catch (Exception e) {
            logger.error("Error while deleting " + baseClass.getSimpleName() + " with uuid: " + key, e);
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
        Object found = null;
        try {
            found = entityManager.find(baseClass, key);
            logger.info("Retrieving object: " + baseClass.getSimpleName() + " with key: " + key + " successfully");
        } catch (Exception e) {
            logger.error("Retrieving object: " + baseClass.getSimpleName() + " with key: " + key + " went wrong with error", e);
        }
        return found;
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
        Query query = entityManager.createQuery("SELECT o.uuid FROM " + baseClass.getSimpleName() + " AS o");
        List list = query.getResultList();
        if (list != null) {
            logger.info("Retrieving all-keys for: " + baseClass.getSimpleName() + " with " + list.size() + " keys");
        } else {
            logger.info("Retrieving all-keys for: " + baseClass.getSimpleName() + " with no keys");
        }
        return list;
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
