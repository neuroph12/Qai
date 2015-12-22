package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.message.MessageQueue;
import qube.qai.message.MessageQueueInterface;
import qube.qai.services.*;
import qube.qai.services.implementation.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("Qai-Module");

    private static String wiktionaryDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";

    private static String wiktionaryZipFileName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";

    private static String wikipediaDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";

    private static String wikipediaZipFileName = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    private static String STOCK_QUOTES_DIRECTORY = "data/stockquotes/";

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<EntityManager>();

    @Override
    protected void configure() {

        logger.info("Guice initialization called- binding services");

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);

        // executorService
        bind(ProcedureRunnerInterface.class).to(ProcedureRunner.class);

        bind(MessageQueueInterface.class).to(MessageQueue.class);

    }

    /**
     * @TODO this was mainly for test reasons, i think it is now time to remove it
     * @return
     */
    @Provides
    ProcedureSourceInterface provideProcedureSourceInterface() {
        return CachedProcedureSourceService.getInstance();
    }

    /**
     * used mainly in procedures themselves so that procedures can
     * get their data from hazelcast and return their data via hazelcast as well
     * @return
     */
    @Provides
    SelectorFactoryInterface provideSelectorFactoryInterface() {

        SelectorFactoryInterface selectorfactory = new HazelcastSelectorFactory();

        return selectorfactory;
    }

    /**
     * WiktionarySearchService
     * @return
     */
    @Provides @Named("Wiktionary_en")
    SearchServiceInterface provideWiktionarySearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wiktionaryDirectory, wiktionaryZipFileName);

        return searchService;
    }

    /**
     * WikipediaSearchService
     * @return
     */
    @Provides @Named("Wikipedia_en")
    SearchServiceInterface provideWikipediaSearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wikipediaDirectory, wikipediaZipFileName);

        return searchService;
    }

    /**
     * EntityManagerFactory is used in HsqlDBMapStores
     * and only there... StockEntities
     * @return
     */
    @Provides @Singleton
    public EntityManagerFactory provideEntityManagerFactory() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        properties.put("hibernate.connection.url", "jdbc:hsqldb:" + STOCK_QUOTES_DIRECTORY);
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.pool_size", "1");
        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "create");

        properties.put("current_session_context_class", "org.hibernate.context.ManagedSessionContext");
        properties.put("hibernate.cache.use_second_level_cache", "false");
        properties.put("hibernate.cache.use_query_cache", "false");
        properties.put("cache.provider_class", "org.hibernate.cache.NoCacheProvider");
        properties.put("show_sql", "true");

        return Persistence.createEntityManagerFactory("db-manager", properties);
    }

    @Provides
    public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
        if (entityManager == null) {
            ENTITY_MANAGER_CACHE.set(entityManager = entityManagerFactory.createEntityManager());
        }
        return entityManager;
    }
}
