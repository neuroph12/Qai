package qube.qai.data.stores;

import org.ojalgo.finance.data.YahooSymbol;
import qube.qai.data.DataStore;
import qube.qai.persistence.StockQuote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by rainbird on 11/25/15.
 */
public class StockQuoteDataStore implements DataStore {

    private HashMap<String, YahooSymbol> provided;

    public StockQuoteDataStore() {
        this.provided = new HashMap<String, YahooSymbol>();
    }

    public Collection<StockQuote> retrieveQuotesFor(String quoteName) {
        YahooSymbol symbol;
        if (provided.containsKey(quoteName)) {
            symbol = provided.get(quoteName);
        } else {
            symbol = new YahooSymbol(quoteName);
            provided.put(quoteName, symbol);
        }

        Collection<StockQuote> quotes = new ArrayList<StockQuote>();
        for (YahooSymbol.Data data : symbol.getHistoricalPrices()) {
            StockQuote quote = new StockQuote(data.adjustedClose, data.close, data.high, data.low, data.open, data.volume);
            quotes.add(quote);
        }

        return quotes;
    }
}
