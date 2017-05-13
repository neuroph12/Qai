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
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.Syntax;
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
@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
//@BindConfig(value = "qube/qai/main/config_deploy", syntax = Syntax.PROPERTIES)
public class QaiServerModule extends AbstractModule {

    private static Logger logger = LoggerFactory.getLogger("QaiServerModule");

    public static final String NODE_NAME = "QaiNode";

    public static final String USERS = "Users";

    public static final String USER_SESSIONS = "UserSessions";

    public static final String USER_ROLES = "UserRoles";

    public static final String STOCK_ENTITIES = "StockEntities";

    private static final String STOCK_QUOTES = "StockQuotes";

    public static final String PROCEDURES = "Procedures";

    @InjectConfig(value = "PROCEDURE_BASE_DIRECTORY")
    public static String PROCEDURE_BASE_DIRECTORY;

    public static final String WIKIPEDIA = "Wikipedia_en";

    @InjectConfig(value = "WIKIPEDIA_ARCHIVE")
    public static String WIKIPEDIA_ARCHIVE;
    //public static final String WIKIPEDIA_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";
    //public static final String WIKIPEDIA_ARCHIVE = "/media/pi/BET/wiki-archives/wikipedia_en.zip";

    @InjectConfig(value = "WIKIPEDIA_DIRECTORY")
    public static String WIKIPEDIA_DIRECTORY;
    //public static final String WIKIPEDIA_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";
    //public static final String WIKIPEDIA_DIRECTORY = "/media/pi/BET/wiki-archives/wikipedia_en.index";

    public static final String WIKIPEDIA_RESOURCES = "WikiResources_en";

    @InjectConfig(value = "WIKIPEDIA_RESOURCE_DIRECTORY")
    public String WIKIPEDIA_RESOURCE_DIRECTORY;
    //public static final String WIKIPEDIA_RESOURCE_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";
    //public static final String WIKIPEDIA_RESOURCE_DIRECTORY = "/media/pi/BET/wiki-archives/wikipedia_en.resources";

    @InjectConfig(value = "WIKIPEDIA_RESOURCE_INDEX")
    public String WIKIPEDIA_RESOURCE_INDEX;
    //public static final String WIKIPEDIA_RESOURCE_INDEX = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources.index";
    //public static final String WIKIPEDIA_RESOURCE_INDEX = "/media/pi/BET/wiki-archives/wikipedia_en.resources.index";

    public static final String WIKTIONARY = "Wiktionary_en";

    @InjectConfig(value = "WIKTIONARY_ARCHIVE")
    public static String WIKTIONARY_ARCHIVE;
    //public static final String WIKTIONARY_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //public static final String WIKTIONARY_ARCHIVE = "/media/pi/BET/wiki-archives/wiktionary_en.zip";

    @InjectConfig("WIKTIONARY_DIRECTORY")
    public static String WIKTIONARY_DIRECTORY;
    //public static final String WIKTIONARY_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    //public static final String WIKTIONARY_DIRECTORY = "/media/pi/BET/wiki-archives/wiktionary_en.index";

    public static final String WIKTIONARY_RESOURCES = "WIKTIONARY_RESOURCES";

    @InjectConfig(value = "WIKTIONARY_RESOURCE_DIRECTORY")
    public static String WIKTIONARY_RESOURCE_DIRECTORY;
    //public static final String WIKTIONARY_RESOURCE_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.resources";
    //public static final String WIKTIONARY_RESOURCE_DIRECTORY = "/media/pi/BET/wiki-archives/wiktionary_en.resources";

    @InjectConfig(value = "WIKTIONARY_RESOURCE_INDEX")
    public static String WIKTIONARY_RESOURCE_INDEX;
    //public static final String WIKTIONARY_RESOURCE_INDEX = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.resources.index";
    //public static final String WIKTIONARY_RESOURCE_INDEX = "/media/pi/BET/wiki-archives/wiktionary_en.resources.index";

    @InjectConfig(value = "CREATE_STOCK_ENTITIES")
    public String CREATE_STOCK_ENTITIES;

    @InjectConfig(value = "CREATE_STOCK_QUOTES")
    public String CREATE_STOCK_QUOTES;

    @InjectConfig(value = "CREATE_PROCEDURES")
    public String CREATE_PROCEDURES;

    @InjectConfig(value = "CREATE_WIKIPEDIA")
    public String CREATE_WIKIPEDIA;

    @InjectConfig(value = "CREATE_WIKIPEDIA_RESOURCES")
    public String CREATE_WIKIPEDIA_RESOURCES;

    @InjectConfig(value = "CREATE_WIKTIONARY")
    public String CREATE_WIKTIONARY;

    @InjectConfig(value = "CREATE_WIKTIONARY_RESOURCES")
    public String CREATE_WIKTIONARY_RESOURCES;

