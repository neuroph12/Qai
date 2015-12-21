package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 12/21/15.
 */
public class WikiTarballMapStore implements MapStore<String, InputStream> {

    private Logger logger = LoggerFactory.getLogger("WikiTarballMapStore");

    public void store(String key, InputStream value) {

    }

    public void storeAll(Map<String, InputStream> map) {

    }

    public void delete(String key) {

    }

    public void deleteAll(Collection<String> keys) {

    }

    public InputStream load(String key) {
        return null;
    }

    public Map<String, InputStream> loadAll(Collection<String> keys) {
        return null;
    }

    public Iterable<String> loadAllKeys() {
        return null;
    }
}
