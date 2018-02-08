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
import com.google.inject.Singleton;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.message.MessageQueue;
import qube.qai.message.MessageQueueInterface;
import qube.qai.persistence.MapDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.WikiArticle;
import qube.qai.security.*;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.SelectorFactoryInterface;
import qube.qai.services.UUIDServiceInterface;
import qube.qai.services.implementation.*;

import javax.inject.Named;
import javax.persistence.EntityManager;

import static qube.qai.main.QaiConstants.WIKIPEDIA;
import static qube.qai.main.QaiConstants.WIKTIONARY;

/**
 * Created by rainbird on 11/19/15.
 */
//@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
public class QaiTestModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("QaiTestModule");

    private static String NODE_NAME = "QaiTestNode";

    protected HazelcastInstance hazelcastInstance;

    protected ClientConfig clientConfig;

    private ProcedureManager procedureManager;

    private static String wikipediaDirectory = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.index";
    //private static String wikipediaDirectory = "/media/pi/BET/wiki-archives/wikipedia_en.index";

    private static String wikipediaZipFileName = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.zip";
    //private static String wikipediaZipFileName = "/media/pi/BET/wiki-archives/wikipedia_en.zip";

    private static String wiktionaryDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    //private static String wiktionaryDirectory = "/media/pi/BET/wiki-archives/wiktionary_en.index";

    private static String wiktionaryZipFileName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //private static String wiktionaryZipFileName = "/media/pi/BET/wiki-archives/wiktionary_en.zip";

    private static String STOCK_QUOTES_DIRECTORY = "test/stockquotes/";

    //@InjectConfig(value = "PERSISTENCE_BASE")
    public String PERSISTENCE_BASE;

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<EntityManager>();

    @Override
    protected void configure() {

        logger.info("Guice initialization called- binding services");

        // load the given configuration for
//        install(ConfigurationModule.create());
//        requestInjection(this);

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);

        // ProcedureSource
        //bind(ProcedureSourceInterface.class).to(TestProcedureSourceService.class);

        // executorService
        bind(ProcedureRunnerInterface.class).to(ProcedureRunner.class);

        // messageQueue
        bind(MessageQueueInterface.class).to(MessageQueue.class);

        bind(QaiSecurity.class).to(QaiTestSecurityManager.class);

        bind(ProcedureManagerInterface.class).to(TestProcedureManager.class);
    }

    @Provides
    Logger provideLogger() {
        return LoggerFactory.getLogger("QaiTest");
    }

//    @Provides
//    public QaiDataProvider provideDataProvider() {
//        return new DataProvider();
//    }

    @Provides
    @Named("Wikipedia_en")
    public QaiDataProvider<WikiArticle> provideWikiDataProvider() {
        QaiDataProvider<WikiArticle> wikiProvider = new MapDataProvider(hazelcastInstance, WIKIPEDIA);
        return wikiProvider;
    }

    @Provides
    @Singleton
    public HazelcastInstance provideHazelcastInstance() {
        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }


        hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        return hazelcastInstance;
    }

    @Provides
    public ClientConfig provideHazelcastConfig() {
        clientConfig = new ClientConfig();
        //clientConfig.setInstanceName(NODE_NAME);
        GuiceManagedContext managedContext = new GuiceManagedContext();
        clientConfig.setManagedContext(managedContext);
        clientConfig.getNetworkConfig().addAddress("127.0.0.1:5701");
        return clientConfig;
    }

    @Provides
    SelectorFactoryInterface provideSelectorFactoryInterface() {
        SelectorFactoryInterface selectorfactory = new DataSelectorFactory();
        return selectorfactory;
    }

    @Provides
    @Named("Wiktionary_en")
    SearchServiceInterface provideWiktionarySearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(WIKTIONARY, wiktionaryDirectory, wiktionaryZipFileName);
        return searchService;
    }

    @Provides
    @Named("Wikipedia_en")
    SearchServiceInterface provideWikipediaSearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(WIKIPEDIA, wikipediaDirectory, wikipediaZipFileName);
        return searchService;
    }

//    @Provides
//    @Singleton
//    ProcedureManagerInterface provideProcedureManager() {
//
//        if (hazelcastInstance == null) {
//            hazelcastInstance = provideHazelcastInstance();
//        }
//
//        procedureManager = ProcedureManager.getInstance(hazelcastInstance);
//        return procedureManager;
//    }

}
