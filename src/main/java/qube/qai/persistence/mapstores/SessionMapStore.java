package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.user.Session;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 1/8/17.
 * @Deprecated class is to be replaced by DatabaseMapStore
 */
@Deprecated
public class SessionMapStore implements MapStore<String, Session> {

    private static Logger logger = LoggerFactory.getLogger("SessionMapStore");

    private boolean debug = true;

    @Inject
    protected EntityManager entityManager;

    @Override
    public void store(String key, Session value) {
        entityManager.persist(value);
    }

    @Override
    public void storeAll(Map<String, Session> map) {
        for (String key : map.keySet()) {
            Session session = map.get(key);
            store(key, session);
        }
    }

    @Override
    public void delete(String key) {
        Session session = load(key);
        entityManager.remove(session);
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    @Override
    public Session load(String key) {
        return entityManager.find(Session.class, key);
    }

    @Override
    public Map<String, Session> loadAll(Collection<String> keys) {
        Map<String, Session> sessionsMap = new HashMap<>();
        for (String key : keys) {
            Session session = load(key);
            sessionsMap.put(key, session);
        }
        return sessionsMap;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        return null;
    }
}
