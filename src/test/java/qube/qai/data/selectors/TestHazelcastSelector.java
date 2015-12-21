package qube.qai.data.selectors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import qube.qai.data.Selector;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.mapstores.TestMapStore;
import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureSource;
import qube.qai.services.UUIDServiceInterface;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 11/19/15.
 */
public class TestHazelcastSelector extends QaiBaseTestCase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureSource procedureSource;

    @Inject
    private UUIDServiceInterface uuidService;

    private String STOCK_SOURCE = "STOCK_ENTITIES";
    private String PROCEDURE_SOURCE = "PROCEDURES";

    public void restHazelcastStockEntities() throws Exception {

        IMap<String,StockEntity> stockEntities = hazelcastInstance.getMap(STOCK_SOURCE);

        int number = 100;
        Collection<Selector> selectors = new ArrayList<Selector>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = TestMapStore.createEntity(name);
            String uuid = entity.getUuid();
            stockEntities.put(uuid, entity);
            Selector<StockEntity> selector = new HazelcastSelector<StockEntity>(STOCK_SOURCE, uuid);
            selectors.add(selector);
        }

        // now after putting everything in hazelcast we should be able to read them as well
        for (Selector selector : selectors) {
            injector.injectMembers(selector);
            StockEntity entity = (StockEntity) selector.getData();
            assertNotNull("there has to be a stock entity", entity);
        }
    }

    public void testHazelcastProcedures() throws Exception {

        IMap<String,Procedure> procedures = hazelcastInstance.getMap(PROCEDURE_SOURCE);

        List<Selector> selectors = new ArrayList<Selector>();
        String[] procedureNames = procedureSource.getProcedureNames();
        for (String name : procedureNames) {
            Procedure procedure = procedureSource.getProcedureWithName(name);
            assertNotNull("procedure should not be null", procedure);
            String uuid = procedure.getUuid();
            if (StringUtils.isBlank(uuid)) {
                uuid = uuidService.createUUIDString();
                procedure.setUuid(uuid);
            }
            Selector<Procedure> selector = new HazelcastSelector<Procedure>(PROCEDURE_SOURCE, uuid);
            procedures.put(uuid, procedure);
            selectors.add(selector);
        }

        // now after putting everything in hazelcast we should be able to read them as well
        for (Selector selector : selectors) {
            injector.injectMembers(selector);
            Procedure procedure = (Procedure) selector.getData();
            assertNotNull("there has to be a procedure", procedure);
        }
    }
}
