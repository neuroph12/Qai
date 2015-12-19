package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;

import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 11/19/15.
 */
public class ZipFileMapStore implements MapStore<String, Procedure> {

    private boolean debug;

    private static String directory = "/media/rainbird/ALEPH/wiki-data/";
    private static String file = "enwiki-20121104-local-media-1.tar";

    public ZipFileMapStore() {
        // whatever you need to do here
        this.debug = true;
    }

    public void store(String key, Procedure value) {

    }

    public void storeAll(Map<String, Procedure> map) {

    }

    public void delete(String key) {

    }

    public void deleteAll(Collection<String> keys) {

    }

    public Procedure load(String key) {
        return null;
    }

    public Map<String, Procedure> loadAll(Collection<String> keys) {
        return null;
    }

    public Iterable<String> loadAllKeys() {
        return null;
    }
}
