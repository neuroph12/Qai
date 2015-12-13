package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;

import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 12/13/15.
 */
public class DirectoryMapStore implements MapStore<String, Object> {

    public void store(String key, Object value) {

    }

    public void storeAll(Map<String, Object> map) {

    }

    public void delete(String key) {

    }

    public void deleteAll(Collection<String> keys) {

    }

    public Object load(String key) {
        return null;
    }

    public Map<String, Object> loadAll(Collection<String> keys) {
        return null;
    }

    public Iterable<String> loadAllKeys() {
        return null;
    }
}
