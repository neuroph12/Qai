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

import com.google.inject.*;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.*;
import qube.qai.persistence.mapstores.DatabaseMapStore;
import qube.qai.persistence.mapstores.DirectoryMapStore;
import qube.qai.persistence.mapstores.IndexedDirectoryMapStore;
import qube.qai.persistence.mapstores.WikiArticleMapStore;
import qube.qai.persistence.search.DatabaseSearchService;
import qube.qai.procedure.Procedure;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;
import qube.qai.services.implementation.DistributedSearchListener;
import qube.qai.services.implementation.WikiSearchService;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Properties;

/**
 * Created by rainbird on 11/26/15.
 */
//@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
//@BindConfig(value = "qube/qai/main/config_deploy", syntax = Syntax.PROPERTIES)
public class QaiServerModule extends AbstractModule implements QaiConstants {

    private static Logger logger = LoggerFactory.getLogger("QaiServerModule");

    public static final String CONFIG_FILE_NAME = "/home/rainbird/projects/work/qai/src/main/resources/qube/qai/main/config_dev.properties";

    //@InjectConfig(value = "CREATE_STOCK_ENTITIES")
    public String CREATE_STOCK_ENTITIES = "CREATE_STOCK_ENTITIES";

    //@InjectConfig(value = "CREATE_STOCK_QUOTES")
    public String CREATE_STOCK_QUOTES = "CREATE_STOCK_QUOTES";

    //@InjectConfig(value = "CREATE_PROCEDURES")
    public String CREATE_PROCEDURES = "CREATE_PROCEDURES";

    //@InjectConfig(value = "CREATE_PROCEDURES")
    public String CREATE_STOCK_GROUPS = "CREATE_STOCK_GROUPS";

    //@InjectConfig(value = "CREATE_WIKIPEDIA")
    public String CREATE_WIKIPEDIA = "CREATE_WIKIPEDIA";

    //@InjectConfig(value = "CREATE_WIKIPEDIA_RESOURCES")
    public String CREATE_WIKIPEDIA_RESOURCES = "CREATE_WIKIPEDIA_RESOURCES";

    //@InjectConfig(value = "CREATE_WIKTIONARY")
    public String CREATE_WIKTIONARY = "CREATE_WIKTIONARY";

    //@InjectConfig(value = "CREATE_WIKTIONARY_RESOURCES")
    public String CREATE_WIKTIONARY_RESOURCES = "CREATE_WIKTIONARY_RESOURCES";

    //@InjectConfig(value = "CREATE_DBPEDIA")
    public String CREATE_DBPEDIA = "CREATE_DBPEDIA";

    //@InjectConfig(value = "CREATE_USERS")
    public String CREATE_USERS = "CREATE_USERS";

    //@InjectConfig(value = "CREATE_USER_SESSIONS")
    public String CREATE_USER_SESSIONS = "CREATE_USER_SESSIONS";

    //@InjectConfig(value = "CREATE_USER_ROLES")
    public String CREATE_USER_ROLES = "CREATE_USER_ROLES";

//    @InjectConfig(value = "CREATE_DBPERSON")
//    public String CREATE_DBPERSON;

    public static final String DBPEDIA = "DBPEDIA";

//    public static final String DBPERSON = "DBPERSON";

    private HazelcastInstance hazelcastInstance;

    private DatabaseMapStore stockEntityMapStore;

    private DatabaseMapStore stockGroupsMapStore;

    private MapStore<String, StockQuote> stockQuoteMapStore;

    private DatabaseMapStore userMapStore;

    private DatabaseMapStore sessionMapStore;

    private DatabaseMapStore roleMapStore;

    private MapStore<String, Procedure> procedureMapStore;

    private MapStore<String, WikiArticle> wikipediaMapStore;

    private IndexedDirectoryMapStore wikiResourcesMapStore;

    private MapStore<String, WikiArticle> wiktionaryMapStore;

    private IndexedDirectoryMapStore wiktionaryResourcesStore;

    private static Injector jpaStocksInjector;

