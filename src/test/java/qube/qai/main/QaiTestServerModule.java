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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.ResourceData;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.WikiArticle;
import qube.qai.persistence.mapstores.DatabaseMapStore;
import qube.qai.persistence.mapstores.IndexedDirectoryMapStore;
import qube.qai.persistence.mapstores.PersistentModelMapStore;
import qube.qai.persistence.mapstores.WikiArticleMapStore;
import qube.qai.procedure.Procedure;
import qube.qai.services.implementation.DirectorySearchService;
import qube.qai.services.implementation.DistributedSearchListener;
import qube.qai.user.User;

import javax.inject.Singleton;
import java.util.Properties;

/**
 * Created by rainbird on 12/21/15.
 */
public class QaiTestServerModule extends AbstractModule {

    private static Logger logger = LoggerFactory.getLogger("QaiTestServerModule");

    private static final String NODE_NAME = "QaiTestNode";

    private static final String USERS = "USERS";

    private static final String USER_SESSIONS = "USER_SESSIONS";

    private static final String USER_ROLES = "USER_ROLES";

    private static final String STOCK_ENTITIES = "STOCK_ENTITIES";

    private static final String STOCK_QUOTES = "STOCK_QUOTES";

    private static final String PROCEDURES = "PROCEDURES";

    private String PERSISTENCE_BASE = "/media/rainbird/ALEPH/qai-persistence.db";
    // private String PERSISTENCE_BASE = "/media/pi/BET/qai-persistence.db";

    private static final String MODEL_BASE_DIRECTORY = "/media/rainbird/ALEPH/qai-persistence.db/test_model_persistence";

    private static final String WIKIPEDIA = "WIKIPEDIA_EN";

    private static final String WIKIPEDIA_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";
    //private static final String WIKIPEDIA_ARCHIVE = "/media/pi/BET/wiki-archives/wikipedia_en.zip";

    private static final String WIKTIONARY = "WIKTIONARY_EN";

    private static final String WIKTIONARY_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //private static final String WIKTIONARY_ARCHIVE = "/media/pi/BET/wiki-archives/wiktionary_en.zip";

    public static final String WIKIPEDIA_RESOURCES = "WIKIPEDIA_RESOURCES";

    public String WIKIPEDIA_RESOURCE_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";

    public String WIKIPEDIA_RESOURCE_INDEX = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources.index";

    public String USER_MODEL_DIRECTORY = "/media/rainbird/ALEPH/qai-persistence.db/test_user_model";

    public String PROCEDURE_MODEL_DIRECTORY = "/media/rainbird/ALEPH/qai-persistence.db/test_procedure_model";

    private HazelcastInstance hazelcastInstance;

    private MapStore stockEntityMapStore;

    private MapStore stockQuoteMapStore;

    private MapStore userMapStore;

    private MapStore sessionMapStore;

    private MapStore roleMapStore;

    private DistributedSearchListener userSearchListener;

    private DistributedSearchListener wikipediaSearchListener;

    private DistributedSearchListener wiktionarySearchListener;

    private DistributedSearchListener wikiResourcesSearchListener;

    private DistributedSearchListener stockEntitiesSearchListener;

    private DistributedSearchListener proceduresSearchListener;


    // with static fields i can use the injector even when it is active for other tests
    private static Injector stocksInjector;

    // with static fields i can use the injector even when it is active for other tests
    private static Injector usersInjector;

    @Override
    protected void configure() {

//        bind(StockEntityMapStore.class).toInstance(stockEntityMapStore);
//
//        bind(StockQuoteMapStore.class).toInstance(stockQuoteMapStore);
//
//        bind(UserMapStore.class).toInstance(userMapStore);
//
//        bind(SessionMapStore.class).toInstance(sessionMapStore);
    }

//    @Provides @Named("STOCKS")
//    public EntityManager provideStocksEntityManager() {
//        return initStocksInjector().getInstance(EntityManager.class);
//    }
//
//    @Provides @Named("USERS")
//    public EntityManager provideUsersEntityManager() {
//        return initUsersInjector().getInstance(EntityManager.class);
//    }

