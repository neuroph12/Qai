package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import qube.qai.persistence.StockQuote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 11/26/15.
 */
public class HqslDBMapStore implements MapStore<String, StockQuote> {

    private boolean debug = true;

    private final Connection con;

    public HqslDBMapStore() {
        try {
            con = DriverManager.getConnection("jdbc:hsqldb:mydatabase", "SA", "");
            con.createStatement().executeUpdate(
                    "create table if not exists person (id bigint, name varchar(45))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void delete(String key) {
        log("Delete:" + key);
        try {
            String statement = "delete from person where id = %s" + key;
            con.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void store(String key, StockQuote value) {
        try {
            String statement = "insert into person values(%s,'%s')";
            con.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void storeAll(Map<String, StockQuote> map) {
        for (Map.Entry<String, StockQuote> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());
    }

    public synchronized void deleteAll(Collection<String> keys) {
        for (String key : keys) delete(key);
    }

    public synchronized StockQuote load(String key) {
        try {
            String statement = "select name from person where id =%s" + key;
            ResultSet resultSet = con.createStatement().executeQuery(statement);
            try {
                if (!resultSet.next()) return null;
                String name = resultSet.getString(1);
                return new StockQuote();
            } finally {
                resultSet.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Map<String, StockQuote> loadAll(Collection<String> keys) {
        Map<String, StockQuote> result = new HashMap<String, StockQuote>();
        for (String key : keys) result.put(key, load(key));
        return result;
    }

    public Iterable<String> loadAllKeys() {
        return null;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
