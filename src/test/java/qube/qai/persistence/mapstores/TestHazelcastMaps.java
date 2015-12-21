package qube.qai.persistence.mapstores;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainbird on 12/21/15.
 */
public class TestHazelcastMaps extends QaiBaseTestCase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    public void testHazelcastInstance() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);

        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String,StockEntity> stockEntities = hazelcastInstance.getMap("STOCK_ENTITIES");
        assertNotNull("there has to be a map", stockEntities);
        int number = 100;
        List<String> uuidList = new ArrayList<String>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = TestMapStore.createEntity(name);
            String uuid = entity.getUuid();
            stockEntities.put(uuid, entity);
            uuidList.add(uuid);
        }

        for (String uuid : uuidList) {
            assertTrue("we just put this one 'ere", stockEntities.containsKey(uuid));
        }

        for (String uuid : stockEntities.keySet()) {
            StockEntity entity = stockEntities.get(uuid);
            assertNotNull(entity);
            if (!uuidList.contains(uuid)) {
                logger.info("not an entity of this test: " + entity.getName());
            }
        }

    }


}
