package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.Syntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.message.MessageQueue;
import qube.qai.message.MessageQueueInterface;
import qube.qai.services.*;
import qube.qai.services.implementation.*;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 11/19/15.
 */
@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
public class QaiTestModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("QaiTestModule");

    private static String NODE_NAME = "QaiTestNode";

    private HazelcastInstance hazelcastInstance;

    private static String wikipediaDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";
    //private static String wikipediaDirectory = "/media/pi/BET/wiki-archives/wikipedia_en.index";

    private static String wikipediaZipFileName = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";
    //private static String wikipediaZipFileName = "/media/pi/BET/wiki-archives/wikipedia_en.zip";

    private static String wiktionaryDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    //private static String wiktionaryDirectory = "/media/pi/BET/wiki-archives/wiktionary_en.index";

    private static String wiktionaryZipFileName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //private static String wiktionaryZipFileName = "/media/pi/BET/wiki-archives/wiktionary_en.zip";

    private static String STOCK_QUOTES_DIRECTORY = "test/stockquotes/";

    @InjectConfig(value = "PERSISTENCE_BASE")
    public String PERSISTENCE_BASE;

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<EntityManager>();

    @Override
    protected void configure() {

        logger.info("Guice initialization called- binding services");

        // load the given configuration for
        install(ConfigurationModule.create());
        requestInjection(this);

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);

        // ProcedureSource
        bind(ProcedureSourceInterface.class).to(TestProcedureSourceService.class);

        // executorService
        bind(ProcedureRunnerInterface.class).to(ProcedureRunner.class);

        // messageQueue
        bind(MessageQueueInterface.class).to(MessageQueue.class);
    }

    @Provides
    public StockQuoteDataStore provideStockQuoteDataStore() {
        StockQuoteDataStore dataStore = new StockQuoteDataStore();
        return dataStore;
    }


    @Provides @Singleton
    HazelcastInstance provideHazelcastInstance() {
        if (hazelcastInstance != null) {
            return hazelcastInstance;
        }

        ClientConfig clientConfig = new ClientConfig();
        //clientConfig.setInstanceName(NODE_NAME);
        clientConfig.getNetworkConfig().addAddress("127.0.0.1:5701");
        hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        return hazelcastInstance;
    }

    @Provides
    SelectorFactoryInterface provideSelectorFactoryInterface() {
        SelectorFactoryInterface selectorfactory = new DataSelectorFactory();

        return selectorfactory;
    }

    @Provides @Named("Wiktionary_en")
    SearchServiceInterface provideWiktionarySearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wiktionaryDirectory, wiktionaryZipFileName);

        return searchService;
    }

    @Provides @Named("Wikipedia_en")
    SearchServiceInterface provideWikipediaSearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wikipediaDirectory, wikipediaZipFileName);

        return searchService;
    }
}
