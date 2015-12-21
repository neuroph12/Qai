package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by rainbird on 11/19/15.
 */
public class TarballMapStore implements MapStore<String, Object> {

    private static Logger logger = LoggerFactory.getLogger("TarballMapStore");

    private boolean debug;

    private static String directory = "/media/rainbird/ALEPH/wiki-data/";
    private static String file = "enwiki-20121104-local-media-1.tar";

    private String filename;

    private ZipFile zipFile;

    public TarballMapStore() {
        // whatever you need to do here
        this.debug = true;

    }

    public TarballMapStore(String filename) {
        this();
        this.filename = filename;
        init();
    }

    private void init() {
        try {
            zipFile = new ZipFile(filename);
        } catch (IOException e) {
            String message = "Error while opening zip-file: " + filename + ": " + e.getMessage();
            logger.error(message);
        }
    }

    public void store(String key, Object value) {

    }

    public void storeAll(Map<String, Object> map) {

    }

    public void delete(String key) {

    }

    public void deleteAll(Collection<String> keys) {

    }

    public Object load(String key) {

        InputStream stream = null;
        try {
            ZipEntry entry = zipFile.getEntry(key);
            if (entry == null) {
                logger.error("No entry with name: '" + key + "' found in this archive- skipping");
                return stream;
            }
            stream = zipFile.getInputStream(entry);
        } catch (IOException e) {
            String message = "Error while reading stream for '" + key + "': "  + e.getMessage();
            logger.error(message);
        }

        return stream;
    }

    public Map<String, Object> loadAll(Collection<String> keys) {
        return null;
    }

    public Iterable<String> loadAllKeys() {
        List<String> keys = new ArrayList<String>();
        try {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                keys.add(zipEntries.nextElement().getName());
            }
        } catch (Exception e) {
            String message = "Error while reading zip-file entries: " + e.getMessage();
            logger.error(message);
        }
        return keys;
    }
}
