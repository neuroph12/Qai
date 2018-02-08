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
//@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
//@BindConfig(value = "qube/qai/main/config_deploy", syntax = Syntax.PROPERTIES)
public class QaiModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("Qai-Module");

    public static final String CONFIG_FILE_NAME = "config_dev.properties";

//    private static String wiktionaryDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
//
//    private static String wiktionaryZipFileName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
//
//    private static String wikipediaDirectory = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.index";
//
//    private static String wikipediaResources = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.resources";
//
//    private static String wikipediaZipFileName = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.zip";

    //@InjectConfig(value = "PERSISTENCE_BASE")
    public String PERSISTENCE_BASE;

    //private static String stockQuotesDirectory = "data/stockquotes/";

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
     * @return
     * @TODO this was mainly for test reasons, i think it is now time to remove it
     */
    /*@Provides
    ProcedureSourceInterface provideProcedureSourceInterface() {
        return CachedProcedureSourceService.getInstance();
    }*/

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

    /**
     * EntityManagerFactory is used in HsqlDBMapStores
     * and only there... StockEntities, RDFTriples and StockQuotes
     * @return
     */
//    @Provides @Singleton @Named("STOCKS")
//    public EntityManagerFactory provideStocksDBEntityManagerFactory() {
//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
//        properties.put("hibernate.connection.url", "jdbc:hsqldb:" + PERSISTENCE_BASE);
//        properties.put("hibernate.connection.username", "sa");
//        properties.put("hibernate.connection.password", "");
//        properties.put("hibernate.connection.pool_size", "1");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//
//        properties.put("current_session_context_class", "org.hibernate.context.ManagedSessionContext");
//        properties.put("hibernate.cache.use_second_level_cache", "false");
//        properties.put("hibernate.cache.use_query_cache", "false");
//        properties.put("cache.provider_class", "org.hibernate.cache.NoCacheProvider");
//        properties.put("show_sql", "true");
//
//        return Persistence.createEntityManagerFactory("STOCKS", properties);
//    }

//    @Provides @Singleton @Named("PERSONDATA_EN")
//    public EntityManagerFactory providePersondataEnEntityManagerFactory() {
//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
//        properties.put("hibernate.connection.url", "jdbc:hsqldb:" + PERSISTENCE_BASE + "persondata_en/");
//        properties.put("hibernate.connection.username", "sa");
//        properties.put("hibernate.connection.password", "");
//        properties.put("hibernate.connection.pool_size", "1");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//
//        properties.put("current_session_context_class", "org.hibernate.context.ManagedSessionContext");
//        properties.put("hibernate.cache.use_second_level_cache", "false");
//        properties.put("hibernate.cache.use_query_cache", "false");
//        properties.put("cache.provider_class", "org.hibernate.cache.NoCacheProvider");
//        properties.put("show_sql", "true");
//
//        return Persistence.createEntityManagerFactory("persondata_en", properties);
//    }

//    @Provides @Singleton @Named("DBPEDIA_EN")
//    public EntityManagerFactory provideDbPediaEnEntityManagerFactory() {
//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
//        properties.put("hibernate.connection.url", "jdbc:hsqldb:" + PERSISTENCE_BASE + "/dbpedia_en/");
//        properties.put("hibernate.connection.username", "sa");
//        properties.put("hibernate.connection.password", "");
//        properties.put("hibernate.connection.pool_size", "1");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//
//        properties.put("current_session_context_class", "org.hibernate.context.ManagedSessionContext");
//        properties.put("hibernate.cache.use_second_level_cache", "false");
//        properties.put("hibernate.cache.use_query_cache", "false");
//        properties.put("cache.provider_class", "org.hibernate.cache.NoCacheProvider");
//        properties.put("show_sql", "true");
//
//        return Persistence.createEntityManagerFactory("dbpedia_en", properties);
//    }

//    @Provides
//    public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
//        EntityManager entityManager = entityManagerCache.get();
//        if (entityManager == null) {
//            entityManagerCache.set(entityManager = entityManagerFactory.createEntityManager());
//        }
//        return entityManager;
//    }
}
