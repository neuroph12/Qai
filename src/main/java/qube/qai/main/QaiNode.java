package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiNode {

    private static Logger logger = LoggerFactory.getLogger("QaiNode");

    private static Injector injector;

    private String NODE_NAME = "QaiNode";

    @Inject
    private HazelcastInstance hazelcastInstance;

    public QaiNode() {
    }

    protected void startServices() {
        // injector knows all
        // Server configuration lies in QaiServerModule
        // other Qai dependent services and things lie in QaiModule
        QaiServerModule qaiServer = new QaiServerModule();
        QaiModule qaiModule = new QaiModule();
        injector = Guice.createInjector(qaiServer, qaiModule);

        // this is crazy but might just work...
        injector.injectMembers(qaiServer);

        injector.injectMembers(this);

        // the whole configuration takes place in guice
        // and the main instance is then distributed via injection
        String instanceName = hazelcastInstance.getName();
        logger.info("hazelcastInstance with name: '" + instanceName + "' has been started");

    }

    public static void main(String[] params) {

        // instantiate the node and start its services
        QaiNode qaiNode = new QaiNode();
        qaiNode.startServices();
    }

    public static Injector getInjector() {
        return injector;
    }
}
