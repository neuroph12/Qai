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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.persistence.StockQuote;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static qube.qai.procedure.finance.StockEntityInitialization.NUMBER_OF_RECORDS_CREATED;


/**
 * Created by rainbird on 1/21/17.
 */
public class TestStockEntityInitialization extends TestCase {

    private static Logger logger = LoggerFactory.getLogger("TestStockEntityInitialization");

    // "STAND_ALONE_TEST_STOCKS" is for running test regularly
    // "TEST_STOCKS" is what all test-routines with hazelcast use
    // "STOCKS_HSQLDB" is what all test-routines with hazelcast use
    private String jpaModuleName = "STOCKS_MYSQL"; // is the life database

    public void estInitializedDatabase() throws Exception {

        Injector injector = createInjector(jpaModuleName);
        EntityManager entityManager = injector.getInstance(EntityManager.class);
        StockQuoteDataStore store = new StockQuoteDataStore();

        Query query = entityManager.createQuery("SELECT g FROM StockGroup AS g");
        List<StockGroup> groups = query.getResultList();
        assertNotNull("result may not be null", groups);
        assertTrue("resultset may not be empty", !groups.isEmpty());

        Set<String> uuids = new HashSet<>();
        StockGroup group = groups.iterator().next();
        Set<StockEntity> entities = group.getEntities();
        for (StockEntity entity : entities) {
            Set<StockQuote> quotes = entity.getQuotes();
            if (quotes == null || quotes.isEmpty()) {
                logger.info("Entity " + entity.getTickerSymbol() + " has no quotes- trying to add");
                quotes = store.retrieveQuotesFor(entity.getTickerSymbol());
                if (quotes != null && !quotes.isEmpty()) {

                    entityManager.getTransaction().begin();
                    for (StockQuote quote : quotes) {
                        quote.setParentUUID(entity.getUuid());
                        entityManager.persist(quote);
                    }

                    entity.setQuotes(quotes);
                    entityManager.merge(entity);
                    entityManager.flush();
                    entityManager.getTransaction().commit();

                    uuids.add(entity.getUuid());
                    logger.info("Added " + quotes.size() + " quotes to " + entity.getTickerSymbol());
                }
            } else {
                logger.info("Entity " + entity.getTickerSymbol() + " has already " + entity.getQuotes().size() + " quotes in database");
            }
        }

        // now we read the entities with the uuids which we saved and check their quotes
        for (String uuid : uuids) {

            StockEntity entity = entityManager.find(StockEntity.class, uuid);
            assertNotNull("there has to be an entity", entity);
            assertNotNull("entity has to have quotes", entity.getQuotes());
            assertTrue("there has to be quotes", !entity.getQuotes().isEmpty());

        }

    }

    public void testStockEntityInitializationProcedure() throws Exception {
        //String jpaModuleName = "STAND_ALONE_TEST_STOCKS";
        Injector injector = createInjector(jpaModuleName);
        EntityManager entityManager = injector.getInstance(EntityManager.class);
        StockEntityInitialization procedure = new StockEntityInitialization();
        procedure.addQuotes = false;
        procedure.setEntityManager(entityManager);

        String[] listings = {StockEntityInitialization.S_AND_P_500_LISTING
                , StockEntityInitialization.NYSE_LISTING
//                , StockEntityInitialization.OTHER_LISTED_ENTITIES
                , StockEntityInitialization.NASDAQ_LISTING
        };

        //int overallCount = 0;
        for (String listingName : listings) {
            procedure.setGroupName(listingName);
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

        //StockQuoteDataStore store = new StockQuoteDataStore();
//        StockGroup group = groups.iterator().next();
//        Set<StockEntity> entities = group.getEntities();
//        for (StockEntity entity : entities) {
//            log("'" + group.getName() + "' entity: '" + entity.getName() + "'");
//            log("'" + entity.getName() + "' quotes is empty: '" + entity.getQuotes().isEmpty() + "'");
//            if (entity.getQuotes() == null || entity.getQuotes().isEmpty()) {
//                entityManager.getTransaction().begin();
//                Set<StockQuote> quotes = store.retrieveQuotesFor(entity.getTickerSymbol());
//                if (quotes != null && !quotes.isEmpty()) {
//                    entity.setQuotes(quotes);
//                    entityManager.persist(entity);
//                }
//                entityManager.getTransaction().commit();
//            }
//
//        }


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
