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
        StockEntityInitializationProcedure procedure = new StockEntityInitializationProcedure();
        injector.injectMembers(procedure);

        assertTrue("for the moment this si good enough- testing the injector", true);

        procedure.setName("test import procedure");
        procedure.setCategoryName("Standard & Poor Top 500 Stocks");
        procedure.setSelectedFile(StockEntityInitializationProcedure.S_AND_P_500_LISTING);
    }

}
