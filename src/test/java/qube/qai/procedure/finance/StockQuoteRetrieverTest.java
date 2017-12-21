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
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.utils.ForEach;
import qube.qai.services.ProcedureRunnerInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by rainbird on 3/11/17.
 */
public class StockQuoteRetrieverTest extends TestCase implements ProcedureConstants {


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

    public void testWithForEach() throws Exception {

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

        QaiDataProvider<Collection> dataProvider = new DummyQaiDataProvider<>(entities);

        ForEach forEach = ProcedureLibrary.forEachTemplate.createProcedure();
        forEach.setTemplate(ProcedureLibrary.stockQuoteRetriverTemplate);
        forEach.setTargetInputName(STOCK_ENTITY);
        forEach.setTargetCollectionProvider(dataProvider);

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
    }

    private void log(String message) {
        System.out.println(message);
    }

}
