package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import qube.qai.persistence.WikiArticle;

import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 11/19/15.
 */
public class ZipFileMapStore implements MapStore<String, WikiArticle> {

    private boolean debug;

    public ZipFileMapStore() {
        // whatever you need to do here
        this.debug = true;
    }

    public ZipFileMapStore(boolean debug) {
        this.debug = debug;
    }

    public void store(String key, WikiArticle value) {

    }

    public void storeAll(Map<String, WikiArticle> map) {

    }

    public void delete(String key) {

    }

    public void deleteAll(Collection<String> keys) {

    }

    public WikiArticle load(String key) {
        return null;
    }

    public Map<String, WikiArticle> loadAll(Collection<String> keys) {
        return null;
    }

    public Iterable<String> loadAllKeys() {
        return null;
    }
}
