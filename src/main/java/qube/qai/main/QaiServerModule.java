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
import qube.qai.persistence.mapstores.*;
import qube.qai.persistence.search.DatabaseSearchService;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureLibraryInterface;
import qube.qai.security.ProcedureManager;
import qube.qai.security.ProcedureManagerInterface;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;
import qube.qai.services.implementation.GuiceManagedContext;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by rainbird on 11/26/15.
 */
//@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
//@BindConfig(value = "qube/qai/main/config_deploy", syntax = Syntax.PROPERTIES)
public class QaiServerModule extends AbstractModule implements QaiConstants {

    private static Logger logger = LoggerFactory.getLogger("QaiServerModule");

    //@InjectConfig(value = "NODE_NAME")
    public String NODE_NAME = "QaiNode";

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

    //@InjectConfig(value = "CREATE_USERS")
    public String CREATE_USERS = "CREATE_USERS";

    //@InjectConfig(value = "CREATE_USER_SESSIONS")
    public String CREATE_USER_SESSIONS = "CREATE_USER_SESSIONS";

    //@InjectConfig(value = "CREATE_USER_ROLES")
    public String CREATE_USER_ROLES = "CREATE_USER_ROLES";

    public String CREATE_UPLOAD_DIRECTORY = "CREATE_UPLOAD_DIRECTORY";

    private HazelcastInstance hazelcastInstance;

    private DatabaseMapStore stockEntityMapStore;

    private DatabaseMapStore stockGroupsMapStore;

    private MapStore<String, StockQuote> stockQuoteMapStore;

    private DatabaseMapStore userMapStore;

    private DatabaseMapStore sessionMapStore;

    private DatabaseMapStore roleMapStore;

    private DirectoryMapStore uploadDirectoryMapStore;

    private MapStore<String, Procedure> procedureMapStore;

    private MapStore<String, WikiArticle> wikipediaMapStore;

    private IndexedDirectoryMapStore wikiResourcesMapStore;

    private MapStore<String, WikiArticle> wiktionaryMapStore;

    private IndexedDirectoryMapStore wiktionaryResourcesStore;

    private static Injector jpaStocksInjector;

    private static Injector jpaDBPediaInjector;

    private static Injector jpaUsersInjector;

    static JpaPersistModule userJpaModule;

    private SearchServiceInterface stocksSearchService;

    private SearchServiceInterface userSearchService;

    private SearchServiceInterface wikiResourcesSearchService;

    private SearchServiceInterface wikipediaSearchService;

    private SearchServiceInterface wiktionarySearchService;

    private ProcedureManagerInterface procedureManager;

    private Properties properties;

    private Set<String> localServices = new HashSet<>();

