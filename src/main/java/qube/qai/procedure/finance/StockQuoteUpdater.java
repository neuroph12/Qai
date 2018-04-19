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

import com.hazelcast.core.HazelcastInstance;
import org.ojalgo.finance.data.YahooSymbol;
import org.ojalgo.type.CalendarDateUnit;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.services.QaiInjectorService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by zenpunk on 11/19/15.
 */
public class StockQuoteUpdater extends Procedure {

    public String NAME = "Stock-quote Updater";

    public String DESCRIPTION = "Drop the stock-quotes to be updated in the selection tab, " +
            "the procedure will retrieve the stock-quotes to the latest stand";

    public long numberOfInserts = 0;

    private Collection<StockQuote> quotes;

    @Inject
    private HazelcastInstance hazelcastInstance;

    public StockQuoteUpdater() {
        super("Stock-quote Updater");
    }

    @Override
    public void execute() {

        if (getInputs() == null || getInputs().isEmpty()) {
            info("No inputs to work on, terminating process");
            return;
        }

        for (QaiDataProvider<StockEntity> entityProvider : getInputs()) {
            QaiInjectorService.getInstance().injectMembers(entityProvider);
            StockEntity entity = entityProvider.getData();
            if (entity == null) {
                error("An entity with not be found- skipping!");
                throw new RuntimeException("An entity could not be found- skipping!");
            }

            Collection<StockQuote> quotes = retrieveQuotesFor(entity.getTickerSymbol().trim());
            // reload the entity so that its quotes too are at latest form in dataase
            //IMap<String, StockEntity> entityMap = hazelcastInstance.getMap(STOCK_ENTITY);
            //entity = entityMap.get(entity.getUuid());
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

            String tmpl = "Fetched: %d new entries for ticker-symbol '%s'";
            info(String.format(tmpl, numberOfInserts, entity.getTickerSymbol()));
        }

        hasExecuted = true;

    }

    @Override
    public Procedure createInstance() {
        return new StockQuoteUpdater();
    }

    private Collection<StockQuote> retrieveQuotesFor(String tickerSymbol) {

        Collection<StockQuote> quotes = new ArrayList<StockQuote>();
        //GoogleSymbol symbol = new GoogleSymbol(stockName);
        YahooSymbol symbol = new YahooSymbol(tickerSymbol);

        try {
            if (symbol == null || symbol.getHistoricalPrices() == null) {
                error("Ticker symbol: '" + tickerSymbol + "' could not be found");
                return quotes;
            }
            //for (GoogleSymbol.Data data : symbol.getHistoricalPrices()) {
            for (YahooSymbol.Data data : symbol.getHistoricalPrices()) {
                Date date = new Date(data.getKey().toTimeInMillis(CalendarDateUnit.DAY));
                StockQuote quote = new StockQuote();
                quote.setTickerSymbol(symbol.getSymbol());
                quote.setQuoteDate(date);
                quote.setAdjustedClose(data.adjustedClose);
                //quote.setAdjustedClose(data.close);
                quote.setClose(data.close);
                quote.setHigh(data.high);
                quote.setLow(data.low);
                quote.setOpen(data.open);
                quote.setVolume(data.volume);
                quotes.add(quote);
            }
        } catch (Exception e) {
            error("Ticker symbol: '" + tickerSymbol + "' does not exist", e);
        } finally {
            return quotes;
        }
    }

    @Override
    public void buildArguments() {

    }

    public Collection<StockQuote> getQuotes() {
        return quotes;
    }

    public long getNumberOfInserts() {
        return numberOfInserts;
    }

}
