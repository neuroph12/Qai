package qube.qai.persistence.mapstores;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import qube.qai.main.QaiTestServerModule;
import qube.qai.persistence.StockEntity;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import qube.qai.services.implementation.UUIDService;

/**
 * Created by rainbird on 6/4/16.
 */
public class TestStockEntityMapStore extends TestCase {

    /**
     * in this case, we will be storing the Stock-Quotes in HsqlDb
     * i am not sure i really want to keep a copy of them, to be honest
     * but for completeness' sake, i will be storing them away in HsqlDb
     * so that they can be collected with sql-statements as well, if need be
     * @throws Exception
     */
    public void testStockEntityMapStore() throws Exception {

//        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_STOCKS"));
//        PersistService service = injector.getInstance(PersistService.class);
//        service.start();

        Injector injector = QaiTestServerModule.initStocksInjector();

        // the fields in class will have to be injected
        StockEntityMapStore mapStore = new StockEntityMapStore();
        injector.injectMembers(mapStore);

        int number = 100;
        Map<String, StockEntity> entityMap = new HashMap<String, StockEntity>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = createEntity(name);
            String uuid = entity.getUuid();
            if (uuid == null || "".equals(uuid)) {
                uuid = UUIDService.uuidString();
                entity.setUuid(uuid);
            }
            mapStore.store(uuid, entity);
            entityMap.put(uuid, entity);
        }

        // in this case the map-store should be returning all keys
        Iterable<String> storedKeys = mapStore.loadAllKeys();
        assertNotNull("stored keys may not be null", storedKeys);

        // now read them back from database
        for (String entityId : entityMap.keySet()) {
            StockEntity cachedEntity = entityMap.get(entityId);
            StockEntity storedEntity = mapStore.load(entityId);
            assertNotNull("there has to be an entity", storedEntity);
            assertTrue("entities have to be equal", cachedEntity.equals(storedEntity));
        }

        // when we are done we delete the things as well, just to keep things managable
        for (String entityId : entityMap.keySet()) {
            mapStore.delete(entityId);
        }
    }

    /**
     * creates a silly StockEntity
     * @param name
     * @return
     */
    public static StockEntity createEntity(String name) {
        StockEntity entity = new StockEntity();
        entity.setName(name);
        entity.setAddress("address of " + name);
        entity.setGicsSector("gicsSector of " + name);
        entity.setGicsSubIndustry("gicsSubIndustry of " + name);
        entity.setSecurity("security of " + name);
        entity.setTradedIn("vsex");
        entity.setTickerSymbol(name);
        return entity;
    }
}
