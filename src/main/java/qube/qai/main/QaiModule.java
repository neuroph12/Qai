package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.Syntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.stores.DataStore;
import qube.qai.data.stores.StockEntityDataStore;
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
@BindConfig(value = "qube/qai/main/config_dev", syntax = Syntax.PROPERTIES)
//@BindConfig(value = "qube/qai/main/config_deploy", syntax = Syntax.PROPERTIES)
public class QaiModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("Qai-Module");

//    private static String wiktionaryDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
//
//    private static String wiktionaryZipFileName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
//
//    private static String wikipediaDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";
//
//    private static String wikipediaResources = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";
//
//    private static String wikipediaZipFileName = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    @InjectConfig(value = "PERSISTENCE_BASE")
    public String PERSISTENCE_BASE;

    //private static String stockQuotesDirectory = "data/stockquotes/";

    private static final ThreadLocal<EntityManager> entityManagerCache = new ThreadLocal<EntityManager>();

    @Override
    protected void configure() {

        logger.info("Guice initialization called- binding services");

        // load the given configuration for
        install(ConfigurationModule.create());
        requestInjection(this);

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);

        // executorService
        bind(ProcedureRunnerInterface.class).to(ProcedureRunner.class);

        bind(MessageQueueInterface.class).to(MessageQueue.class);

        // stockEntityDataStore
        bind(DataStore.class).annotatedWith(Names.named("StockEntities")).to(StockEntityDataStore.class);

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
     * EntityManagerFactory is used in HsqlDBMapStores
     * and only there... StockEntities, RDFTriples and StockQuotes
     * @return
     */
    @Provides @Singleton //@Named("STOCKS_DB")
    public EntityManagerFactory provideStocksDBEntityManagerFactory() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        properties.put("hibernate.connection.url", "jdbc:hsqldb:" + PERSISTENCE_BASE);
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

//    @Provides @Singleton @Named("PERSONDATA_EN")
//    public EntityManagerFactory providePersondataEnEntityManagerFactory() {
//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
//        properties.put("hibernate.connection.url", "jdbc:hsqldb:" + PERSISTENCE_BASE + "persondata_en/");
//        properties.put("hibernate.connection.username", "sa");
//        properties.put("hibernate.connection.password", "");
//        properties.put("hibernate.connection.pool_size", "1");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//
//        properties.put("current_session_context_class", "org.hibernate.context.ManagedSessionContext");
//        properties.put("hibernate.cache.use_second_level_cache", "false");
//        properties.put("hibernate.cache.use_query_cache", "false");
//        properties.put("cache.provider_class", "org.hibernate.cache.NoCacheProvider");
//        properties.put("show_sql", "true");
//
//        return Persistence.createEntityManagerFactory("persondata_en", properties);
//    }

//    @Provides @Singleton @Named("DBPEDIA_EN")
//    public EntityManagerFactory provideDbPediaEnEntityManagerFactory() {
//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
//        properties.put("hibernate.connection.url", "jdbc:hsqldb:" + PERSISTENCE_BASE + "/dbpedia_en/");
//        properties.put("hibernate.connection.username", "sa");
//        properties.put("hibernate.connection.password", "");
//        properties.put("hibernate.connection.pool_size", "1");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//
//        properties.put("current_session_context_class", "org.hibernate.context.ManagedSessionContext");
//        properties.put("hibernate.cache.use_second_level_cache", "false");
//        properties.put("hibernate.cache.use_query_cache", "false");
//        properties.put("cache.provider_class", "org.hibernate.cache.NoCacheProvider");
//        properties.put("show_sql", "true");
//
//        return Persistence.createEntityManagerFactory("dbpedia_en", properties);
//    }

    @Provides
    public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerCache.get();
        if (entityManager == null) {
            entityManagerCache.set(entityManager = entityManagerFactory.createEntityManager());
        }
        return entityManager;
    }
}
