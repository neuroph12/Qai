package qube.qai.data.stores;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.StockQuote;

import java.util.Collection;

/**
 * Created by rainbird on 11/25/15.
 */
public class TestStockQuoteDataStore extends TestCase {

    private static Logger logger = LoggerFactory.getLogger("TestDataStore");

    // HRS GGP DTV CI LMT TAP DO HON JCI AFL
    private String[] names = {"HRS", "GGP", "CI", "LMT", "TAP"};
    private String dummy = "DTV";

    public void testDataStore() throws Exception {

        StockQuoteDataStore dataStore = new StockQuoteDataStore();
        for (String name : names) {
            logger.info("collecting data for '" + name + "'");
            Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(name);
            assertNotNull("there has to be something", quotes);
            assertTrue("there has to be content as well", !quotes.isEmpty());
            StockQuote quote = (StockQuote) quotes.iterator().next();
            assertTrue("both ticker and exchange", quote.getTickerSymbol().equals(name));
        }

        // thisone does not exist check that the return value is empty
        Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(dummy);
        assertNotNull("we expect an emty array", quotes);
        assertNotNull("we expect an emty array", quotes.isEmpty());
    }


}
