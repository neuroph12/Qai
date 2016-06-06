package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.*;
import qube.qai.persistence.mapstores.DirectoryMapStore;
import qube.qai.persistence.mapstores.StockEntityMapStore;
import qube.qai.persistence.mapstores.StockQuoteMapStore;
import qube.qai.persistence.mapstores.WikiArticleMapStore;
import qube.qai.persistence.search.RDFTriplesSearchService;
import qube.qai.persistence.search.StockQuoteSearchService;
import qube.qai.procedure.Procedure;
import qube.qai.services.SearchServiceInterface;

import java.util.Properties;

/**
 * Created by rainbird on 12/21/15.
 */
public class QaiTestServerModule extends AbstractModule {

    private static Logger logger = LoggerFactory.getLogger("QaiTestServerModule");

    private static final String NODE_NAME = "QaiTestNode";

    private static final String STOCK_ENTITIES = "STOCK_ENTITIES";

    private static final String STOCK_QUOTES = "STOCK_QUOTES";

    private static final String PROCEDURES = "PROCEDURES";

    private String PERSISTENCE_BASE = "/media/rainbird/ALEPH/qai-persistence.db";
    // private String PERSISTENCE_BASE = "/media/pi/BET/qai-persistence.db";

    private static final String WIKIPEDIA = "WIKIPEDIA_EN";

    private static final String WIKIPEDIA_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";
    //private static final String WIKIPEDIA_ARCHIVE = "/media/pi/BET/wiki-archives/wikipedia_en.zip";

    private static final String WIKTIONARY = "WIKTIONARY_EN";

    private static final String WIKTIONARY_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //private static final String WIKTIONARY_ARCHIVE = "/media/pi/BET/wiki-archives/wiktionary_en.zip";

    private HazelcastInstance hazelcastInstance;

    private StockEntityMapStore stockEntityMapStore;

    private StockQuoteMapStore stockQuoteMapStore;

    private Injector childInjector;

    @Override
    protected void configure() {

        bind(StockEntityMapStore.class).toInstance(stockEntityMapStore);

        bind(StockQuoteMapStore.class).toInstance(stockQuoteMapStore);
    }

    /**
     * StockQuotesSearchService
     * @return
     */
    @Provides @Named("Stock_Quotes")
    SearchServiceInterface provideStockQuoteSearchService() {

        // create an injector for initializing JPA-Module & start the service
        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_STOCKS"));
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.start();

        StockQuoteSearchService searchService = new StockQuoteSearchService();
        injector.injectMembers(searchService);

        return  searchService;
    }

    /**
     * RdfTripleSearchService
     * @return
     */
    @Provides @Named("Dbpedia_en")
    SearchServiceInterface provideDbpediaSearchService() {

        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_DBPEDIA"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        RDFTriplesSearchService searchService = new RDFTriplesSearchService();
        injector.injectMembers(searchService);
        return searchService;
    }

    /**
     * @TODO refoactir this mess!!!
     * this is more or less where everything happens
     * @return
     */
    @Provides
    HazelcastInstance provideHazelcastInstance() {

        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }

        Config config = new Config(NODE_NAME);

        if (childInjector == null) {
            childInjector = Guice.createInjector(new JpaPersistModule("STOCKS"));
            PersistService service = childInjector.getInstance(PersistService.class);
            service.start();
        }

        /**
         * here we add the map-store for Stock-entities which is
         * in this case the HsqlDBMapStore
         */
        MapConfig stockEntitiesConfig = config.getMapConfig(STOCK_ENTITIES);
        MapStoreConfig stockEntitiesMapstoreConfig = stockEntitiesConfig.getMapStoreConfig();
        if (stockEntitiesMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + STOCK_ENTITIES);
            stockEntitiesMapstoreConfig = new MapStoreConfig();

        }
        stockEntitiesMapstoreConfig.setFactoryImplementation(new MapStoreFactory<StockEntityId, StockEntity>() {
            public MapLoader<StockEntityId, StockEntity> newMapStore(String mapName, Properties properties) {
                if (STOCK_ENTITIES.equals(mapName)) {
                    if (stockEntityMapStore == null) {
                        stockEntityMapStore = new StockEntityMapStore();
                        childInjector.injectMembers(stockEntityMapStore);
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
        MapConfig stockQuotesConfig = config.getMapConfig(STOCK_QUOTES);
        MapStoreConfig stockQuotesMapstoreConfig = stockQuotesConfig .getMapStoreConfig();
        if (stockQuotesMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + STOCK_QUOTES);
            stockQuotesMapstoreConfig = new MapStoreConfig();

        }
        stockQuotesMapstoreConfig.setFactoryImplementation(new MapStoreFactory<QuoteId, StockQuote>() {
            public MapLoader<QuoteId, StockQuote> newMapStore(String mapName, Properties properties) {
                if (STOCK_QUOTES.equals(mapName)) {
                    if (stockQuoteMapStore == null) {
                        stockQuoteMapStore = new StockQuoteMapStore();
                        childInjector.injectMembers(stockQuoteMapStore);
                    }

                    return stockQuoteMapStore;
                } else {
                    return null;
                }
            }
        });
        logger.info("adding mapstore configuration for " + STOCK_QUOTES);
        stockQuotesConfig.setMapStoreConfig(stockQuotesMapstoreConfig);

        /**
         * here we add the map-store for Procedures which is
         * in this case DirectoryMapStore
         */
        MapConfig procedureConfig = config.getMapConfig(PROCEDURES);
        MapStoreConfig procedureMapstoreConfig = procedureConfig.getMapStoreConfig();
        if (procedureMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + PROCEDURES);
            procedureMapstoreConfig = new MapStoreConfig();
        }
        procedureMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, Procedure>() {
            public MapLoader<String, Procedure> newMapStore(String mapName, Properties properties) {
                if (PROCEDURES.equals(mapName)) {
                    return new DirectoryMapStore(PERSISTENCE_BASE);
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
        MapStoreConfig wikiMapstoreConfig = wikiConfig.getMapStoreConfig();
        if (wikiMapstoreConfig == null) {
            logger.info("mapStoreConfig is null... creating one for: " + WIKIPEDIA);
            wikiMapstoreConfig = new MapStoreConfig();
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
        logger.info("adding mapstore configuration for " + WIKIPEDIA_ARCHIVE);
        wikiConfig.setMapStoreConfig(wikiMapstoreConfig);

        /**
         * wiktionary-article map-store
         */
        MapConfig wiktionaryConfig = config.getMapConfig(WIKTIONARY);
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
        logger.info("adding mapstore configuration for " + WIKIPEDIA_ARCHIVE);
        wiktionaryConfig.setMapStoreConfig(wiktionaryMapstoreConfig);

        // now we are ready to get an instance
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        return hazelcastInstance;
    }
}
