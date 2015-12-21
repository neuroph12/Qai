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
import qube.qai.persistence.mapstores.HqslDBMapStore;

import java.util.Properties;

/**
 * Created by rainbird on 12/21/15.
 */
public class QaiTestServerModule extends AbstractModule {

    public static String NODE_NAME = "QaiTestNode";

    public static String STOCK_ENTITIES = "STOCK_ENTITIES";

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

        // add the map-store for stock-entities
        MapConfig stockEntityConfig = config.getMapConfig(STOCK_ENTITIES);
        MapStoreConfig stockQuoteMapstoreConfig = stockEntityConfig.getMapStoreConfig();
        if (stockEntityConfig == null) {
            System.out.println("mapStoreConfig is null... creating one for: STOCK_ENTITIES");

            stockQuoteMapstoreConfig = new MapStoreConfig();
            stockQuoteMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockEntity>() {
                public MapLoader<String, StockEntity> newMapStore(String mapName, Properties properties) {
                    if ("STOCK_ENTITIES".equals(mapName)) {
                        return new HqslDBMapStore();
                    } else {
                        return null;
                    }
                }
            });

            stockEntityConfig.setMapStoreConfig(stockQuoteMapstoreConfig);
        }

        // map-store for Wikipedia_en
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
