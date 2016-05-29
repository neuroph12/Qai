package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.apache.commons.lang3.StringUtils;
import qube.qai.persistence.RDFTriple;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 5/24/16.
 */
public class RdfTripleFileMapStore implements MapStore<RDFTriple.RDFKey, RDFTriple> {

    private String tableName = "RdfTripleSet";

    // the database is defined with its path on file-system
    // once the database base directory is so defined, you can either
    // attach files which are in the directory
    // or alternatively you can set
    // textdb.allow_full_path=true OR java -Dtextdb.allow_full_path=true
    // so that the complete path can be entered for attaching files
    //private String rdfTurtleFile = "/media/rainbird/ALEPH/qai-persistence.db/dbperson_en/persondata_en.ttl";
    private String rdfTurtleFile; // = "persondata_en.ttl";

    private String createStatement = "CREATE TEXT TABLE IF NOT EXISTS " + tableName + " (subject VARCHAR(256), predicate VARCHAR(256), object VARCHAR(256)) ";

    private String assignFileStatement = "SET TABLE " + tableName + " SOURCE \"" + rdfTurtleFile + ";fs=\\space;vs=\\space\"";

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
    public boolean isTableExists() {

        boolean isTableExists = false;

        if (StringUtils.isBlank(rdfTurtleFile) || StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("Filename: " + rdfTurtleFile + " or table name: " + tableName + " has not been defined");
        }

        transaction = entityManager.getTransaction();
        transaction.begin();

        String statement = createStatement;
        Query query = entityManager.createNativeQuery(statement, Boolean.class);
        int retval = query.executeUpdate();
        if (retval >= 0) {
            isTableExists = true;
        }

        return isTableExists;
    }

    /**
     * based on the csv-file-table feature of HsqlDB this method
     * creates a file-table in the given database
     * this method attaches the file to the table
     */
    public void createFileTable() {

        if (!isTableExists()) {
            throw new IllegalArgumentException("Something has gone wrong with table creation- skipping attaching data.");
        }

        // make sure that the file exists and is accessible
//        File dataFile = new File(rdfTurtleFile);
//        if (!dataFile.exists()
//                || !dataFile.canRead()
//                || !dataFile.canWrite()) {
//            throw new RuntimeException("File is not accessible");
//        }

        Query query = entityManager.createNativeQuery(assignFileStatement);
        int retval = query.executeUpdate();
        if (retval >= 0 && transaction != null && transaction.isActive()) {
            transaction.commit();
        }

    }

    @Override
    public void store(RDFTriple.RDFKey key, RDFTriple value) {

    }

    @Override
    public void storeAll(Map<RDFTriple.RDFKey, RDFTriple> map) {

    }

    @Override
    public void delete(RDFTriple.RDFKey key) {

    }

    @Override
    public void deleteAll(Collection<RDFTriple.RDFKey> keys) {

    }

    @Override
    public RDFTriple load(RDFTriple.RDFKey key) {
        return null;
    }

    @Override
    public Map<RDFTriple.RDFKey, RDFTriple> loadAll(Collection<RDFTriple.RDFKey> keys) {
        return null;
    }

    @Override
    public Iterable<RDFTriple.RDFKey> loadAllKeys() {
        return null;
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