    public QaiServerModule(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected void configure() {

        // for the time being nothing to do here.
        bind(ProcedureLibraryInterface.class).to(ProcedureLibrary.class);
    }

    @Provides
    protected ProcedureManagerInterface provideProcedureManager() {

        if (hazelcastInstance == null) {
            hazelcastInstance = provideHazelcastInstance();
        }

        if (procedureManager == null) {
            procedureManager = new ProcedureManager(hazelcastInstance);
            //((ProcedureManager) procedureManager).initialize();
        }

        return procedureManager;
    }

    /**
     * WiktionarySearchService
     * returns the distributed search service for wiktionary
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    /*@Provides
    @Named("Wiktionary_en")
    @Singleton
    public DistributedSearchListener provideWiktionarySearchListener(HazelcastInstance hazelcastInstance) {

        if (wiktionarySearchListener != null) {
            return wiktionarySearchListener;
        }

        wiktionarySearchService = new WikiSearchService(
                WIKTIONARY,
                properties.getProperty(WIKTIONARY_DIRECTORY),
                properties.getProperty(WIKTIONARY_ARCHIVE));

        ((WikiSearchService) wiktionarySearchService).initialize();

        wiktionarySearchListener = new DistributedSearchListener(WIKTIONARY);
        wiktionarySearchListener.setSearchService(wiktionarySearchService);
        wiktionarySearchListener.setHazelcastInstance(hazelcastInstance);
        wiktionarySearchListener.initialize();

        return wiktionarySearchListener;
    }*/

    /**
     * WikipediaSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    /*@Provides
    @Named("Wikipedia_en")
    @Singleton
    public DistributedSearchListener provideWikipediaSearchListener(HazelcastInstance hazelcastInstance) {

        if (wikipediaSearchListener != null) {
            return wikipediaSearchListener;
        }

        wikipediaSearchService = new WikiSearchService(
                WIKIPEDIA,
                properties.getProperty(WIKIPEDIA_DIRECTORY),
                properties.getProperty(WIKIPEDIA_ARCHIVE));

        ((WikiSearchService) wikipediaSearchService).initialize();

        wikipediaSearchListener = new DistributedSearchListener(WIKIPEDIA);
        wikipediaSearchListener.setSearchService(wikipediaSearchService);
        wikipediaSearchListener.setHazelcastInstance(hazelcastInstance);
        wikipediaSearchListener.initialize();

        return wikipediaSearchListener;
    }*/

    /**
     * WikiResourcesSearchService
     * returns the distributed search service for WikiResources
     * and starts the listener service which will broker the requests
     * @return
     */
    /*@Provides
    @Named("WikiResources_en")
    @Singleton
    public DistributedSearchListener provideWikiResourcesSearchListener(HazelcastInstance hazelcastInstance) {

        if (wikiResourcesSearchListener != null) {
            return wikipediaSearchListener;
        }

        wikiResourcesSearchService = new DirectorySearchService(WIKIPEDIA_RESOURCES,
                properties.getProperty(WIKTIONARY_RESOURCE_INDEX));

        wikiResourcesSearchListener = new DistributedSearchListener(WIKIPEDIA_RESOURCES);
        wikiResourcesSearchListener.setSearchService(wikiResourcesSearchService);
        wikiResourcesSearchListener.setHazelcastInstance(hazelcastInstance);
        wikiResourcesSearchListener.initialize();

        return wikiResourcesSearchListener;
    }*/

    /**
     * StockQuotesSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    /*@Provides
    @Named("StockQuotes")
    @Singleton
    public DistributedSearchListener provideStockQuotesSearchListener(HazelcastInstance hazelcastInstance) {

        if (stockQuotesSearchListener != null) {
            return stockQuotesSearchListener;
        }

        SearchServiceInterface searchService = new DatabaseSearchService("StockQuotes");
        getJpaStocksInjector().injectMembers(searchService);

        stockQuotesSearchListener = new DistributedSearchListener(STOCK_QUOTES);
        stockQuotesSearchListener.setSearchService(searchService);
        stockQuotesSearchListener.setHazelcastInstance(hazelcastInstance);
        stockQuotesSearchListener.initialize();

        return stockQuotesSearchListener;
    }*/

    @Provides
    @Named("Users")
    @Singleton
    private SearchServiceInterface createUserDatabaseSearchService() {
        if (userSearchService == null) {
            userSearchService = new DatabaseSearchService(USERS);
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
    /*@Provides
    @Named("StockEntities")
    @Singleton
    public DistributedSearchListener provideStockEntitiesSearchListener(HazelcastInstance hazelcastInstance) {

        if (stockEntitiesSearchListener != null) {
            return stockEntitiesSearchListener;
        }

        SearchServiceInterface stocksSearchService = new DatabaseSearchService("StockEntities");
        getJpaStocksInjector().injectMembers(stocksSearchService);

        stockEntitiesSearchListener = new DistributedSearchListener(STOCK_ENTITIES);
        stockEntitiesSearchListener.setSearchService(stocksSearchService);
        stockEntitiesSearchListener.setHazelcastInstance(hazelcastInstance);
        stockEntitiesSearchListener.initialize();

        return stockEntitiesSearchListener;
    }*/

    @Provides
    @Named("StockGroups")
    @Singleton
    private SearchServiceInterface createStockGroupsSearchServiceInterface() {
        if (stocksSearchService == null) {
            stocksSearchService = new DatabaseSearchService("StcokGroups");
            getJpaStocksInjector().injectMembers(stocksSearchService);
        }
        return stocksSearchService;
    }

    /**
     * StockQuotesSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
//    @Provides
//    @Named("StockGroups")
//    @Singleton
//    public DistributedSearchListener provideStockGroupsSearchListener(HazelcastInstance hazelcastInstance) {
//
//        if (stockGroupsSearchListener != null) {
//            return stockGroupsSearchListener;
//        }
//
//        SearchServiceInterface searchService = createStockGroupsSearchServiceInterface();
//
//        stockGroupsSearchListener = new DistributedSearchListener(STOCK_GROUPS);
//        stockGroupsSearchListener.setSearchService(searchService);
//        stockGroupsSearchListener.setHazelcastInstance(hazelcastInstance);
//        stockGroupsSearchListener.initialize();
//
//        return stockGroupsSearchListener;
//    }

    /**
     * UsersSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
//    @Provides
//    @Named("Users")
//    @Singleton
//    public DistributedSearchListener provideUsersSearchListener(HazelcastInstance hazelcastInstance) {
//
//        if (userSearchListener != null) {
//            return userSearchListener;
//        }
//
//        SearchServiceInterface searchService = createUserDatabaseSearchService();
//
//        userSearchListener = new DistributedSearchListener(USERS);
//        userSearchListener.setSearchService(searchService);
//        userSearchListener.setHazelcastInstance(hazelcastInstance);
//        userSearchListener.initialize();
//
//        return userSearchListener;
//    }

    /**
     * ProceduresSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    /*@Provides
    @Named("Procedures")
    @Singleton
    public DistributedSearchListener provideProceduresSearchListener(HazelcastInstance hazelcastInstance) {

        if (proceduresSearchListener != null) {
            return proceduresSearchListener;
        }

        SearchServiceInterface searchService = new ModelSearchService(PROCEDURES, properties.getProperty(PROCEDURE_BASE_DIRECTORY));

        proceduresSearchListener = new DistributedSearchListener(PROCEDURES);
        proceduresSearchListener.setSearchService(searchService);
        proceduresSearchListener.setHazelcastInstance(hazelcastInstance);
        proceduresSearchListener.initialize();

        return proceduresSearchListener;
    }*/
    public JpaPersistModule getUserJpaModule() {
        if (userJpaModule == null) {
            new JpaPersistModule("STOCKS_MYSQL");
        }
        return userJpaModule;
    }

    public static Injector getJpaStocksInjector() {

        if (jpaStocksInjector == null) {
            jpaStocksInjector = Guice.createInjector(new JpaPersistModule("STOCKS_MYSQL"));
            PersistService service = jpaStocksInjector.getInstance(PersistService.class);
            service.start();
        }
        return jpaStocksInjector;
    }

    public static Injector getJpaUsersInjector() {

        if (jpaUsersInjector == null) {
            userJpaModule = new JpaPersistModule("USERS_MYSQL");
            jpaUsersInjector = Guice.createInjector(userJpaModule);
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
        hazelcastConfig.setInstanceName(NODE_NAME);
        GuiceManagedContext managedContext = new GuiceManagedContext();
        hazelcastConfig.setManagedContext(managedContext);

        // create stock-groups map-store
        if ("true".equals(properties.getProperty(CREATE_STOCK_GROUPS))) {
            createStockGroupsConfig(hazelcastConfig);
            localServices.add(STOCK_GROUPS);
        }

        // create Stock_Entities map-store
        if ("true".equals(properties.getProperty(CREATE_STOCK_ENTITIES))) {
            createStockEntitiesConfig(hazelcastConfig);
            localServices.add(STOCK_ENTITIES);
        }

        // create StockQuotes map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_STOCK_QUOTES))) {
            createStockQuotesConfig(hazelcastConfig);
            localServices.add(STOCK_QUOTES);
        }

        // create Procedures map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_PROCEDURES))) {
            createProceduresConfig(hazelcastConfig);
            localServices.add(PROCEDURES);
        }

        // create Wikipedia map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKIPEDIA))) {
            createWikipediaConfig(hazelcastConfig);
            localServices.add(WIKIPEDIA);
        }

        // create Wikipedia-Resources map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKIPEDIA_RESOURCES))) {
            createWikipediaResourcesConfig(hazelcastConfig);
            localServices.add(WIKIPEDIA_RESOURCES);
        }

        // create Wiktionary map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKTIONARY))) {
            createWiktionaryConfig(hazelcastConfig);
            localServices.add(WIKTIONARY);
        }

