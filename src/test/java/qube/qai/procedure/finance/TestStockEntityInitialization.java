/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.procedure.finance;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Set;

import static qube.qai.procedure.finance.StockEntityInitialization.NUMBER_OF_RECORDS_CREATED;


/**
 * Created by rainbird on 1/21/17.
 */
public class TestStockEntityInitialization extends TestCase {

//    public void restStockEntityInitialization() throws Exception {
//
//        Injector injector = QaiTestServerModule.initStocksInjector();
//
//        MapStore categoryMapStore = new DatabaseMapStore(StockGroup.class);
//        injector.injectMembers(categoryMapStore);
//
//        StockGroup category = new StockGroup();
//        category.setName("Standard and Poor top 500");
//
//        for (int i = 0; i < 10; i++) {
//            StockEntity entity = TestDatabaseMapStores.createEntity("stock_entity_" + i);
//            category.addStockEntity(entity);
//        }
//
//        categoryMapStore.store(category.getUuid(), category);
//
//        StockGroup foundCategory = (StockGroup) categoryMapStore.load(category.getUuid());
//        assertNotNull(foundCategory);
//        assertTrue(foundCategory.getEntities() != null && !foundCategory.getEntities().isEmpty());
//
//        EntityManager entityManager = ((DatabaseMapStore) categoryMapStore).getEntityManager();
//        String queryString = "select c from StockEntity c where c.tickerSymbol like '%s' and c.tradedIn like '%s'";
//        for (StockEntity entity : category.getEntities()) {
//            String tickerSymbol = entity.getTickerSymbol();
//            String tradedIn = entity.getTradedIn();
//            String query = String.format(queryString, tickerSymbol, tradedIn);
//            StockEntity foundEntity = entityManager.createQuery(query, StockEntity.class).getSingleResult();
//            assertNotNull(foundEntity);
//            assertTrue(foundEntity.getUuid().equals(entity.getUuid()));
//        }
//    }

    public void testStockEntityInitializationProcedure() throws Exception {

        // "STAND_ALONE_TEST_STOCKS" is for running test regularly
        // "TEST_STOCKS" is what all test-routines with hazelcast use
        // "STOCKS" is the life database
        String jpaModuleName = "STAND_ALONE_TEST_STOCKS";
        Injector injector = createInjector(jpaModuleName);
        EntityManager entityManager = injector.getInstance(EntityManager.class);
        StockEntityInitialization procedure = new StockEntityInitialization();
        procedure.setEntityManager(entityManager);

        String[] listings = {StockEntityInitialization.S_AND_P_500_LISTING
//                , StockEntityInitialization.NYSE_LISTING
//                , StockEntityInitialization.OTHER_LISTED_ENTITIES
//                , StockEntityInitialization.NASDAQ_LISTING
        };

        //int overallCount = 0;
        for (String listingName : listings) {
            procedure.setGroupName("Testing: " + listingName);
            procedure.setSelectedFile(listingName);

            procedure.execute();
            Integer recordsCreated = (Integer) procedure.getProcedureDescription().getProcedureResults().getNamedResult(NUMBER_OF_RECORDS_CREATED).getValue();
            assertTrue("all has gone good!", procedure.hasExecuted());
            log("--------------------------------------------------------------------");
            log("listings created:" + listings[0]);
            log("altogether: " + recordsCreated + " stock-entities added in the database");
            log("---------------------------------------------------------------------");
        }

        // now we read the data from the database which has been imported
        Query query = entityManager.createQuery("SELECT g FROM StockGroup AS g");
        List<StockGroup> groups = query.getResultList();
        assertNotNull("result may not be null", groups);
        assertTrue("resultset may not be empty", !groups.isEmpty());

        StockGroup group = groups.iterator().next();
        Set<StockEntity> entities = group.getEntities();
        for (StockEntity entity : entities) {
            log("'" + group.getName() + "' eleement entity: '" + entity.getName() + "'");
        }


    }

    private Injector createInjector(String moduleName) {
        Injector injector = Guice.createInjector(new JpaPersistModule(moduleName));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();
        return injector;
    }

    private void log(String message) {
        System.out.println(message);
    }
}
