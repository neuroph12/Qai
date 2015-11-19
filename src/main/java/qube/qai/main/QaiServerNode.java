package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiServerNode {

    private static Injector injector;

    private static Config config;

    private static HazelcastInstance hazelcastInstance;

    public static void main(String[] params) {

        // instantiate Guice
        injector = Guice.createInjector(new QaiModule());

        // this configuration is quite important
        // @TODO add the map-store configurations pointing to the wiki-archives ot this configuration
        config = new Config("QaiNode");


        // we start the Hazelcast instance
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }
}
