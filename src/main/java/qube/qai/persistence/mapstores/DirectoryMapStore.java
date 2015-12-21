package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.Procedure;

import java.io.*;
import java.util.*;

/**
 * Created by rainbird on 12/13/15.
 */
public class DirectoryMapStore implements MapStore<String, Procedure> {

    private Logger logger = LoggerFactory.getLogger("DirectoryMapStore");

    private String directory;

    public DirectoryMapStore() {
        // any initialization required here?
    }

    public DirectoryMapStore(String directory) {
        this();
        this.directory = directory;
        // there are better ways of doing this really
        if (!this.directory.endsWith("/")) {
            this.directory = directory + "/";
        }
    }

    public void store(String key, Procedure value) {
        try {
            String filename = directory + key;
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput out = new ObjectOutputStream(fos);
            out.writeObject(value);
            out.close();
        } catch (IOException e) {
            logger.error("error while writing " + key + " " + e.getMessage());
        }
    }

    public void storeAll(Map<String, Procedure> map) {
        for (String name : map.keySet()) {
            Procedure procedure = map.get(name);
            store(name, procedure);
        }
    }

    public void delete(String key) {
        String filename = directory + key;
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    public void deleteAll(Collection<String> keys) {
        for (String name : keys) {
            delete(name);
        }
    }

    public Procedure load(String key) {
        Procedure procedure = null;
        try {
            String filename = directory + key;
            File file = new File(filename);
            InputStream fin = new FileInputStream(file);
            ObjectInputStream inStream = new ObjectInputStream(fin);
            procedure = (Procedure) inStream.readObject();
        } catch (FileNotFoundException e) {
            logger.error("error while reading " + key + " " + e.getMessage());
        } catch (IOException e) {
            logger.error("error while reading " + key + " " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("error while reading " + key + " " + e.getMessage());
        }
        return procedure;
    }

    public Map<String, Procedure> loadAll(Collection<String> keys) {

        Map<String, Procedure> procedures = new HashMap<String, Procedure>();
        File file = new File(directory);
        String[] filenames = file.list();
        for (int i = 0; i < filenames.length; i++) {
            String name = filenames[i];
            Procedure procedure = load(name);
            if (procedure != null) {
                procedures.put(name, procedure);
            }
        }

        return procedures;
    }

    public Iterable<String> loadAllKeys() {
        List<String> filenames = new ArrayList<String>();
        File file = new File(directory);
        String[] names = file.list();
        if (names == null) {
            return filenames;
        }
        for (String name : names) {
            filenames.add(name);
        }
        return filenames;
    }
}
