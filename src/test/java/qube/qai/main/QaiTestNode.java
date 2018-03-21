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
import javax.inject.Singleton;

/**
 * Created by rainbird on 12/21/15.
 */
public class QaiTestNode extends QaiTestModule {

    private static Logger logger = LoggerFactory.getLogger("QaiTestNode");

    private boolean debug = true;

    private static Injector injector;

    private String NODE_NAME = "QaiTestNode";

    @Inject
    private HazelcastInstance hazelcastInstance;

    public QaiTestNode() {
    }

    protected void startServices() {
        // injector knows all
        // Server configuration lies in QaiServerModule
        // other Qai dependent services and things lie in QaiModule
        //QaiTestModule qaiTestModule = new QaiTestModule();
        QaiTestServerModule qaiTestServer = new QaiTestServerModule();
        QaiTestSecurityModule qaiTestSecurity = new QaiTestSecurityModule();
        injector = Guice.createInjector(qaiTestServer, qaiTestSecurity);

        //GuiceManagedContext managedContext = injector.getInstance(GuiceManagedContext.class);
        //provideHazelcastConfig(managedContext);
        // this looks crazy but just works...
        injector.injectMembers(qaiTestServer);

        injector.injectMembers(this);

        // so that the singleton is initialized
        QaiInjectorService.getInstance().setInjector(injector);

        // the whole configuration takes place in guice
        // and the main instance is then distributed via injection
        String instanceName = hazelcastInstance.getName();
        logger.info("hazelcastInstance with name: '" + instanceName + "' has been started");

    }

    @Override
    @Singleton
    public HazelcastInstance provideHazelcastInstance() {
        return hazelcastInstance;
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