    @InjectConfig(value = "CREATE_DBPEDIA")
    public String CREATE_DBPEDIA;

    @InjectConfig(value = "CREATE_USERS")
    public String CREATE_USERS;

    @InjectConfig(value = "CREATE_USER_SESSIONS")
    public String CREATE_USER_SESSIONS;

    @InjectConfig(value = "CREATE_USER_ROLES")
    public String CREATE_USER_ROLES;

//    @InjectConfig(value = "CREATE_DBPERSON")
//    public String CREATE_DBPERSON;

    public static final String DBPEDIA = "DBPEDIA";

//    public static final String DBPERSON = "DBPERSON";

    private HazelcastInstance hazelcastInstance;

    private DatabaseMapStore stockEntityMapStore;

    private DatabaseMapStore stockQuoteMapStore;

    private DatabaseMapStore userMapStore;

    private DatabaseMapStore sessionMapStore;

    private DatabaseMapStore roleMapStore;

    private DatabaseMapStore dbpediaMapStore;

    private DatabaseMapStore dbpersonMapStore;

    private static Injector jpaStocksInjector;

    private static Injector jpaDBPediaInjector;

    private static Injector jpaDBPersonInjector;

    private static Injector jpaUsersInjector;

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

    public QaiServerModule() {

    }

    @Override
    protected void configure() {
        // load the given configuration for loading config-file
        install(ConfigurationModule.create());
        requestInjection(this);
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
    public static DistributedSearchListener provideWiktionarySearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface basicSearchService = new WikiSearchService(WIKTIONARY_DIRECTORY, WIKTIONARY_ARCHIVE);

        DistributedSearchListener searchListener = new DistributedSearchListener("Wiktionary_en");
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
    public static DistributedSearchListener provideWikipediaSearchListener(HazelcastInstance hazelcastInstance) {
        SearchServiceInterface basicSearchService = new WikiSearchService(WIKIPEDIA_DIRECTORY, WIKIPEDIA_ARCHIVE);

        DistributedSearchListener searchListener = new DistributedSearchListener("Wikipedia_en");
        searchListener.setSearchService(basicSearchService);
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
    public static DistributedSearchListener provideWikiResourcesSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = new DirectorySearchService(WIKTIONARY_RESOURCE_INDEX);

        DistributedSearchListener searchListener = new DistributedSearchListener("WikiResources_en");
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
    @Named("StockEntities")
    @Singleton
    public static DistributedSearchListener provideStockQuotesSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = new DatabaseSearchService();
        getJpaStocksInjector().injectMembers(searchService);

        DistributedSearchListener searchListener = new DistributedSearchListener("StockEntities");
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
    public static DistributedSearchListener provideUsersSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = new DatabaseSearchService();
        getJpaUsersInjector().injectMembers(searchService);

        DistributedSearchListener searchListener = new DistributedSearchListener("Users");
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
    public static DistributedSearchListener provideProceduresSearchListener(HazelcastInstance hazelcastInstance) {

        SearchServiceInterface searchService = new ModelStore(PROCEDURE_BASE_DIRECTORY);

        DistributedSearchListener searchListener = new DistributedSearchListener("Procedures");
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
        if ("true".equals(CREATE_STOCK_ENTITIES)) {
            createStockEntitiesConfig(hazelcastConfig);
        }

        // create StockQuotes map-store
        if ("true".equalsIgnoreCase(CREATE_STOCK_QUOTES)) {
            createStockQuotesConfig(hazelcastConfig);
        }

        // create Procedures map-store
        if ("true".equalsIgnoreCase(CREATE_PROCEDURES)) {
            createProceduresConfig(hazelcastConfig);
        }

        // create Wikipedia map-store
        if ("true".equalsIgnoreCase(CREATE_WIKIPEDIA)) {
            createWikipediaConfig(hazelcastConfig);
        }

        // create Wikipedia-Resources map-store
        if ("true".equalsIgnoreCase(CREATE_WIKIPEDIA_RESOURCES)) {
            createWikipediaResourcesConfig(hazelcastConfig);
        }

        // create Wiktionary map-store
        if ("true".equalsIgnoreCase(CREATE_WIKTIONARY)) {
            createWiktionaryConfig(hazelcastConfig);
        }

        // create Wiktionary-Resources map-store
        if ("true".equalsIgnoreCase(CREATE_WIKTIONARY_RESOURCES)) {
            createWiktionaryResourceConfig(hazelcastConfig);
        }

        // create DBPedia map-store
        if ("true".equalsIgnoreCase(CREATE_DBPEDIA)) {
            createDBPediaConfig(hazelcastConfig);
        }
//
//        // create DBPerson map-store
//        if ("true".equals(CREATE_DBPERSON)) {
//            if (jpaDBPersonInjector == null) {
//                jpaDBPersonInjector = Guice.createInjector(new JpaPersistModule("DBPERSON"));
//                PersistService service = jpaDBPersonInjector.getInstance(PersistService.class);
//                service.start();
//            }
//            createDBPersonConfig(hazelcastConfig);
//        }

        // create User database and Hazelcast map
        if ("true".equalsIgnoreCase(CREATE_USERS)) {
            createUsersMapConfig(hazelcastConfig);
        }

        // create UserRoles and Hazelcast map
        if ("true".equalsIgnoreCase(CREATE_USER_ROLES)) {
            createUserRoles(hazelcastConfig);
        }

        // create UserSessions
        if ("true".equalsIgnoreCase(CREATE_USER_SESSIONS)) {
            createUserSessions(hazelcastConfig);
        }

        // now we are ready to get an instance
        hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        return hazelcastInstance;
    }

    /**
     * DBPedia map-store
     */
    private void createDBPediaConfig(Config hazelcastConfig) {
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
    }

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
        userMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, User>() {
            public MapLoader<String, User> newMapStore(String mapName, Properties properties) {
                if (USERS.equals(mapName)) {
                    if (userMapStore == null) {
                        userMapStore = new DatabaseMapStore(User.class);
                        getJpaUsersInjector().injectMembers(userMapStore);
                    }
                    return userMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USERS);
        mapConfig.setMapStoreConfig(userMapstoreConfig);
    }

    private void createUserRoles(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(USER_ROLES);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig roleMapStoreConfig = mapConfig.getMapStoreConfig();
        if (roleMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USER_ROLES);
            roleMapStoreConfig = new MapStoreConfig();
        }
        roleMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Role>() {
            public MapLoader<String, Role> newMapStore(String mapName, Properties properties) {
                if (USER_ROLES.equals(mapName)) {
                    if (roleMapStore == null) {
                        roleMapStore = new DatabaseMapStore(Role.class);
                        getJpaUsersInjector().injectMembers(roleMapStore);
                    }

                    return roleMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USER_ROLES);
        mapConfig.setMapStoreConfig(roleMapStoreConfig);
    }

    private void createUserSessions(Config hazelcastConfig) {
        MapConfig mapConfig = hazelcastConfig.getMapConfig(USER_SESSIONS);
        mapConfig.getMapStoreConfig().setEnabled(true);
        MapStoreConfig sessionMapStoreConfig = mapConfig.getMapStoreConfig();
        if (sessionMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USER_SESSIONS);
            sessionMapStoreConfig = new MapStoreConfig();
        }
        sessionMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Session>() {
            public MapLoader<String, Session> newMapStore(String mapName, Properties properties) {
                if (USER_SESSIONS.equals(mapName)) {
                    if (sessionMapStore == null) {
                        sessionMapStore = new DatabaseMapStore(Session.class);
                        getJpaUsersInjector().injectMembers(sessionMapStore);
                    }

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
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, ResourceData>() {
            public MapLoader<String, ResourceData> newMapStore(String mapName, Properties properties) {
                if (WIKTIONARY_RESOURCES.equals(mapName)) {
                    IndexedDirectoryMapStore store = new IndexedDirectoryMapStore(WIKTIONARY_RESOURCE_DIRECTORY, WIKTIONARY_RESOURCE_INDEX);
                    DirectorySearchService directorySearchService = new DirectorySearchService(WIKTIONARY_RESOURCE_INDEX);
                    store.setSearchService(directorySearchService);
                    return store;
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
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, WikiArticle>() {
            public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties) {
                if (WIKTIONARY.equals(mapName)) {
                    return new WikiArticleMapStore(WIKTIONARY_ARCHIVE);
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
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, ResourceData>() {
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
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, WikiArticle>() {
            public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties) {
                if (WIKIPEDIA.equals(mapName)) {
                    return new WikiArticleMapStore(WIKIPEDIA_ARCHIVE);
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
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Procedure>() {
            public MapLoader<String, Procedure> newMapStore(String mapName, Properties properties) {
                if (PROCEDURES.equals(mapName)) {
                    return new DirectoryMapStore(PROCEDURE_BASE_DIRECTORY);
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
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockQuote>() {
            public MapLoader<String, StockQuote> newMapStore(String mapName, Properties properties) {
                if (STOCK_QUOTES.equals(mapName)) {
                    if (stockQuoteMapStore == null) {
                        stockQuoteMapStore = new DatabaseMapStore(StockQuote.class);
                        getJpaStocksInjector().injectMembers(stockQuoteMapStore);
                    }

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
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockEntity>() {
            public MapLoader<String, StockEntity> newMapStore(String mapName, Properties properties) {
                if (STOCK_ENTITIES.equals(mapName)) {
                    if (stockEntityMapStore == null) {
                        stockEntityMapStore = new DatabaseMapStore(StockEntity.class);
                        getJpaStocksInjector().injectMembers(stockEntityMapStore);
                    }

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
