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
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.utils.ForEach;
import qube.qai.services.ProcedureRunnerInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by rainbird on 3/11/17.
 */
public class StockQuoteUpdaterTest extends TestCase implements ProcedureConstants {


    public void testStockQuoteRetriever() throws Exception {

        //Injector injector = createInjector();
        //EntityManager entityManager = injector.getInstance(EntityManager.class);
        /*StockQuoteUpdater retriever = ProcedureLibrary.plainStockQuoteUpdater.createProcedure();
        String tickerSymbol = "GOOG";
        StockEntity entity = new StockEntity();
        entity.setTickerSymbol(tickerSymbol);
        QaiDataProvider<StockEntity> provider = new DataProvider<StockEntity>(entity);
        retriever.setEntityProvider(provider);

        retriever.execute();

        for (StockQuote quote : entity.getQuotes()) {
            log(quote.getTickerSymbol() + " $" + quote.getAdjustedClose() + " on: " + quote.getQuoteDate());
        }*/
    }

    public void testWithForEach() throws Exception {

        Collection<StockEntity> entities = createTestEntites();

        QaiDataProvider<Collection> dataProvider = new DataProvider<>(entities);

        ForEach forEach = new ForEach();
        //forEach.setTemplate(ProcedureLibrary.plainStockQuoteUpdater);
        forEach.setTargetInputName(STOCK_ENTITY);
        //forEach.setTargetCollectionProvider(dataProvider);

        ProcedureRunnerInterface dummyRunner = new ProcedureRunnerInterface() {
            @Override
            public void submitProcedure(Procedure procedure) {
                String messTmpl = "Procedure: '%s' has been submitted";
                log(String.format(messTmpl, procedure.getProcedureName()));
                procedure.execute();

            }

            @Override
            public ProcedureState queryState(String uuid) {
                return null;
            }

            @Override
            public Set<String> getStartedProcedures() {
                return null;
            }
        };

        forEach.setProcedureRunner(dummyRunner);

        forEach.execute();

        // this depends on hazelcast-instance
        //forEach.isChildrenExceuted();
    }

    private Collection<StockEntity> createTestEntites() {

        Collection<StockEntity> entities = new ArrayList<>();

        StockEntity goog = new StockEntity();
        goog.setTickerSymbol("GOOG");
        entities.add(goog);

        StockEntity aapl = new StockEntity();
        aapl.setTickerSymbol("AAPL");
        entities.add(aapl);

        StockEntity msft = new StockEntity();
        msft.setTickerSymbol("MSFT");
        entities.add(msft);

        return entities;
    }

    private void log(String message) {
        System.out.println(message);
    }

}
