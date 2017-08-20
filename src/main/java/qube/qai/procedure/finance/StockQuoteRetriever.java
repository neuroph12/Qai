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

import org.apache.commons.lang3.StringUtils;
import org.ojalgo.finance.data.YahooSymbol;
import org.ojalgo.type.CalendarDateUnit;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by rainbird on 11/19/15.
 */
public class StockQuoteRetriever extends Procedure {

    public static String NAME = "Stock Quote Retriever Procedure";

    public static String DESCRIPTION = "Retrieves the stock quotes for given list of stock entities " +
            "and updates them to the latest stand";

    public static String TICKER_SYMBOL = "tickerSymbol";

    public static String NUMBER_OF_INSERTS = "numberOfInserts";

    public static long numberOfInserts;

    private String tickerSymbol;

    @Inject
    @Named("STOCKS")
    private EntityManager entityManager;

    public StockQuoteRetriever() {
        super(NAME);
    }

    @Override
    public void execute() {

        executeInputProcedures();

        // first get the selector
        if (StringUtils.isEmpty(tickerSymbol)) {
            tickerSymbol = (String) getInputValueOf(INPUT_TIME_SEQUENCE);
        }

        if (StringUtils.isEmpty(tickerSymbol)) {
            throw new RuntimeException("There has to be a ticker-symbol to update");
        }

        //entityManager.getTransaction().begin();

        StockEntity entity = retrieveEntityForTickerSymbol(tickerSymbol);
        if (entity == null) {
            error("An entity with tickerSymbol: '" + tickerSymbol + "' could not be found- skipping!");
        }

        Collection<StockQuote> quotes = retrieveQuotesFor(tickerSymbol);
        Set<StockQuote> entityQuotes = entity.getQuotes();
        for (StockQuote quote : quotes) {
            if (!entityQuotes.contains(quote)) {
                entityManager.persist(quote);
                entity.addQuote(quote);
                entityManager.persist(quote);
                numberOfInserts++;
            }
        }

        entityManager.persist(entity);

        setResultValueOf(NUMBER_OF_INSERTS, numberOfInserts);
    }

    private StockEntity retrieveEntityForTickerSymbol(String tickerSymbol) {

        String searchString = "select o from StockEntity o where o.tickerSymbol like '" + tickerSymbol + "'";
        Query query = entityManager.createQuery(searchString);
        StockEntity entity = (StockEntity) query.getSingleResult();

        return entity;
    }

    private Collection<StockQuote> retrieveQuotesFor(String stockName) {

        Collection<StockQuote> quotes = new ArrayList<StockQuote>();
        YahooSymbol symbol = new YahooSymbol(stockName);

        try {
            for (YahooSymbol.Data data : symbol.getHistoricalPrices()) {
                Date date = new Date(data.getKey().toTimeInMillis(CalendarDateUnit.DAY));
                StockQuote quote = new StockQuote();
                quote.setTickerSymbol(symbol.getSymbol());
                quote.setQuoteDate(date);
                quote.setAdjustedClose(data.adjustedClose);
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
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<String>(TICKER_SYMBOL) {
            @Override
            public void setValue(String value) {
                super.setValue(value);
                tickerSymbol = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Number>(NUMBER_OF_INSERTS, MIMETYPE_NUMBER) {
            @Override
            public Number getValue() {
                return numberOfInserts;
            }
        });
    }

    public static long getNumberOfInserts() {
        return numberOfInserts;
    }

    public static void setNumberOfInserts(long numberOfInserts) {
        StockQuoteRetriever.numberOfInserts = numberOfInserts;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
