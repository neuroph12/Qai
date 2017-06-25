/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.services.implementation.DistributedSearchListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static qube.qai.main.QaiServerModule.CONFIG_FILE_NAME;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiNode {

    private static Logger logger = LoggerFactory.getLogger("QaiNode");

    private static Injector injector;

    private String NODE_NAME = "QaiNode";

//    @InjectConfig(value = "PERSISTENCE_BASE")
//    public String PERSISTENCE_BASE;

    @Inject
    @Named("Wikipedia_en")
    private DistributedSearchListener wikipediaListener;

    @Inject
    @Named("Wiktionary_en")
    private DistributedSearchListener wikitionaryListener;

    @Inject
    @Named("WikiResources_en")
    private DistributedSearchListener wikiResourcesListener;

    @Inject
    @Named("Users")
    private DistributedSearchListener usersListener;

    @Inject
    @Named("StockEntities")
    private DistributedSearchListener stocksListener;

    @Inject
    @Named("Procedures")
    private DistributedSearchListener proceduresListener;

    @Inject
    private HazelcastInstance hazelcastInstance;

    public QaiNode() {
    }


    protected void startServices() {
        // injector knows all
        // Server configuration lies in QaiServerModule
        // other Qai dependent services and things lie in QaiModule
        try {
            Properties properties = new Properties();

            ClassLoader loader = QaiServerModule.class.getClassLoader();
            URL url = loader.getResource("qube/qai/main/config_dev.properties");
            properties.load(url.openStream());

//            properties.load(new FileInputStream(CONFIG_FILE_NAME));
//            Names.bindProperties(binder(), properties);
//            String s = properties.getProperty("WIKIPEDIA_DIRECTORY");
//            if (StringUtils.isEmpty(s)) {
//                throw new RuntimeException("Configuration 'WIKIPEDIA_DIRECTORY' could not be found- have to exit!");
//            }

            QaiServerModule qaiServer = new QaiServerModule(properties);
            QaiModule qaiModule = new QaiModule();
            injector = Guice.createInjector(qaiServer, qaiModule);

        } catch (IOException e) {
            logger.error("Error while loading configuration file: " + CONFIG_FILE_NAME, e);
            throw new RuntimeException("Configuration file: '" + CONFIG_FILE_NAME + "' could not be found- have to exit!");
        }


        // this is crazy but might just work...
        //injector.injectMembers(qaiServer);

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
