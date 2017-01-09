package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.StockEntity;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 1/8/17.
 */
public class UserMapStore implements MapStore<String, User> {

    private static Logger logger = LoggerFactory.getLogger("UserMapStore");

    private boolean debug = true;

    @Inject
    protected EntityManager entityManager;

    public UserMapStore() {
    }

    public UserMapStore(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void store(String key, User value) {
        entityManager.persist(value);
    }

    @Override
    public void storeAll(Map<String, User> map) {
        for (String key : map.keySet()) {
            User user = map.get(key);
            store(key, user);
        }
    }

    @Override
    public void delete(String key) {
        User user = load(key);
        entityManager.remove(user);
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    @Override
    public User load(String key) {
        return entityManager.find(User.class, key);
    }

    @Override
    public Map<String, User> loadAll(Collection<String> keys) {
        Map<String, User> userMap = new HashMap<>();
        for (String key : keys) {
            User user = load(key);
            userMap.put(key, user);
        }
        return userMap;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        return null;
    }
}
