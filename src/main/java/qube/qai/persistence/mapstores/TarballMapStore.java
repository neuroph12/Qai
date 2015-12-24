package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
    private static String filename = "enwiki-20121104-local-media-1.tar";

    /**
     * this class is mainly for retrieving the resources from wiki-tarballs
     */
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
        // nothing to do really
    }

    public void store(String key, Object value) {
        // we are not writing in those files
    }

    public void storeAll(Map<String, Object> map) {
        for (String key : map.keySet()) {
            store(key, map.get(key));
        }
    }

    public void delete(String key) {
        // no idea... just forget about it i suppose
    }

    public void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    public Object load(String key) {

        InputStream stream = null;
        try {
            String filename = directory + TarballMapStore.filename;
            File file = new File(filename);
            TarArchiveInputStream tarInputStream = new TarArchiveInputStream(new FileInputStream(file));
            ArchiveEntry entry = new TarArchiveEntry(key);
            // this means, requested file doesn't exist i suppose
            if (!tarInputStream.canReadEntryData(entry)) {
                return null;
            }
            // do the actual reading
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                // check if we already are at the requested file
                if (!key.equals(entry.getName())) {
                    continue;
                }
                if (!entry.isDirectory()) {
                    int arraySize = (int) entry.getSize();
                    int index = 0;
                    byte[] chars = new byte[arraySize];
                    while (index < entry.getSize()) {
                        chars[index] = (byte) tarInputStream.read();
                        index++;
                    }
                    stream = new ByteArrayInputStream(chars);
                    break;
                }
            }
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
            TarArchiveInputStream tarInputStream = null;

            String filename = directory + TarballMapStore.filename;
            File file = new File(filename);
            tarInputStream = new TarArchiveInputStream(new FileInputStream(file));

            long fileCount = 0;
            long directoryCount = 0;
            TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    File compressedFile = entry.getFile();
                    String name = entry.getName();
                    keys.add(name);
                    logger.info("found filename: " + name);
                    fileCount++;
                } else {
                    directoryCount++;
                }
            }

            logger.info("there are: " + fileCount + "files and " + directoryCount +" directories in the archive");
        } catch (Exception e) {
            String message = "Error while reading zip-filename entries: " + e.getMessage();
            logger.error(message);
        }
        return keys;
    }

    public static String getDirectory() {
        return directory;
    }

    public static void setDirectory(String directory) {
        TarballMapStore.directory = directory;
    }

    public static String getFilename() {
        return filename;
    }

    public static void setFilename(String filename) {
        TarballMapStore.filename = filename;
    }
}
