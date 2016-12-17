package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.RDFId;
import qube.qai.persistence.RDFTriple;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.File;
import java.util.*;

/**
 * Created by rainbird on 5/24/16.
 */
public class RdfTripleFileMapStore implements MapStore<String, RDFTriple> {

    private Logger logger = LoggerFactory.getLogger("RdfTripleFileMapStore");

    private String tableName = "RdfTripleSet";

    // the database is defined with its path on file-system
    // once the database base directory is so defined, you can either
    // attach files which are in the directory
    // or alternatively you can set
    // textdb.allow_full_path=true OR java -Dtextdb.allow_full_path=true
    // so that the complete path can be entered for attaching files
    //private String rdfTurtleFile = "/media/rainbird/ALEPH/qai-persistence.db/dbperson_en/persondata_en.ttl";
    private String rdfTurtleFile; // = "persondata_en.ttl";

//    private String createStatement = "CREATE TEXT TABLE IF NOT EXISTS " + tableName
//            + " (subject VARCHAR(255) NOT NULL, predicate VARCHAR(255) NOT NULL, "
//            + "object VARCHAR(255), PRIMARY KEY (subject, predicate)) ";

//    private String createStatement = "CREATE TEXT TABLE IF NOT EXISTS " + tableName
//            + " (subject VARCHAR(256), predicate VARCHAR(256), "
//            + "object VARCHAR(256))";


    //private String assignFileStatement = "SET TABLE " + tableName + " SOURCE \"" + rdfTurtleFile + ";fs=\\space;vs=\\space\"";
//    private String assignFileStatement = "SET TABLE " + tableName + " SOURCE \"dummy.ttl;fs=\\space;vs=\\space;cache_rows=500;cache_size=500\"";

    @Inject
    private EntityManager entityManager;

    private EntityTransaction transaction;

    public RdfTripleFileMapStore() {
    }

    public RdfTripleFileMapStore(String tableName, String rdfTurtleFile) {
        this.tableName = tableName;
        this.rdfTurtleFile = rdfTurtleFile;
    }

    /**
     * checks if the table with tableName is already
     * created in the database, if it doesn't creates the table
     * all you need to do after this is to add the file-source to the
     * table description so that HsqlDb knows how to attach
     * the data to table
     * @return
     */
//    public boolean isTableExists() {
//
//        boolean isTableExists = false;
//
//        if (StringUtils.isBlank(rdfTurtleFile) || StringUtils.isBlank(tableName)) {
//            throw new IllegalArgumentException("Filename: " + rdfTurtleFile + " or table name: " + tableName + " has not been defined");
//        }
//
//        transaction = entityManager.getTransaction();
//        transaction.begin();
//
//        Query createQuery = entityManager.createNativeQuery(createStatement);
//        int retval = createQuery.executeUpdate();
//        if (retval >= 0) {
//            isTableExists = true;
//        }
//
//        return isTableExists;
//    }

    /**
     * based on the csv-file-table feature of HsqlDB this method
     * creates a file-table in the given database
     * this method attaches the file to the table
     */
//    public void createFileTable() {
//
//        if (!isTableExists()) {
//            throw new IllegalArgumentException("Something has gone wrong with table creation- skipping attaching data.");
//        }
//
////        Query dropQuery = entityManager.createNativeQuery("DROP TABLE IF EXISTS " + tableName);
////        dropQuery.executeUpdate();
//
//        Query query = entityManager.createNativeQuery(assignFileStatement);
//        int retval = query.executeUpdate();
//        if (retval >= 0 && transaction != null && transaction.isActive()) {
//            transaction.commit();
//        }
//
//    }

    @Override
    public void store(String key, RDFTriple value) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(value);
        entityManager.getTransaction().commit();
    }

    @Override
    public void storeAll(Map<String, RDFTriple> map) {
        for (RDFTriple triple : map.values()) {
            store(null, triple);
        }
    }

    @Override
    public void delete(String key) {
        RDFTriple triple = load(key);
        if (triple != null) {
            entityManager.remove(triple);
        }
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        for (String id : keys) {
            delete(id);
        }
    }

    @Override
    public RDFTriple load(String key) {
        RDFTriple triple = entityManager.find(RDFTriple.class, key);
        return triple;
    }

    @Override
    public Map<String, RDFTriple> loadAll(Collection<String> keys) {
        Map<String, RDFTriple> triples = new TreeMap<>();

        for (String key : keys) {
            RDFTriple triple = load(key);
            triples.put(key, triple);
        }

        return triples;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        Query query = entityManager.createQuery("SELECT t.uuid FROM RDFTriple t", String.class);
        List<String> results = query.getResultList();
        return results;
    }

    public String getRdfTurtleFile() {
        return rdfTurtleFile;
    }

    public void setRdfTurtleFile(String rdfTurtleFile) {
        this.rdfTurtleFile = rdfTurtleFile;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
