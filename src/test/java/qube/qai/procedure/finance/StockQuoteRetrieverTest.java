/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

import junit.framework.TestCase;
import qube.qai.persistence.DummyQaiDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.ProcedureLibrary;

/**
 * Created by rainbird on 3/11/17.
 */
public class StockQuoteRetrieverTest extends TestCase {


    public void testStockQuoteRetriever() throws Exception {

        //Injector injector = createInjector();
        //EntityManager entityManager = injector.getInstance(EntityManager.class);
        StockQuoteRetriever retriever = ProcedureLibrary.stockQuoteRetriverTemplate.createProcedure();
        String tickerSymbol = "GOOG";
        StockEntity entity = new StockEntity();
        entity.setTickerSymbol(tickerSymbol);
        QaiDataProvider<StockEntity> provider = new DummyQaiDataProvider<StockEntity>(entity);
        retriever.setEntityProvider(provider);

        retriever.execute();

        for (StockQuote quote : entity.getQuotes()) {
            log(quote.getTickerSymbol() + " $" + quote.getAdjustedClose() + " on: " + quote.getQuoteDate());
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

}
