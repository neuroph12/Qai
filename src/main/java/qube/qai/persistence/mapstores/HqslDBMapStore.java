package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 11/26/15.
 */
public class HqslDBMapStore implements MapStore<String, StockEntity> {

    private static Logger logger = LoggerFactory.getLogger("HsqlDBMapStore");

    private boolean debug = true;

    // varchar(45) was the original- changed that to string
    private String filename;
    private String createTable = "create table if not exists StockQuote " +
            "(uuid String, " +
            "tickerSymbol String, " +
            "security String, " +
            "secFilings String," +
            "gicsSector String," +
            "gicsSubIndustry String," +
            "address String," +
            "dateFristAdded Date)";
    //private final Connection con;

    @Inject
    protected EntityManager entityManager;
    /**
     * @TODO a more generic implementation of the class is required
     * @TODO and its test, obviously.
     * this is a very interesting notion for creating a generic
     * database backed map-store really...
     */
    public HqslDBMapStore() {
//        String connString = "jdbc:hsqldb:" + filename;
//        try {
//            con = DriverManager.getConnection(connString, "SA", "");
//            con.createStatement().executeUpdate(createTable);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void store(String key, StockEntity value) {
//        try {
//            String statement = ""; //format("insert into person values(%s,'%s')", key, value.name)
//            con.createStatement().executeUpdate(statement);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        entityManager.persist(value);
    }

    public void storeAll(Map<String, StockEntity> map) {
        for (String uuid : map.keySet()) {
            StockEntity entity = map.get(uuid);
            store(uuid, entity);
        }
    }

    public void delete(String key) {
        StockEntity entity = load(key);
        entityManager.remove(entity);
    }

    public void deleteAll(Collection<String> keys) {
        for (String uuid : keys) {
            delete(uuid);
        }
    }

    public StockEntity load(String key) {
//        try {
//            String statement = ""; // format("select name from person where id =%s", key)
//            ResultSet resultSet = con.createStatement().executeQuery(statement);
//            try {
//                if (!resultSet.next()) return null;
//                String name = resultSet.getString(1);
//                return new StockEntity();
//            } finally {
//                resultSet.close();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        StockEntity entity = entityManager.find(StockEntity.class, key);
        return entity;
    }

    public Map<String, StockEntity> loadAll(Collection<String> keys) {
        Map<String, StockEntity> entityMap = new HashMap<String, StockEntity>();
        for (String uuid : keys) {
            StockEntity entity = load(uuid);
            entityMap.put(uuid, entity);
        }
        return entityMap;
    }

    public Iterable<String> loadAllKeys() {
        List<String> resultList = entityManager.createQuery("select uuid from StockQuote").getResultList();
        return resultList;
    }
}
