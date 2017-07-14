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
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.ProcedureLibrary;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Set;

/**
 * Created by rainbird on 3/11/17.
 */
public class TestStockQuoteRetriever extends TestCase {


    public void testStockQuoteRetriever() throws Exception {

        Injector injector = createInjector();
        EntityManager entityManager = injector.getInstance(EntityManager.class);
        StockQuoteRetriever retriever = ProcedureLibrary.stockQuoteRetriverTemplate.createProcedure();
        retriever.setEntityManager(entityManager);

        String tickerSymbol = "GOOG";
        retriever.setTickerSymbol(tickerSymbol);
        retriever.execute();

        String queryString = "select o from StockEntity o where o.tickerSymbol like '" + tickerSymbol + "'";
        Query query = entityManager.createQuery(queryString);
        StockEntity entity = (StockEntity) query.getSingleResult();
        Set<StockQuote> quotes = entity.getQuotes();
        assertNotNull("there has to be quotes now", quotes);
        assertTrue("the list may not be empty", !quotes.isEmpty());

        for (StockQuote quote : quotes) {
            log(quote.getTickerSymbol() + " $" + quote.getAdjustedClose() + " on: " + quote.getQuoteDate());
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    private Injector createInjector() {
        Injector injector = Guice.createInjector(new JpaPersistModule("STAND_ALONE_TEST_STOCKS"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();
        return injector;
    }
}
