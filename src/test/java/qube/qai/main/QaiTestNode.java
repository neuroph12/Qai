package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rainbird on 12/21/15.
 */
public class QaiTestNode {

    private static Logger logger = LoggerFactory.getLogger("QaiTestNode");

    private boolean debug = true;

    private static Injector injector;

    private String NODE_NAME = "QaiTestNode";

    @Inject @Named("HAZELCAST_SERVER")
    private HazelcastInstance hazelcastInstance;

    public QaiTestNode() {
    }

    protected void startServices() {
        // injector knows all
        // Server configuration lies in QaiServerModule
        // other Qai dependent services and things lie in QaiModule
        QaiTestModule qaiTestModule = new QaiTestModule();
        QaiTestServerModule qaiTestServer = new QaiTestServerModule();

        injector = Guice.createInjector(qaiTestServer, qaiTestModule);

        // this looks crazy but just works...
        injector.injectMembers(qaiTestServer);

        injector.injectMembers(this);

        // the whole configuration takes place in guice
        // and the main instance is then distributed via injection
        String instanceName = hazelcastInstance.getName();
        logger.info("hazelcastInstance with name: '" + instanceName + "' has been started");

    }

    public static void main(String[] params) {

        // instantiate the node and start its services
        QaiTestNode qaiTestNode = new QaiTestNode();
        qaiTestNode.startServices();
    }

    public static Injector getInjector() {
        return injector;
    }

}
