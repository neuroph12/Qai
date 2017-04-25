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
import net.jmob.guice.conf.core.InjectConfig;
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

    @InjectConfig(value = "PERSISTENCE_BASE")
    public String PERSISTENCE_BASE;

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
