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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.message.MessageQueue;
import qube.qai.message.MessageQueueInterface;
import qube.qai.security.QaiSecurity;
import qube.qai.security.QaiSecurityManager;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.SelectorFactoryInterface;
import qube.qai.services.UUIDServiceInterface;
import qube.qai.services.implementation.HazelcastSelectorFactory;
import qube.qai.services.implementation.ProcedureRunner;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.EntityManager;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("Qai-Module");

    public static final String CONFIG_FILE_NAME = "config_dev.properties";

    public String PERSISTENCE_BASE;

    private static final ThreadLocal<EntityManager> entityManagerCache = new ThreadLocal<EntityManager>();

    @Override
    protected void configure() {

        logger.info("Guice initialization called- binding services");

        // load the given configuration for
        //install(ConfigurationModule.create());
        //requestInjection(this);
//        try {
//            Properties properties = new Properties();
//            properties.load(new FileReader(CONFIG_FILE_NAME));
//            Names.bindProperties(binder(), properties);
//        } catch (IOException e) {
//            logger.error("Error while loading configuration file: " + CONFIG_FILE_NAME, e);
//        }

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);

        // executorService
        bind(ProcedureRunnerInterface.class).to(ProcedureRunner.class);

        bind(MessageQueueInterface.class).to(MessageQueue.class);

        bind(QaiSecurity.class).to(QaiSecurityManager.class);

        // stockEntityDataStore
        //bind(DataStore.class).annotatedWith(Names.named("StockEntities")).to(StockEntityDataStore.class);

    }

    @Provides
    Logger provideLogger() {
        return LoggerFactory.getLogger("Qai");
    }

    /**
     * used mainly in procedures themselves so that procedures can
     * get their data from hazelcast and return their data via hazelcast as well
     *
     * @return
     */
    @Provides
    SelectorFactoryInterface provideSelectorFactoryInterface() {

        SelectorFactoryInterface selectorfactory = new HazelcastSelectorFactory();

        return selectorfactory;
    }

}