    /**
     * StockQuotesSearchService
     *
     * @return
     */
//    @Provides
//    @Named("Stock_Quotes")
//    public SearchServiceInterface provideStockQuoteSearchService() {
//
//        // create an injector for initializing JPA-Module & start the service
//        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_STOCKS"));
//        PersistService persistService = injector.getInstance(PersistService.class);
//        persistService.start();
//
//        StockQuoteSearchService searchService = new StockQuoteSearchService();
//        injector.injectMembers(searchService);
//
//        return searchService;
//    }

    /**
     * RdfTripleSearchService
     * @return
     */
    // this should by now be entirely obsolete... or is it not?
//    @Provides @Named("Dbpedia_en")
//    SearchServiceInterface provideDbpediaSearchService() {
//
//        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_DBPEDIA"));
//        PersistService service = injector.getInstance(PersistService.class);
//        service.start();
//
//        RDFTriplesSearchService searchService = new RDFTriplesSearchService();
//        injector.injectMembers(searchService);
//        return searchService;
//    }

    /**
     * @return
     * @TODO refactor this mess!!!
     * this is more or less where everything happens
     */
    @Provides
    @Singleton
    public HazelcastInstance provideHazelcastInstance() {

        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }

        Config config = new Config(NODE_NAME);

        initStocksInjector();

        initUsersInjector();

