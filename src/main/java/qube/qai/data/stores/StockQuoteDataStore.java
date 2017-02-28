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

package qube.qai.data.stores;

import org.ojalgo.finance.data.YahooSymbol;
import org.ojalgo.type.CalendarDateUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.StockQuote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by rainbird on 11/25/15.
 */
public class StockQuoteDataStore implements DataStore {

    private Logger logger = LoggerFactory.getLogger("StockQuoteDataStore");

    private HashMap<String, YahooSymbol> provided;

    public StockQuoteDataStore() {
        this.provided = new HashMap<String, YahooSymbol>();
    }

    public Collection<StockQuote> retrieveQuotesFor(String quoteName) {

        Collection<StockQuote> quotes = new ArrayList<StockQuote>();
        YahooSymbol symbol;
        if (provided.containsKey(quoteName)) {
            symbol = provided.get(quoteName);
        } else {
            symbol = new YahooSymbol(quoteName);
            provided.put(quoteName, symbol);
        }

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
            logger.error("Ticker symbol: '" + quoteName + "' does not exist", e);
        } finally {
            return quotes;
        }
    }

    public boolean isProvided(String tickerSymbol) {
        return provided.containsKey(tickerSymbol);
    }

}
