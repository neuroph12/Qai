package qube.qai.data.stores;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 11/25/15.
 */
public class TestDataStore extends TestCase {

    private static Logger logger = LoggerFactory.getLogger("TestDataStore");

    public void testDataStoreWithHazelcastInstance() throws Exception {

        // @TODO the test is not implemented
        // i am guessing this is in order to tes the stock-quote data-store...
        logger.info("tests are not yet implemented");
        DataStore dataStore = new StockQuoteDataStore();
        assertNotNull("this can simply not happen", dataStore);
        fail("tests are not yet implemented");
    }


}
