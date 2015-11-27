package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.mapstores.HqslDBMapStore;

import javax.inject.Inject;
import java.util.Properties;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiServerNode {

    private boolean debug = true;

    private Injector injector;

    private String NODE_NAME = "QaiNode";

    @Inject
    private HazelcastInstance hazelcastInstance;

    public QaiServerNode() {
    }

    protected void startServices() {
        // injector knows all
        // Server configuration lies in QaiServerModule
        // other Qai dependent services and things lie in QaiModule
        injector = Guice.createInjector(new QaiServerModule(), new QaiModule());
        injector.injectMembers(this);

        // the whole configuration takes place in guice
        // and the main instance is then distributed via injection
        String instanceName = hazelcastInstance.getName();
        log(instanceName + " has been started");

    }

    public static void main(String[] params) {

        // instantiate the node and start its services
        QaiServerNode qaiServerNode = new QaiServerNode();
        qaiServerNode.startServices();
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