    private static Injector jpaDBPediaInjector;

    private static Injector jpaUsersInjector;

    private SearchServiceInterface stocksSearchServiceInterface;

    private SearchServiceInterface userSearchService;


    @Inject
    @Named("Users")
    private DistributedSearchListener userSearchListener;

    @Inject
    @Named("Wikipedia_en")
    private DistributedSearchListener wikipediaSearchListener;

    @Inject
    @Named("Wiktionary_en")
    private DistributedSearchListener wiktionarySearchListener;

    @Inject
    @Named("WikiResources_en")
    private DistributedSearchListener wikiResourcesSearchListener;

    @Inject
    @Named("StockEntities")
    private DistributedSearchListener stockEntitiesSearchListener;

    @Inject
    @Named("Procedures")
    private DistributedSearchListener proceduresSearchListener;

    private Properties properties;

    public QaiServerModule(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected void configure() {

        // load the given configuration for loading config-file
//        install(ConfigurationModule.create());
//        requestInjection(this);
//        try {
//            properties = new Properties();
//
//            ClassLoader loader = QaiServerModule.class.getClassLoader();
//            URL url = loader.getResource("qube/qai/main/config_dev.properties");
//            properties.load(url.openStream());
//
////            properties.load(new FileInputStream(CONFIG_FILE_NAME));
////            Names.bindProperties(binder(), properties);
////            String s = properties.getProperty("WIKIPEDIA_DIRECTORY");
////            if (StringUtils.isEmpty(s)) {
////                throw new RuntimeException("Configuration 'WIKIPEDIA_DIRECTORY' could not be found- have to exit!");
////            }
//
//
//        } catch (IOException e) {
//            logger.error("Error while loading configuration file: " + CONFIG_FILE_NAME, e);
//            throw new RuntimeException("Configuration file: '" + CONFIG_FILE_NAME + "' could not be found- have to exit!");
//        }
    }

    /**
     * WiktionarySearchService
     * returns the distributed search service for wiktionary
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("Wiktionary_en")
    @Singleton
    public DistributedSearchListener provideWiktionarySearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface basicSearchService = new WikiSearchService(
                WIKTIONARY,
                properties.getProperty(WIKTIONARY_DIRECTORY),
                properties.getProperty(WIKTIONARY_ARCHIVE));

        DistributedSearchListener searchListener = new DistributedSearchListener(WIKTIONARY);
        searchListener.setSearchService(basicSearchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * WikipediaSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("Wikipedia_en")
    @Singleton
    public DistributedSearchListener provideWikipediaSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = new WikiSearchService(
                WIKIPEDIA,
                properties.getProperty(WIKIPEDIA_DIRECTORY),
                properties.getProperty(WIKIPEDIA_ARCHIVE));

        DistributedSearchListener searchListener = new DistributedSearchListener(WIKIPEDIA);
        searchListener.setSearchService(searchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * WikiResourcesSearchService
     * returns the distributed search service for WikiResources
     * and starts the listener service which will broker the requests
     * @return
     */
    @Provides
    @Named("WikiResources_en")
    @Singleton
    public DistributedSearchListener provideWikiResourcesSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = new DirectorySearchService(
                properties.getProperty(WIKTIONARY_RESOURCE_INDEX));

        DistributedSearchListener searchListener = new DistributedSearchListener(WIKIPEDIA_RESOURCES);
        searchListener.setSearchService(searchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * StockQuotesSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("StockQuotes")
    @Singleton
    public DistributedSearchListener provideStockQuotesSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = createStocksSearchServiceInterface();

        DistributedSearchListener searchListener = new DistributedSearchListener(STOCK_QUOTES);
        searchListener.setSearchService(searchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    private SearchServiceInterface createUserDatabaseSearchService() {
        if (userSearchService == null) {
            userSearchService = new DatabaseSearchService();
            getJpaUsersInjector().injectMembers(userSearchService);
        }
        return userSearchService;
    }

    /**
     * StockQuotesSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("StockEntities")
    @Singleton
    public DistributedSearchListener provideStockEntitiesSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = createStocksSearchServiceInterface();

        DistributedSearchListener searchListener = new DistributedSearchListener(STOCK_ENTITIES);
        searchListener.setSearchService(searchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }


    private SearchServiceInterface createStocksSearchServiceInterface() {
        if (stocksSearchServiceInterface == null) {
            stocksSearchServiceInterface = new DatabaseSearchService();
            getJpaStocksInjector().injectMembers(stocksSearchServiceInterface);
        }
        return stocksSearchServiceInterface;
    }

    /**
     * StockQuotesSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("StockGroups")
    @Singleton
    public DistributedSearchListener provideStockGroupsSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = createStocksSearchServiceInterface();

        DistributedSearchListener searchListener = new DistributedSearchListener(STOCK_GROUPS);
        searchListener.setSearchService(searchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * UsersSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("Users")
    @Singleton
    public DistributedSearchListener provideUsersSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = createUserDatabaseSearchService();

        DistributedSearchListener searchListener = new DistributedSearchListener(USERS);
        searchListener.setSearchService(searchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * ProceduresSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("Procedures")
    @Singleton
    public DistributedSearchListener provideProceduresSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = new ModelStore(properties.getProperty(PROCEDURE_BASE_DIRECTORY));

        DistributedSearchListener searchListener = new DistributedSearchListener(PROCEDURES);
        searchListener.setSearchService(searchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * WiktionarySearchService
     *
     * @return
     */
//    @Provides
//    @Named("Wiktionary_en")
//    SearchServiceInterface provideWiktionarySearchService() {
//
//        SearchServiceInterface searchService = new WikiSearchService(WIKTIONARY_DIRECTORY, WIKTIONARY_ARCHIVE);
//        return searchService;
//    }
//
//    /**
//     * WikipediaSearchService
//     *
//     * @return
//     */
//    @Provides
//    @Named("Wikipedia_en")
//    SearchServiceInterface provideWikipediaSearchService() {
//
//        SearchServiceInterface searchService = new WikiSearchService(WIKIPEDIA_DIRECTORY, WIKIPEDIA_ARCHIVE);
//        return searchService;
//    }


    /**
     * StockQuotesSearchService
     *
     * @return
     */
//    @Provides
//    @Named("Stocks")
//    SearchServiceInterface provideStockQuoteSearchService() {
//
//        DatabaseSearchService searchService = new DatabaseSearchService();
//        getJpaStocksInjector().injectMembers(searchService);
//
//        return searchService;
//    }
//
//    @Provides
//    @Named("Users")
//    SearchServiceInterface provideUsersSearchService() {
//
//        DatabaseSearchService searchService = new DatabaseSearchService();
//        getJpaUsersInjector().injectMembers(searchService);
//
//        return searchService;
//    }

    /**
     * RdfTripleSearchService
     *
     * @return
     */
//    @Provides
//    @Named("Dbpedia_en")
//    SearchServiceInterface provideDbpediaSearchService() {
//
//        Injector injector = Guice.createInjector(new JpaPersistModule("DBPEDIA"));
//        PersistService service = injector.getInstance(PersistService.class);
//        service.start();
//
//        RDFTriplesSearchService searchService = new RDFTriplesSearchService();
//        injector.injectMembers(searchService);
//        return searchService;
//    }
    public static Injector getJpaStocksInjector() {

        if (jpaStocksInjector == null) {
            jpaStocksInjector = Guice.createInjector(new JpaPersistModule("STOCKS"));
            PersistService service = jpaStocksInjector.getInstance(PersistService.class);
            service.start();
        }
        return jpaStocksInjector;
    }

    public static Injector getJpaUsersInjector() {

        if (jpaUsersInjector == null) {
            jpaUsersInjector = Guice.createInjector(new JpaPersistModule("USERS"));
            PersistService service = jpaUsersInjector.getInstance(PersistService.class);
            service.start();
        }
        return jpaUsersInjector;
    }

    @Provides
    @Singleton
    HazelcastInstance provideHazelcastInstance() {

        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }

        Config hazelcastConfig = new Config(NODE_NAME);

        // create Stock_Entities map-store
        if ("true".equals(properties.getProperty(CREATE_STOCK_ENTITIES))) {
            createStockEntitiesConfig(hazelcastConfig);
        }

        // create StockQuotes map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_STOCK_QUOTES))) {
            createStockQuotesConfig(hazelcastConfig);
        }

        // create Procedures map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_PROCEDURES))) {
            createProceduresConfig(hazelcastConfig);
        }

        if ("true".equals(properties.getProperty(CREATE_STOCK_GROUPS))) {
            createStockGroupsConfig(hazelcastConfig);
        }

        // create Wikipedia map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKIPEDIA))) {
            createWikipediaConfig(hazelcastConfig);
        }

