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
    private String dummy = "QUAPIL";

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

        // this one does not exist check that the return value is empty
        Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(dummy);
        assertNotNull("we expect an empty array", quotes);
        assertTrue("we expect an empty array", quotes.isEmpty());
    }


}
