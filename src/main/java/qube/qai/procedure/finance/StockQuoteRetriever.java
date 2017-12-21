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

import org.ojalgo.finance.data.GoogleSymbol;
import org.ojalgo.type.CalendarDateUnit;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by rainbird on 11/19/15.
 */
public class StockQuoteRetriever extends Procedure {

    public static String NAME = "Stock Quote Retriever Procedure";

    public static String DESCRIPTION = "Retrieves the stock quotes for given entity " +
            "and updates them to the latest stand";

    public long numberOfInserts = 0;

    private QaiDataProvider<StockEntity> entityProvider;

    public StockQuoteRetriever() {
        super(NAME);
    }

    @Override
    public void execute() {

        // first get the selector
        if (entityProvider == null) {
            entityProvider = (QaiDataProvider<StockEntity>) getInputValueOf(STOCK_ENTITY);
        }

        if (entityProvider == null) {
            throw new RuntimeException("There has to be a entityProvider to update");
        }

        //entityManager.getTransaction().begin();

        StockEntity entity = entityProvider.getData();
        if (entity == null) {
            error("An entity with not be found- skipping!");
            throw new RuntimeException("An entity could not be found- skipping!");
        }

        Collection<StockQuote> quotes = retrieveQuotesFor(entity.getTickerSymbol());
        Set<StockQuote> entityQuotes = entity.getQuotes();
        for (StockQuote quote : quotes) {
            if (!entityQuotes.contains(quote)) {
                entity.addQuote(quote);
                numberOfInserts++;
            }
        }

        if (numberOfInserts > 0) {
            entityProvider.putData(entity.getUuid(), entity);
        }

        setResultValueOf(NUMBER_OF_INSERTS, numberOfInserts);

        String tmpl = "Fetched: %d new entries for ticker-symbol '%s'";
        info(String.format(tmpl, numberOfInserts, entity.getTickerSymbol()));

    }

    private Collection<StockQuote> retrieveQuotesFor(String stockName) {

        Collection<StockQuote> quotes = new ArrayList<StockQuote>();
        GoogleSymbol symbol = new GoogleSymbol(stockName);

        try {
            for (GoogleSymbol.Data data : symbol.getHistoricalPrices()) {
                Date date = new Date(data.getKey().toTimeInMillis(CalendarDateUnit.DAY));
                StockQuote quote = new StockQuote();
                quote.setTickerSymbol(symbol.getSymbol());
                quote.setQuoteDate(date);
                //quote.setAdjustedClose(data.adjustedClose);
                quote.setAdjustedClose(data.close);
                quote.setClose(data.close);
                quote.setHigh(data.high);
                quote.setLow(data.low);
                quote.setOpen(data.open);
                quote.setVolume(data.volume);
                quotes.add(quote);
            }
        } catch (Exception e) {
            error("Ticker symbol: '" + stockName + "' does not exist", e);
        } finally {
            return quotes;
        }
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<QaiDataProvider<StockEntity>>(STOCK_ENTITY) {
            @Override
            public void setValue(QaiDataProvider<StockEntity> value) {
                super.setValue(value);
                entityProvider = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Number>(NUMBER_OF_INSERTS, MIMETYPE_NUMBER) {
            @Override
            public Number getValue() {
                return numberOfInserts;
            }
        });
    }

    public long getNumberOfInserts() {
        return numberOfInserts;
    }

    public QaiDataProvider<StockEntity> getEntityProvider() {
        return entityProvider;
    }

    public void setEntityProvider(QaiDataProvider<StockEntity> entityProvider) {
        this.entityProvider = entityProvider;
    }
}
