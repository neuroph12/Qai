/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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
import qube.qai.services.QaiInjectorService;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static qube.qai.main.QaiServerModule.CONFIG_FILE_NAME;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiNode {

    private static Logger logger = LoggerFactory.getLogger("QaiNode");

    private Injector injector;

    private String NODE_NAME = "QaiNode";

    // this is where the different configration can be loadaed
    // private String PROPERTIES_FILE = "qube/qai/main/config_monday.properties";
    // private String PROPERTIES_FILE = "qube/qai/main/config_tuesday.properties";
    private String PROPERTIES_FILE = "qube/qai/main/config_wednesday.properties";
    //private String PROPERTIES_FILE = "qube/qai/main/config_dev.properties";

    @Inject
    private HazelcastInstance hazelcastInstance;

    private QaiServices qaiServices;

    public QaiNode() {
    }

    protected void startServices() {
        // injector knows all
        // Server configuration lies in QaiServerModule
        // other Qai dependent services and things lie in QaiModule
        QaiServerModule qaiServer;
        try {
            Properties properties = new Properties();

            ClassLoader loader = QaiServerModule.class.getClassLoader();
            URL url = loader.getResource(PROPERTIES_FILE);
            properties.load(url.openStream());

            qaiServer = new QaiServerModule(properties);
            QaiModule qaiModule = new QaiModule();
            QaiSecurityModule qaiSecurityModule = new QaiSecurityModule();
            injector = Guice.createInjector(qaiServer, qaiModule, qaiSecurityModule);

        } catch (IOException e) {
            logger.error("Error while loading configuration file: " + CONFIG_FILE_NAME, e);
            throw new RuntimeException("Configuration file: '" + CONFIG_FILE_NAME + "' could not be found- have to exit!");
        }

        // so that the singleton is initialized
        QaiInjectorService injectorService = new QaiInjectorService(injector);
        // self inoculation...
        injector.injectMembers(this);

        // create the services by injecting them
        qaiServices = new QaiServices();
        qaiServices.startServices(injector, qaiServer.getProperties(), qaiServer.getLocalServices());
        qaiServices.checkAllServices();

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

}
