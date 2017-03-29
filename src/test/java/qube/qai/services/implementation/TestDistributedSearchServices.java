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

package qube.qai.services.implementation;

import qube.qai.main.QaiTestBase;

/**
 * Created by rainbird on 1/6/16.
 */
public class TestDistributedSearchServices extends QaiTestBase {

//    @Inject @Named("Wikipedia_en")
//    SearchServiceInterface wikiSearchService;
//
//    @Inject @Named("Stock_Quotes")
//    SearchServiceInterface quotesSearchService;
//
//    @Inject @Named("Dbpedia_en")
//    SearchServiceInterface dbpediaSearchService;

    public void testDistributedUserSearch() throws Exception {

        fail("there is something wrong with this test- take a look later... now running all tests.");

    }

    public void testDistributedWikiSearch() throws Exception {

        fail("there is something wrong with this test- take a look later... now running all tests.");
        // make an instance of the thing
//        DistributedSearchListener searchListener = new DistributedSearchListener("Wikipedia_en");
//        searchListener.setSearchService(wikiSearchService);
//        injector.injectMembers(searchListener);
//
//        searchListener.initialize();
//
//
//        String topicName = "Wikipedia_en";
//        DistributedSearchService distributedSearch = new DistributedSearchService(topicName);
//        injector.injectMembers(distributedSearch);
//        distributedSearch.initialize();
//
//        Collection<SearchResult> results = distributedSearch.searchInputString("mouse", "title", 100);
//        assertNotNull("have to return something", results);
//        assertTrue("has to be something in there as well", !results.isEmpty());
//        for (SearchResult result : results) {
//            logger.info("found result: " + result.getContext());
//        }
    }

    public void testDistributedStockQuoteSearch() throws Exception {

        fail("there is something wrong with this test- take a look later... now running all tests.");

        // make an instance of the thing
//        DistributedSearchListener searchListener = new DistributedSearchListener("Stock_Quotes");
//        searchListener.setSearchService(quotesSearchService);
//        injector.injectMembers(searchListener);
//
//        searchListener.initialize();
//
//        String topicName = "Stock_Quotes";
//        DistributedSearchService distributedSearch = new DistributedSearchService(topicName);
//        injector.injectMembers(distributedSearch);
//        distributedSearch.initialize();
//
//        Collection<SearchResult> results = distributedSearch.searchInputString("HRS", "TICKERSYMBOL", 100);
//        assertNotNull("have to return something", results);
//        assertTrue("has to be something in there as well", !results.isEmpty());
//        for (SearchResult result : results) {
//            logger.info("found result: " + result.getContext());
//        }
    }

    public void testDistributedRdfTripleSearch() throws Exception {

        fail("there is something wrong with this test- take a look later... now running all tests.");

        // make an instance of the thing
//        DistributedSearchListener searchListener = new DistributedSearchListener("Dbpedia_en");
//        searchListener.setSearchService(dbpediaSearchService);
//        injector.injectMembers(searchListener);
//
//        searchListener.initialize();
//
//        String topicName = "Dbpedia_en";
//        DistributedSearchService distributedSearch = new DistributedSearchService(topicName);
//        injector.injectMembers(distributedSearch);
//        distributedSearch.initialize();
//
//        Collection<SearchResult> results = distributedSearch.searchInputString("Aristotle", "OBJECT", 100);
//        assertNotNull("have to return something", results);
//        assertTrue("has to be something in there as well", !results.isEmpty());
//        for (SearchResult result : results) {
//            logger.info("found result: " + result.getContext());
//        }
    }
}