        /**
         * here we add the map-store for Users which is
         * in this case the HsqlDBMapStore
         */
        MapConfig usersConfig = config.getMapConfig(USERS);
        usersConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig userMapstoreConfig = usersConfig.getMapStoreConfig();
        if (userMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USERS);
            userMapstoreConfig = new MapStoreConfig();
            userMapstoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
        }
        userMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, User>() {
            public MapLoader<String, User> newMapStore(String mapName, Properties properties) {
                if (USERS.equals(mapName)) {
                    if (userMapStore == null) {
                        userMapStore = new PersistentModelMapStore(User.class, USER_MODEL_DIRECTORY);
                        ((PersistentModelMapStore) userMapStore).init();
                        usersInjector.injectMembers(userMapStore);
                    }
                    return userMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USERS);
        usersConfig.setMapStoreConfig(userMapstoreConfig);

        /**
         * here we add the map-store for Sessions which is
         * in this case the HsqlDBMapStore
         */
        //MapConfig sessionsConfig = config.getMapConfig(USER_SESSIONS);
//        MapStoreConfig sessionMapStoreConfig = mapConfig.getMapStoreConfig();
//        sessionMapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
//        if (sessionMapStoreConfig == null) {
//            logger.info("mapStoreConfig is null... creating one for: " + USER_SESSIONS);
//            sessionMapStoreConfig = new MapStoreConfig();
//        }
//        sessionMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Session>() {
//            public MapLoader<String, Session> newMapStore(String mapName, Properties properties) {
//                if (USER_SESSIONS.equals(mapName)) {
//                    if (sessionMapStore == null) {
//                        sessionMapStore = new DatabaseMapStore(Session.class);
//                        usersInjector.injectMembers(sessionMapStore);
//                    }
//
//                    return sessionMapStore;
//                } else {
//                    return null;
//                }
//            }
//        });
//        logger.info("adding mapstore configuration for " + USER_SESSIONS);
//        mapConfig.setMapStoreConfig(sessionMapStoreConfig);

        /**
         * here we add the map-store for Roles which is
         * in this case the HsqlDBMapStore
         */
        //MapConfig rolesConfig = config.getMapConfig(USER_ROLES);
//        MapStoreConfig roleMapStoreConfig = mapConfig.getMapStoreConfig();
//        roleMapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
//        if (roleMapStoreConfig == null) {
//            logger.info("mapStoreConfig is null... creating one for: " + USER_ROLES);
//            roleMapStoreConfig = new MapStoreConfig();
//        }
//        roleMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Role>() {
//            public MapLoader<String, Role> newMapStore(String mapName, Properties properties) {
//                if (USER_ROLES.equals(mapName)) {
//                    if (roleMapStore == null) {
//                        roleMapStore = new DatabaseMapStore(Role.class);
//                        usersInjector.injectMembers(roleMapStore);
//                    }
//
//                    return roleMapStore;
//                } else {
//                    return null;
//                }
//            }
//        });
//        logger.info("adding mapstore configuration for " + USER_ROLES);
//        mapConfig.setMapStoreConfig(roleMapStoreConfig);

        /**
         * here we add the map-store for Stock-entities which is
         * in this case the HsqlDBMapStore
         */
        MapConfig stockEntitiesConfig = config.getMapConfig(STOCK_ENTITIES);
        stockEntitiesConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig stockEntitiesMapstoreConfig = stockEntitiesConfig.getMapStoreConfig();
        stockEntitiesMapstoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
        if (stockEntitiesMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + STOCK_ENTITIES);
            stockEntitiesMapstoreConfig = new MapStoreConfig();

        }
        stockEntitiesMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockEntity>() {
            public MapLoader<String, StockEntity> newMapStore(String mapName, Properties properties) {
                if (STOCK_ENTITIES.equals(mapName)) {
                    if (stockEntityMapStore == null) {
                        stockEntityMapStore = new DatabaseMapStore(StockEntity.class);
                        stocksInjector.injectMembers(stockEntityMapStore);
                    }

                    return stockEntityMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + STOCK_ENTITIES);
        stockEntitiesConfig.setMapStoreConfig(stockEntitiesMapstoreConfig);

        /**
         * here we add the map-store for Stock-quotes which is
         * in this case the HsqlDBMapStore
         */
        //MapConfig stockQuotesConfig = config.getMapConfig(STOCK_QUOTES);
//        MapStoreConfig stockQuotesMapstoreConfig = mapConfig.getMapStoreConfig();
//        stockQuotesMapstoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
//        if (stockQuotesMapstoreConfig == null) {
//            logger.info("mapStoreConfig is null... creating one for: " + STOCK_QUOTES);
//            stockQuotesMapstoreConfig = new MapStoreConfig();
//
//        }
//        stockQuotesMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockQuote>() {
//            public MapLoader<String, StockQuote> newMapStore(String mapName, Properties properties) {
//                if (STOCK_QUOTES.equals(mapName)) {
//                    if (stockQuoteMapStore == null) {
//                        stockQuoteMapStore = new DatabaseMapStore(StockQuote.class);
//                        stocksInjector.injectMembers(stockQuoteMapStore);
//                    }
//
//                    return stockQuoteMapStore;
//                } else {
//                    return null;
//                }
//            }
//        });
//        logger.info("adding mapstore configuration for " + STOCK_QUOTES);
//        mapConfig.setMapStoreConfig(stockQuotesMapstoreConfig);

        /**
         * here we add the map-store for Procedures which is
         * in this case DirectoryMapStore
         */
        MapConfig procedureConfig = config.getMapConfig(PROCEDURES);
        procedureConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig procedureMapstoreConfig = procedureConfig.getMapStoreConfig();
        if (procedureMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + PROCEDURES);
            procedureMapstoreConfig = new MapStoreConfig();
        }
        procedureMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, Procedure>() {
            public MapLoader<String, Procedure> newMapStore(String mapName, Properties properties) {
                if (PROCEDURES.equals(mapName)) {
                    return new PersistentModelMapStore(Procedure.class, PROCEDURE_MODEL_DIRECTORY);
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + PROCEDURES);
        procedureConfig.setMapStoreConfig(procedureMapstoreConfig);

        /**
         * wikipedia-article map-store
         */
        MapConfig wikiConfig = config.getMapConfig(WIKIPEDIA);
        wikiConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig wikiMapstoreConfig = wikiConfig.getMapStoreConfig();
        if (wikiMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA);
            wikiMapstoreConfig = new MapStoreConfig();
            wikiMapstoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
        }
        wikiMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, WikiArticle>() {
            public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties) {
                if (WIKIPEDIA.equals(mapName)) {
                    return new WikiArticleMapStore(WIKIPEDIA_ARCHIVE);
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKIPEDIA);
        wikiConfig.setMapStoreConfig(wikiMapstoreConfig);

        /**
         * wiktionary-article map-store
         */
        MapConfig wiktionaryConfig = config.getMapConfig(WIKTIONARY);
        wiktionaryConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig wiktionaryMapstoreConfig = wiktionaryConfig.getMapStoreConfig();
        if (wiktionaryMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKTIONARY);
            wiktionaryMapstoreConfig = new MapStoreConfig();
            wiktionaryMapstoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
        }
        wiktionaryMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, WikiArticle>() {
            public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties) {
                if (WIKTIONARY.equals(mapName)) {
                    return new WikiArticleMapStore(WIKTIONARY_ARCHIVE);
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKTIONARY);
        wiktionaryConfig.setMapStoreConfig(wiktionaryMapstoreConfig);

        /**
         * wikipedia-resources
         */
        MapConfig wikiresourcesConfig = config.getMapConfig(WIKIPEDIA_RESOURCES);
        wikiresourcesConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig wikiResourceMapStoreConfig = wikiresourcesConfig.getMapStoreConfig();
        if (wikiResourceMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA_RESOURCES);
            wikiResourceMapStoreConfig = new MapStoreConfig();
        }
        wikiResourceMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, ResourceData>() {
            public MapLoader<String, ResourceData> newMapStore(String mapName, Properties properties) {
                if (WIKIPEDIA_RESOURCES.equals(mapName)) {
                    IndexedDirectoryMapStore store = new IndexedDirectoryMapStore(WIKIPEDIA_RESOURCE_DIRECTORY, WIKIPEDIA_RESOURCE_INDEX);
                    DirectorySearchService directorySearchService = new DirectorySearchService(WIKIPEDIA_RESOURCE_INDEX);
                    store.setSearchService(directorySearchService);
                    return store;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKIPEDIA_RESOURCES);
        wikiresourcesConfig.setMapStoreConfig(wikiResourceMapStoreConfig);

        // now we are ready to get an instance
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        // now start the search-listeners
        userSearchListener = QaiServerModule.provideUsersSearchListener(hazelcastInstance);

        wikipediaSearchListener = QaiServerModule.provideWikipediaSearchListener(hazelcastInstance);

        wiktionarySearchListener = QaiServerModule.provideWiktionarySearchListener(hazelcastInstance);

        wikiResourcesSearchListener = QaiServerModule.provideWikiResourcesSearchListener(hazelcastInstance);

        stockEntitiesSearchListener = QaiServerModule.provideStockQuotesSearchListener(hazelcastInstance);

        proceduresSearchListener = QaiServerModule.provideProceduresSearchListener(hazelcastInstance);

        return hazelcastInstance;
    }

    public static Injector initUsersInjector() {
        if (usersInjector == null) {
            usersInjector = Guice.createInjector(new JpaPersistModule("TEST_USERS"));
            PersistService service = usersInjector.getInstance(PersistService.class);
            service.start();
        }

        return usersInjector;
    }

    public static Injector initStocksInjector() {
        if (stocksInjector == null) {
            stocksInjector = Guice.createInjector(new JpaPersistModule("TEST_STOCKS"));
            PersistService service = stocksInjector.getInstance(PersistService.class);
            service.start();
        }

        return stocksInjector;
    }
}
