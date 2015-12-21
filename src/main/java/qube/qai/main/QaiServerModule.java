package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.mapstores.DirectoryMapStore;
import qube.qai.persistence.mapstores.HqslDBMapStore;
import qube.qai.procedure.Procedure;

import java.util.Properties;

/**
 * Created by rainbird on 11/26/15.
 */
public class QaiServerModule extends AbstractModule {

    private static String NODE_NAME = "QaiNode";

    private static final String STOCK_ENTITIES = "STOCK_ENTITIES";

    private static final String PROCEDURES = "PROCEDURES";

    private static final String PROCEDURE_BASE_DRIECTORY = "data/procedures/";

    private HazelcastInstance hazelcastInstance;

    @Override
    protected void configure() {

    }

    @Provides
    HazelcastInstance provideHazelcastInstance() {


        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }

        Config config = new Config(NODE_NAME);

        /**
         * here we add the map-store for Stock-entities which is
         * in this case the HsqlDBMapStore
         */
        MapConfig stockQuoteConfig = config.getMapConfig(STOCK_ENTITIES);
        MapStoreConfig stockQuoteMapstoreConfig = stockQuoteConfig.getMapStoreConfig();
        if (stockQuoteMapstoreConfig == null) {
            System.out.println("mapStoreConfig is null... creating one for: " + STOCK_ENTITIES);

            stockQuoteMapstoreConfig = new MapStoreConfig();
            stockQuoteMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockEntity>() {
                public MapLoader<String, StockEntity> newMapStore(String mapName, Properties properties) {
                    if (STOCK_ENTITIES.equals(mapName)) {
                        return new HqslDBMapStore();
                    } else {
                        return null;
                    }
                }
            });

            stockQuoteConfig.setMapStoreConfig(stockQuoteMapstoreConfig);
        }

        /**
         * here we add the map-store for Procedures which is
         * in this case DirectoryMapStore
         */
        MapConfig procedureConfig = config.getMapConfig(PROCEDURES);
        MapStoreConfig procedureMapstoreConfig = procedureConfig.getMapStoreConfig();
        if (procedureMapstoreConfig == null) {
            System.out.println("mapStoreConfig is null... creating one for: " + PROCEDURES);

            procedureMapstoreConfig = new MapStoreConfig();
            procedureMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, Procedure>() {
                public MapLoader<String, Procedure> newMapStore(String mapName, Properties properties) {
                    if (PROCEDURES.equals(mapName)) {
                        return new DirectoryMapStore(PROCEDURE_BASE_DRIECTORY);
                    } else {
                        return null;
                    }
                }
            });

            procedureConfig.setMapStoreConfig(procedureMapstoreConfig);
        }

        // @TODO map-store for Wiki-tarballs
        /*MapConfig wikipediaConfig = config.getMapConfig("WIKI.*");
        MapStoreConfig wikipediaMapStoreConfig = wikipediaConfig.getMapStoreConfig();
        if (wikipediaMapStoreConfig == null) {
            wikipediaMapStoreConfig.setFactoryImplementation( new MapStoreFactory<String, WikiArticle>() {

                public MapLoader<String, WikiArticle> newMapStore(String mapName, Properties properties ) {
                    if ("WIKIPEDIA_EN".equals(mapName)) {
                        return new ZipFileMapStore();
                    } else if ("WIKTIONARY_EN".equals(mapName)) {
                        return new ZipFileMapStore();
                    } else {
                        return null;
                    }
                }
            });
            wikipediaConfig.setMapStoreConfig(wikipediaMapStoreConfig);
        }*/

        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        return hazelcastInstance;
    }
}
