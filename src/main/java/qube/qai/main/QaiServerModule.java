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
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.mapstores.HqslDBMapStore;

import java.util.Properties;

/**
 * Created by rainbird on 11/26/15.
 */
public class QaiServerModule extends AbstractModule {

    private static String NODE_NAME = "QaiNode";

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

        MapConfig stockQuoteConfig = config.getMapConfig("STOCK_QUOTES");
        MapStoreConfig stockQuoteMapstoreConfig = stockQuoteConfig.getMapStoreConfig();
        if (stockQuoteConfig == null) {
            System.out.println("mapStoreConfig is null... creating one for: STOCK_QUOTES");

            stockQuoteMapstoreConfig = new MapStoreConfig();
            stockQuoteMapstoreConfig.setFactoryImplementation(new MapStoreFactory<String, StockQuote>() {

                public MapLoader<String, StockQuote> newMapStore(String mapName, Properties properties) {
                    if ("STOCK_QUOTES".equals(mapName)) {
                        return new HqslDBMapStore();
                    } else {
                        return null;
                    }
                }
            });

            stockQuoteConfig.setMapStoreConfig(stockQuoteMapstoreConfig);
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
