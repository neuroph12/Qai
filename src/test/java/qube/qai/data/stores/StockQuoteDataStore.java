package qube.qai.data.stores;

import org.ojalgo.finance.data.YahooSymbol;
import org.ojalgo.type.CalendarDateUnit;
import qube.qai.data.DataStore;
import qube.qai.persistence.StockQuote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
            StockQuote quote = new StockQuote(quoteName,
                    data.adjustedClose,
                    data.close,
                    data.high,
                    data.low,
                    data.open,
                    data.volume);
            Date date = new Date(data.getKey().toTimeInMillis(CalendarDateUnit.DAY));
            quote.setDate(date);
            quotes.add(quote);
        }

        return quotes;
    }
}
