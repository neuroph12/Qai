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

import com.hazelcast.core.IMap;
import org.ojalgo.finance.data.YahooSymbol;
import org.ojalgo.type.CalendarDateUnit;
import qube.qai.data.Arguments;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.inject.Named;
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

    public static String TICKER_SYMBOLS = "tickerSymbols";

    public static String NUMBER_OF_INSERTS = "numberOfInserts";

    public static long numberOfInserts;

    private Collection<String> tickerSymbols;

    @Inject
    @Named("STOCKS")
    private SearchServiceInterface stockSearchService;

    public StockQuoteRetriever() {
        super(NAME);
    }

    @Override
    public void execute() {

        if (tickerSymbols == null || tickerSymbols.isEmpty()) {
            throw new RuntimeException("A list of ticker-symbols must be provided!");
        }

        for (String tickerSymbol : tickerSymbols) {
            Collection<StockQuote> quotes = retrieveQuotesFor(tickerSymbol);
            Collection<SearchResult> searchResults = stockSearchService.searchInputString(tickerSymbol, "STOCK_ENTITIES", 1);
            // there has to be a stock enttiy to the name
            if (searchResults.isEmpty()) {
                //throw new RuntimeException("Entity with ticker-symbol: '" + tickerSymbol + "' could not be found- cannot continue");
                logger.error("Entity with ticker-symbol: '" + tickerSymbol + "' could not be found- have to skip!");
                continue;
            }
            String entityUuid = searchResults.iterator().next().getUuid();
            IMap<String, StockEntity> entityMap = hazelcastInstance.getMap("STOCK_ENTITIES");
            IMap<String, StockQuote> quoteMap = hazelcastInstance.getMap("STOCK_QUOTES");
            StockEntity stockEntity = entityMap.get(entityUuid);
            Set<StockQuote> entityQuotes = stockEntity.getQuotes();
            for (StockQuote quote : quotes) {
                if (!entityQuotes.contains(quote)) {
                    // @TODO i am not sure which of these is the right choice- i am not assuming hazelcast can deal reasonably with child-collections
                    stockEntity.addQuote(quote);
                    quoteMap.put(quote.getUuid(), quote);
                    numberOfInserts++;
                }
            }
        }

        arguments.addResult(NUMBER_OF_INSERTS, numberOfInserts);

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
            logger.error("Ticker symbol: '" + stockName + "' does not exist", e);
        } finally {
            return quotes;
        }
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(TICKER_SYMBOLS);
        arguments.putResultNames(NUMBER_OF_INSERTS);
    }

    public static long getNumberOfInserts() {
        return numberOfInserts;
    }

    public static void setNumberOfInserts(long numberOfInserts) {
        StockQuoteRetriever.numberOfInserts = numberOfInserts;
    }

    public Collection<String> getTickerSymbols() {
        return tickerSymbols;
    }

    public void setTickerSymbols(Collection<String> tickerSymbols) {
        this.tickerSymbols = tickerSymbols;
    }

}
