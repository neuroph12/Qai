package qube.qai.procedure.finance;

import com.google.inject.Injector;
import com.hazelcast.core.MapStore;
import junit.framework.TestCase;
import qube.qai.main.QaiTestBase;
import qube.qai.main.QaiTestServerModule;
import qube.qai.persistence.StockCategory;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.mapstores.DatabaseMapStore;
import qube.qai.persistence.mapstores.TestDatabaseMapStores;

import javax.persistence.EntityManager;
import java.util.Formatter;
import java.util.Set;


/**
 * Created by rainbird on 1/21/17.
 */
public class TestStockEntityInitialization extends QaiTestBase {

    public void restStockEntityInitialization() throws Exception {

        Injector injector = QaiTestServerModule.initStocksInjector();

        MapStore categoryMapStore = new DatabaseMapStore(StockCategory.class);
        injector.injectMembers(categoryMapStore);

        StockCategory category = new StockCategory();
        category.setName("Sandard and Poor top 500");

        for (int i = 0; i < 10; i++) {
            StockEntity entity = TestDatabaseMapStores.createEntity("stock_entity_" + i);
            category.addStockEntity(entity);
        }

        categoryMapStore.store(category.getUuid(), category);

        StockCategory foundCategory = (StockCategory) categoryMapStore.load(category.getUuid());
        assertNotNull(foundCategory);
        assertTrue(foundCategory.getEntities() != null && !foundCategory.getEntities().isEmpty());

        EntityManager entityManager = ((DatabaseMapStore)categoryMapStore).getEntityManager();
        String queryString = "select c from StockEntity c where c.tickerSymbol like '%s' and c.tradedIn like '%s'";
        for (StockEntity entity : category.getEntities()) {
            String tickerSymbol = entity.getTickerSymbol();
            String tradedIn = entity.getTradedIn();
            String query = String.format(queryString, tickerSymbol, tradedIn);
            StockEntity foundEntity = entityManager.createQuery(query, StockEntity.class).getSingleResult();
            assertNotNull(foundEntity);
            assertTrue(foundEntity.getUuid().equals(entity.getUuid()));
        }
    }

    public void testStockEntityInitializationProcedure() throws Exception {

        Injector injector = QaiTestServerModule.initStocksInjector();
        EntityManager entityManager = injector.getInstance(EntityManager.class);
        StockEntityInitializationProcedure procedure = new StockEntityInitializationProcedure();
        procedure.setEntityManager(entityManager);

        String[] listings = {StockEntityInitializationProcedure.S_AND_P_500_LISTING,
                StockEntityInitializationProcedure.NYSE_LISTING,
                StockEntityInitializationProcedure.OTHER_LISTED_ENTITIES,
                StockEntityInitializationProcedure.NASDAQ_LISTING
        };

        assertTrue("for the moment this is good enough- testing the injector", true);

        for (String listingName : listings) {
            procedure.setName("test import procedure");
            procedure.setCategoryName("Testing: " + listingName);
            procedure.setSelectedFile(listingName);

            procedure.execute();
            //assertTrue("all has gone good!", true);

            Set<String> resultNames = procedure.getArguments().getResultNames();
            assertTrue("there has to be result names", !resultNames.isEmpty());
            log("result names: " + resultNames.toString());

            StockCategory category = (StockCategory) procedure.getArguments().getResult(StockEntityInitializationProcedure.CATEGORY);
            assertNotNull("there has to be a category", category);
            Set<StockEntity> entities = category.getEntities();
            assertNotNull("there have to be some entities", entities);
            assertTrue("the entity listing should not be empty", !entities.isEmpty());
            for (StockEntity entity : entities) {
                log("entity: " + entity.getTickerSymbol() + ": '" + entity.getName() + "'");
            }
        }
    }

}
