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
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.WikiArticle;
import qube.qai.persistence.mapstores.DatabaseMapStore;
import qube.qai.persistence.mapstores.DirectoryMapStore;
import qube.qai.persistence.mapstores.IndexedDirectoryMapStore;
import qube.qai.persistence.mapstores.WikiArticleMapStore;
import qube.qai.persistence.search.RDFTriplesSearchService;
import qube.qai.persistence.search.StockQuoteSearchService;
import qube.qai.procedure.Procedure;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;
import qube.qai.services.implementation.DistributedSearchListener;
import qube.qai.services.implementation.WikiSearchService;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;

import javax.inject.Named;
import java.io.File;
import java.util.Properties;

/**
 * Created by rainbird on 11/26/15.
 */
@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
//@BindConfig(value = "qube/qai/main/config_deploy", syntax = Syntax.PROPERTIES)
public class QaiServerModule extends AbstractModule {

    private static Logger logger = LoggerFactory.getLogger("QaiServerModule");

    public static final String NODE_NAME = "QaiNode";

    public static final String USERS = "USERS";

    public static final String USER_SESSIONS = "USER_SESSIONS";

    public static final String USER_ROLES = "USER_ROLES";

    public static final String STOCK_ENTITIES = "STOCK_ENTITIES";

    private static final String STOCK_QUOTES = "STOCK_QUOTES";

    public static final String PROCEDURES = "PROCEDURES";

    @InjectConfig(value = "PROCEDURE_BASE_DIRECTORY")
    public String PROCEDURE_BASE_DIRECTORY;

    public static final String WIKIPEDIA = "WIKIPEDIA_EN";

    @InjectConfig(value = "WIKIPEDIA_ARCHIVE")
    public String WIKIPEDIA_ARCHIVE;
    //public static final String WIKIPEDIA_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";
    //public static final String WIKIPEDIA_ARCHIVE = "/media/pi/BET/wiki-archives/wikipedia_en.zip";

    @InjectConfig(value = "WIKIPEDIA_DIRECTORY")
    public String WIKIPEDIA_DIRECTORY;
    //public static final String WIKIPEDIA_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";
    //public static final String WIKIPEDIA_DIRECTORY = "/media/pi/BET/wiki-archives/wikipedia_en.index";

    public static final String WIKIPEDIA_RESOURCES = "WIKIPEDIA_RESOURCES";

    @InjectConfig(value = "WIKIPEDIA_RESOURCE_DIRECTORY")
    public String WIKIPEDIA_RESOURCE_DIRECTORY;
    //public static final String WIKIPEDIA_RESOURCE_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";
    //public static final String WIKIPEDIA_RESOURCE_DIRECTORY = "/media/pi/BET/wiki-archives/wikipedia_en.resources";

    @InjectConfig(value = "WIKIPEDIA_RESOURCE_INDEX")
    public String WIKIPEDIA_RESOURCE_INDEX;
    //public static final String WIKIPEDIA_RESOURCE_INDEX = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources.index";
    //public static final String WIKIPEDIA_RESOURCE_INDEX = "/media/pi/BET/wiki-archives/wikipedia_en.resources.index";

    public static final String WIKTIONARY = "WIKTIONARY_EN";

    @InjectConfig(value = "WIKTIONARY_ARCHIVE")
    public String WIKTIONARY_ARCHIVE;
    //public static final String WIKTIONARY_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //public static final String WIKTIONARY_ARCHIVE = "/media/pi/BET/wiki-archives/wiktionary_en.zip";

    @InjectConfig("WIKTIONARY_DIRECTORY")
    public String WIKTIONARY_DIRECTORY;
    //public static final String WIKTIONARY_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    //public static final String WIKTIONARY_DIRECTORY = "/media/pi/BET/wiki-archives/wiktionary_en.index";

    public static final String WIKTIONARY_RESOURCES = "WIKTIONARY_RESOURCES";

    @InjectConfig(value = "WIKTIONARY_RESOURCE_DIRECTORY")
    public String WIKTIONARY_RESOURCE_DIRECTORY;
    //public static final String WIKTIONARY_RESOURCE_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.resources";
    //public static final String WIKTIONARY_RESOURCE_DIRECTORY = "/media/pi/BET/wiki-archives/wiktionary_en.resources";

    @InjectConfig(value = "WIKTIONARY_RESOURCE_INDEX")
    public String WIKTIONARY_RESOURCE_INDEX;
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

    private Injector jpaStocksInjector;

    private Injector jpaDBPediaInjector;

    private Injector jpaDBPersonInjector;

    private Injector jpaUsersInjector;

    public QaiServerModule() {

    }

