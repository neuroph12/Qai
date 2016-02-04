package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
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
import qube.qai.persistence.mapstores.DirectoryMapStore;
import qube.qai.persistence.mapstores.HqslDBMapStore;
import qube.qai.persistence.mapstores.IndexedDirectoryMapStore;
import qube.qai.persistence.mapstores.WikiArticleMapStore;
import qube.qai.procedure.Procedure;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;
import qube.qai.services.implementation.DistributedSearchListener;
import qube.qai.services.implementation.DistributedSearchService;
import qube.qai.services.implementation.WikiSearchService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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

    public static final String STOCK_ENTITIES = "STOCK_ENTITIES";

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

    private HazelcastInstance hazelcastInstance;

    @Inject
    private EntityManager entityManager;

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
     * @return
     */
    @Provides @Named("Wiktionary_en") @Singleton
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
     * @return
     */
    @Provides @Named("Wikipedia_en") @Singleton
    DistributedSearchListener provideWikipediaSearchListener() {
        SearchServiceInterface basicSearchService = new WikiSearchService(WIKIPEDIA_DIRECTORY, WIKIPEDIA_ARCHIVE);

        DistributedSearchListener searchListener = new DistributedSearchListener("Wikipedia_en");
        searchListener.setSearchService(basicSearchService);
        searchListener.setHazelcastInstance(hazelcastInstance);
        searchListener.initialize();

        return searchListener;
    }

    /**
     * WiktionarySearchService
     * @return
     */
    @Provides @Named("Wiktionary_en")
    SearchServiceInterface provideWiktionarySearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(WIKTIONARY_DIRECTORY, WIKTIONARY_ARCHIVE);

        return searchService;
    }

    /**
     * WikipediaSearchService
     * @return
     */
    @Provides @Named("Wikipedia_en")
    SearchServiceInterface provideWikipediaSearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(WIKIPEDIA_DIRECTORY, WIKIPEDIA_ARCHIVE);

        return searchService;
    }

    @Provides
    HazelcastInstance provideHazelcastInstance() {

        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }

        Config hazelcastConfig = new Config(NODE_NAME);

        /**
         * here we add the map-store for Stock-entities which is
         * in this case the HsqlDBMapStore
         */
        MapConfig stockEntitiesConfig = hazelcastConfig.getMapConfig(STOCK_ENTITIES);
        MapStoreConfig stockEntitiesMapstoreConfig = stockEntitiesConfig.getMapStoreConfig();
        if (stockEntitiesMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + STOCK_ENTITIES);
            stockEntitiesMapstoreConfig = new MapStoreConfig();

        }
        stockEntitiesMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockEntity>() {
            public MapLoader<String, StockEntity> newMapStore(String mapName, Properties properties) {
                if (STOCK_ENTITIES.equals(mapName)) {
                    return new HqslDBMapStore(entityManager);
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + STOCK_ENTITIES);
        stockEntitiesConfig.setMapStoreConfig(stockEntitiesMapstoreConfig);

        /**
         * here we add the map-store for Procedures which is
         * in this case DirectoryMapStore
         */
        MapConfig procedureConfig = hazelcastConfig.getMapConfig(PROCEDURES);
        MapStoreConfig procedureMapstoreConfig = procedureConfig.getMapStoreConfig();
        if (procedureMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + PROCEDURES);
            procedureMapstoreConfig = new MapStoreConfig();
        }
        procedureMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, Procedure>() {
            public MapLoader<String, Procedure> newMapStore(String mapName, Properties properties) {
                if (PROCEDURES.equals(mapName)) {
                    return new DirectoryMapStore(PROCEDURE_BASE_DIRECTORY);
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
        MapConfig wikipediaConfig = hazelcastConfig.getMapConfig(WIKIPEDIA);
        MapStoreConfig wikipediaMapstoreConfig = wikipediaConfig.getMapStoreConfig();
        if (wikipediaMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA);
            wikipediaMapstoreConfig = new MapStoreConfig();
        }
        wikipediaMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, WikiArticle>() {
            public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties) {
                if (WIKIPEDIA.equals(mapName)) {
                    return new WikiArticleMapStore(WIKIPEDIA_ARCHIVE);
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + WIKIPEDIA);
        wikipediaConfig.setMapStoreConfig(wikipediaMapstoreConfig);

        /**
         * wikipedia resources
         */
        MapConfig wikipediaResourceConfig = hazelcastConfig.getMapConfig(WIKIPEDIA_RESOURCES);
        MapStoreConfig wikiResourceMapstoreConfig = wikipediaResourceConfig.getMapStoreConfig();
        if (wikiResourceMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA_RESOURCES);
            wikiResourceMapstoreConfig = new MapStoreConfig();
        }
        wikiResourceMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, File>() {
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
        wikipediaResourceConfig.setMapStoreConfig(wikiResourceMapstoreConfig);

        /**
         * wiktionary-article map-store
         */
        MapConfig wiktionaryConfig = hazelcastConfig.getMapConfig(WIKTIONARY);
        MapStoreConfig wiktionaryMapstoreConfig = wiktionaryConfig.getMapStoreConfig();
        if (wiktionaryMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKTIONARY);
            wiktionaryMapstoreConfig = new MapStoreConfig();
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
         * wiktionary resources
         */
        MapConfig wiktionaryResourceConfig = hazelcastConfig.getMapConfig(WIKTIONARY_RESOURCES);
        MapStoreConfig wiktionaryResourceMapstoreConfig = wiktionaryResourceConfig.getMapStoreConfig();
        if (wiktionaryResourceMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKTIONARY_RESOURCES);
            wiktionaryResourceMapstoreConfig = new MapStoreConfig();
        }
        wiktionaryResourceMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, File>() {
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
        wiktionaryResourceConfig.setMapStoreConfig(wiktionaryResourceMapstoreConfig);

        // now we are ready to get an instance
        hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        return hazelcastInstance;
    }
}