        // create Wikipedia-Resources map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKIPEDIA_RESOURCES))) {
            createWikipediaResourcesConfig(hazelcastConfig);
        }

        // create Wiktionary map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKTIONARY))) {
            createWiktionaryConfig(hazelcastConfig);
        }

        // create Wiktionary-Resources map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKTIONARY_RESOURCES))) {
            createWiktionaryResourceConfig(hazelcastConfig);
        }

        // create User database and Hazelcast map
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_USERS))) {
            createUsersMapConfig(hazelcastConfig);
        }

        // create UserRoles and Hazelcast map
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_USER_ROLES))) {
            createUserRolesConfig(hazelcastConfig);
        }

        // create UserSessions
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_USER_SESSIONS))) {
            createUserSessionsConfig(hazelcastConfig);
        }

        // now we are ready to get an instance
        hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        return hazelcastInstance;
    }

    /**
     * DBPedia map-store
     */
    /*private void createDBPediaConfig(Config hazelcastConfig) {
        // first create injector for jpa-module
        if (jpaDBPediaInjector == null) {
            jpaDBPediaInjector = Guice.createInjector(new JpaPersistModule("DBPEDIA"));
            PersistService service = jpaDBPediaInjector.getInstance(PersistService.class);
            service.start();
        }

//        if (jpaUsersInjector == null) {
//            jpaUsersInjector = Guice.createInjector(new JpaPersistModule("USERS"));
//            PersistService service = jpaUsersInjector.getInstance(PersistService.class);
//            service.start();
//        }
//        MapConfig mapConfig = hazelcastConfig.getMapConfig(DBPEDIA);
//        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
//        if (mapStoreConfig == null) {
//            logger.info("mapStoreConfig is null... creating one for: " + DBPEDIA);
//            mapStoreConfig = new MapStoreConfig();
//        }
//        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, RDFTriple>() {
//            public MapLoader<String, RDFTriple> newMapStore(String mapName, Properties properties) {
//                if (DBPEDIA.equals(mapName)) {
//                    dbpediaMapStore = new RdfTripleFileMapStore();
//                    jpaDBPediaInjector.injectMembers(dbpediaMapStore);
//                    return dbpediaMapStore;
//                } else {
//                    return null;
//                }
//            }
//        });
//        logger.info("adding mapstore configuration for " + DBPEDIA);
//        mapConfig.setMapStoreConfig(mapStoreConfig);
    }*/

    /**
     * DbUser map-store
     *
     * @param hazelcastConfig
     */
    private void createUsersMapConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(USERS);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig userMapstoreConfig = mapConfig.getMapStoreConfig();
        if (userMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USERS);
            userMapstoreConfig = new MapStoreConfig();
        }

        userMapstoreConfig.setWriteDelaySeconds(0);

        userMapStore = new DatabaseMapStore(User.class);
        getJpaUsersInjector().injectMembers(userMapStore);

        userMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, User>() {
            public MapLoader<String, User> newMapStore(String mapName, Properties properties) {
                if (USERS.equals(mapName)) {
                    return userMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USERS);
        mapConfig.setMapStoreConfig(userMapstoreConfig);
    }

    /**
     * @param hazelcastConfig
     */
    private void createUserRolesConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(USER_ROLES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig roleMapStoreConfig = mapConfig.getMapStoreConfig();
        if (roleMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USER_ROLES);
            roleMapStoreConfig = new MapStoreConfig();
        }

        roleMapStore = new DatabaseMapStore(Role.class);
        getJpaUsersInjector().injectMembers(roleMapStore);

        roleMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Role>() {
            public MapLoader<String, Role> newMapStore(String mapName, Properties properties) {
                if (USER_ROLES.equals(mapName)) {
                    return roleMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USER_ROLES);
        mapConfig.setMapStoreConfig(roleMapStoreConfig);
    }

    /**
     *
     * @param hazelcastConfig
     */
    private void createUserSessionsConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(USER_SESSIONS);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig sessionMapStoreConfig = mapConfig.getMapStoreConfig();
        if (sessionMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USER_SESSIONS);
            sessionMapStoreConfig = new MapStoreConfig();
        }

        sessionMapStore = new DatabaseMapStore(Session.class);
        getJpaUsersInjector().injectMembers(sessionMapStore);

        sessionMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Session>() {
            public MapLoader<String, Session> newMapStore(String mapName, Properties properties) {
                if (USER_SESSIONS.equals(mapName)) {
                    return sessionMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USER_SESSIONS);
        mapConfig.setMapStoreConfig(sessionMapStoreConfig);
    }

    /**
     * DBPerson map-store
     * this is probably obsolete as the whole information is also to be found in dbpedia as well
     */
//    private void createDBPersonConfig(Config hazelcastConfig) {
//        MapConfig mapConfig = hazelcastConfig.getMapConfig(DBPERSON);
//        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
//        if (mapStoreConfig == null) {
//            logger.info("mapStoreConfig is null... creating one for: " + DBPERSON);
//            mapStoreConfig = new MapStoreConfig();
//        }
//        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<RDFId, RDFTriple>() {
//            public MapLoader<RDFId, RDFTriple> newMapStore(String mapName, Properties properties) {
//                if (DBPERSON.equals(mapName)) {
//                    dbpersonMapStore = new RdfTripleFileMapStore();
//                    jpaDBPersonInjector.injectMembers(dbpersonMapStore);
//                    return dbpediaMapStore;
//                } else {
//                    return null;
//                }
//            }
//        });
//        logger.info("adding mapstore configuration for " + DBPERSON);
//        mapConfig.setMapStoreConfig(mapStoreConfig);
//    }

    /**
     * wiktionary resources
     */

    private void createWiktionaryResourceConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(WIKTIONARY_RESOURCES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKTIONARY_RESOURCES);
            mapStoreConfig = new MapStoreConfig();
        }

        wiktionaryResourcesStore = new IndexedDirectoryMapStore(
                properties.getProperty(WIKTIONARY_RESOURCE_DIRECTORY),
                properties.getProperty(WIKTIONARY_RESOURCE_INDEX));
        DirectorySearchService directorySearchService = new DirectorySearchService(properties.getProperty(WIKTIONARY_RESOURCE_INDEX));
        wiktionaryResourcesStore.setSearchService(directorySearchService);

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, ResourceData>() {
            public MapLoader<String, ResourceData> newMapStore(String mapName, Properties properties) {
                if (WIKTIONARY_RESOURCES.equals(mapName)) {
                    return wiktionaryResourcesStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKTIONARY_RESOURCES);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }

    /**
     * wiktionary-article map-store
     */


    private void createWiktionaryConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(WIKTIONARY);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKTIONARY);
            mapStoreConfig = new MapStoreConfig();
        }

        wiktionaryMapStore = new WikiArticleMapStore(properties.getProperty(WIKTIONARY_ARCHIVE));

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, WikiArticle>() {
            public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties) {
                if (WIKTIONARY.equals(mapName)) {
                    return wiktionaryMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKTIONARY);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }

    /**
     * wikipedia resources
     */
    private void createWikipediaResourcesConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(WIKIPEDIA_RESOURCES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA_RESOURCES);
            mapStoreConfig = new MapStoreConfig();
        }

        wikiResourcesMapStore = new IndexedDirectoryMapStore(
                properties.getProperty(WIKIPEDIA_RESOURCE_DIRECTORY),
                properties.getProperty(WIKIPEDIA_RESOURCE_INDEX));
        DirectorySearchService directorySearchService = new DirectorySearchService(properties.getProperty(WIKIPEDIA_RESOURCE_INDEX));
        wikiResourcesMapStore.setSearchService(directorySearchService);

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, ResourceData>() {
            public MapLoader<String, ResourceData> newMapStore(String mapName, Properties properties) {
                if (WIKIPEDIA_RESOURCES.equals(mapName)) {
                    return wikiResourcesMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKIPEDIA_RESOURCES);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }

    /**
     * wikipedia-article map-store
     */
    private void createWikipediaConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(WIKIPEDIA);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA);
            mapStoreConfig = new MapStoreConfig();
        }

        wikipediaMapStore = new WikiArticleMapStore(properties.getProperty(WIKIPEDIA_ARCHIVE));

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, WikiArticle>() {
            public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties) {
                if (WIKIPEDIA.equals(mapName)) {
                    return wikipediaMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKIPEDIA);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }

    /**
     * here we add the map-store for Procedures which is
     * in this case DirectoryMapStore
     */
    private void createProceduresConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(PROCEDURES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + PROCEDURES);
            mapStoreConfig = new MapStoreConfig();
        }

        procedureMapStore = new DirectoryMapStore(properties.getProperty(PROCEDURE_BASE_DIRECTORY));

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Procedure>() {
            public MapLoader<String, Procedure> newMapStore(String mapName, Properties properties) {
                if (PROCEDURES.equals(mapName)) {
                    return procedureMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + PROCEDURES);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }

    /**
     * here we add the map-store for Stock-quotes which is
     * in this case the HsqlDBMapStore
     */
    private void createStockQuotesConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(STOCK_QUOTES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + STOCK_QUOTES);
            mapStoreConfig = new MapStoreConfig();

        }

        stockQuoteMapStore = new DatabaseMapStore(StockQuote.class);
        getJpaStocksInjector().injectMembers(stockQuoteMapStore);

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockQuote>() {
            public MapLoader<String, StockQuote> newMapStore(String mapName, Properties properties) {
                if (STOCK_QUOTES.equals(mapName)) {
                    return stockQuoteMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + STOCK_QUOTES);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }

    /**
     * here we add the map-store for Stock-quotes which is
     * in this case the HsqlDBMapStore
     */
    private void createStockGroupsConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(STOCK_GROUPS);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + STOCK_GROUPS);
            mapStoreConfig = new MapStoreConfig();

        }

        stockGroupsMapStore = new DatabaseMapStore(StockGroup.class);
        getJpaStocksInjector().injectMembers(stockGroupsMapStore);

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockGroup>() {
            public MapLoader<String, StockGroup> newMapStore(String mapName, Properties properties) {
                if (STOCK_GROUPS.equals(mapName)) {
                    return stockGroupsMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + STOCK_GROUPS);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }

    /**
     * here we add the map-store for Stock-entities which is
     * in this case the HsqlDBMapStore
     */
    private void createStockEntitiesConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(STOCK_ENTITIES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + STOCK_ENTITIES);
            mapStoreConfig = new MapStoreConfig();
        }

        stockEntityMapStore = new DatabaseMapStore(StockEntity.class);
        getJpaStocksInjector().injectMembers(stockEntityMapStore);

        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockEntity>() {
            public MapLoader<String, StockEntity> newMapStore(String mapName, Properties properties) {
                if (STOCK_ENTITIES.equals(mapName)) {
                    return stockEntityMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + STOCK_ENTITIES);
        mapConfig.setMapStoreConfig(mapStoreConfig);
    }
}