    @Override
    protected void configure() {
        // load the given configuration for
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
    DistributedSearchListener provideWiktionarySearchListener() {
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
    DistributedSearchListener provideWikipediaSearchListener() {
        SearchServiceInterface basicSearchService = new WikiSearchService(WIKIPEDIA_DIRECTORY, WIKIPEDIA_ARCHIVE);

        DistributedSearchListener searchListener = new DistributedSearchListener("Wikipedia_en");
        searchListener.setSearchService(basicSearchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * DbpediaSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     * @return
     */
//    @Provides @Named("Dbpedia_en") @Singleton
//    DistributedSearchListener provideDbpediaSearchListener() {
//
//        SearchServiceInterface searchService = provideDbpediaSearchService();
//
//        DistributedSearchListener searchListener = new DistributedSearchListener("Dbpedia_en");
//        searchListener.setSearchService(searchService);
//        searchListener.setHazelcastInstance(hazelcastInstance);
//        searchListener.initialize();
//
//        return searchListener;
//    }

    /**
     * StockQuotesSearchService
     * returns the distributed search service for wikipedia
     * and starts the listener service which will broker the requests
     *
     * @return
     */
    @Provides
    @Named("Stock_Quotes")
    @Singleton
    DistributedSearchListener provideStockQuotesSearchListener() {

        SearchServiceInterface searchService = provideStockQuoteSearchService();

        DistributedSearchListener searchListener = new DistributedSearchListener("Stock_Quotes");
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
    @Provides
    @Named("Wiktionary_en")
    SearchServiceInterface provideWiktionarySearchService() {

        SearchServiceInterface searchService = new WikiSearchService(WIKTIONARY_DIRECTORY, WIKTIONARY_ARCHIVE);
        return searchService;
    }

    /**
     * WikipediaSearchService
     *
     * @return
     */
    @Provides
    @Named("Wikipedia_en")
    SearchServiceInterface provideWikipediaSearchService() {

        SearchServiceInterface searchService = new WikiSearchService(WIKIPEDIA_DIRECTORY, WIKIPEDIA_ARCHIVE);
        return searchService;
    }

    /**
     * StockQuotesSearchService
     *
     * @return
     */
    @Provides
    @Named("Stock_Quotes")
    SearchServiceInterface provideStockQuoteSearchService() {

        // create an injector for initializing JPA-Module & start the service
        Injector injector = Guice.createInjector(new JpaPersistModule("STOCKS"));
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.start();

        StockQuoteSearchService searchService = new StockQuoteSearchService();
        injector.injectMembers(searchService);

        return searchService;
    }

    /**
     * RdfTripleSearchService
     *
     * @return
     */
    @Provides
    @Named("Dbpedia_en")
    SearchServiceInterface provideDbpediaSearchService() {

        Injector injector = Guice.createInjector(new JpaPersistModule("DBPEDIA"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        RDFTriplesSearchService searchService = new RDFTriplesSearchService();
        injector.injectMembers(searchService);
        return searchService;
    }

    @Provides
    @Singleton
        //@Named("HAZELCAST_SERVER")
    HazelcastInstance provideHazelcastInstance() {

        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }

        Config hazelcastConfig = new Config(NODE_NAME);

        if (jpaStocksInjector == null) {
            jpaStocksInjector = Guice.createInjector(new JpaPersistModule("STOCKS"));
            PersistService service = jpaStocksInjector.getInstance(PersistService.class);
            service.start();
        }

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

        if (jpaUsersInjector == null) {
            jpaUsersInjector = Guice.createInjector(new JpaPersistModule("USERS"));
            PersistService service = jpaUsersInjector.getInstance(PersistService.class);
            service.start();
        }
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
        MapConfig usersConfig = hazelcastConfig.getMapConfig(USERS);
        MapStoreConfig userMapstoreConfig = usersConfig.getMapStoreConfig();
        if (userMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USERS);
            userMapstoreConfig = new MapStoreConfig();
        }
        userMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, User>() {
            public MapLoader<String, User> newMapStore(String mapName, Properties properties) {
                if (USERS.equals(mapName)) {
                    if (userMapStore == null) {
                        userMapStore = new DatabaseMapStore(User.class);
                        jpaUsersInjector.injectMembers(userMapStore);
                    }
                    return userMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USERS);
        usersConfig.setMapStoreConfig(userMapstoreConfig);
    }

    private void createUserRoles(Config hazelcastConfig) {
        MapConfig rolesConfig = hazelcastConfig.getMapConfig(USER_ROLES);
        MapStoreConfig roleMapStoreConfig = rolesConfig.getMapStoreConfig();
        if (roleMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USER_ROLES);
            roleMapStoreConfig = new MapStoreConfig();
        }
        roleMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Role>() {
            public MapLoader<String, Role> newMapStore(String mapName, Properties properties) {
                if (USER_ROLES.equals(mapName)) {
                    if (roleMapStore == null) {
                        roleMapStore = new DatabaseMapStore(Role.class);
                        jpaUsersInjector.injectMembers(roleMapStore);
                    }

                    return roleMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USER_ROLES);
        rolesConfig.setMapStoreConfig(roleMapStoreConfig);
    }

    private void createUserSessions(Config hazelcastConfig) {
        MapConfig sessionsConfig = hazelcastConfig.getMapConfig(USER_SESSIONS);
        MapStoreConfig sessionMapStoreConfig = sessionsConfig.getMapStoreConfig();
        if (sessionMapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + USER_SESSIONS);
            sessionMapStoreConfig = new MapStoreConfig();
        }
        sessionMapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, Session>() {
            public MapLoader<String, Session> newMapStore(String mapName, Properties properties) {
                if (USER_SESSIONS.equals(mapName)) {
                    if (sessionMapStore == null) {
                        sessionMapStore = new DatabaseMapStore(Session.class);
                        jpaUsersInjector.injectMembers(sessionMapStore);
                    }

                    return sessionMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + USER_SESSIONS);
        sessionsConfig.setMapStoreConfig(sessionMapStoreConfig);
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
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKTIONARY_RESOURCES);
            mapStoreConfig = new MapStoreConfig();
        }
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, File>() {
            public MapLoader<String, File> newMapStore(String mapName, Properties properties) {
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
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA_RESOURCES);
            mapStoreConfig = new MapStoreConfig();
        }
        mapStoreConfig.setFactoryImplementation(new MapStoreFactory<String, File>() {
            public MapLoader<String, File> newMapStore(String mapName, Properties properties) {
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
                        jpaStocksInjector.injectMembers(stockQuoteMapStore);
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
                        jpaStocksInjector.injectMembers(stockEntityMapStore);
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