        // create Wiktionary-Resources map-store
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_WIKTIONARY_RESOURCES))) {
            createWiktionaryResourceConfig(hazelcastConfig);
            // interesting that this doesn't really exist :)
        }

        // create User database and Hazelcast map
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_USERS))) {
            createUsersMapConfig(hazelcastConfig);
            localServices.add(USERS);
        }

        // create UserRoles and Hazelcast map
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_USER_ROLES))) {
            createUserRolesConfig(hazelcastConfig);
            localServices.add(USER_ROLES);
        }

        // create UserSessions
        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_USER_SESSIONS))) {
            createUserSessionsConfig(hazelcastConfig);
            localServices.add(USER_SESSIONS);
        }

        if ("true".equalsIgnoreCase(properties.getProperty(CREATE_UPLOAD_DIRECTORY))) {
            createUploadDirectoryConfig(hazelcastConfig);
            localServices.add(PDF_FILE_RESOURCES);
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

    private void createUploadDirectoryConfig(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(PDF_FILE_RESOURCES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig sessionMapStoreConfig = mapConfig.getMapStoreConfig();
        if (sessionMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + PDF_FILE_RESOURCES);
            sessionMapStoreConfig = new MapStoreConfig();
        }

        uploadDirectoryMapStore = new DirectoryMapStore(properties.getProperty(UPLOAD_FILE_DIRECTORY));

        sessionMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, ResourceData>() {
            public MapLoader<String, ResourceData> newMapStore(String mapName, Properties properties) {
                if (PDF_FILE_RESOURCES.equals(mapName)) {
                    return uploadDirectoryMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + PDF_FILE_RESOURCES);
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
        DirectorySearchService directorySearchService = new DirectorySearchService(WIKTIONARY_RESOURCES, properties.getProperty(WIKTIONARY_RESOURCE_INDEX));
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
        DirectorySearchService directorySearchService = new DirectorySearchService(WIKIPEDIA_RESOURCES, properties.getProperty(WIKIPEDIA_RESOURCE_INDEX));
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

        procedureMapStore = new PersistentModelMapStore(Procedure.class, properties.getProperty(PROCEDURE_MODEL_DIRECTORY));
        ((PersistentModelMapStore) procedureMapStore).init();

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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Set<String> getLocalServices() {
        return localServices;
    }

    public void setLocalServices(Set<String> localServices) {
        this.localServices = localServices;
    }
}
