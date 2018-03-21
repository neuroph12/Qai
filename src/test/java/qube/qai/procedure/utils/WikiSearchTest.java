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

package qube.qai.procedure.utils;

import qube.qai.main.QaiTestBase;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

/**
 * Created by rainbird on 11/11/15.
 */
public class WikiSearchTest extends QaiTestBase {

    @Inject
    @Named("Wikikipedia_en")
    private SearchServiceInterface wikiSearchService;

    @Inject
    @Named("Wiktionary_en")
    private SearchServiceInterface wiktionarySearchService;


    /**
     * @throws Exception
     * @TODO this test needs improvement- start with injecting the constants
     */
    public void testWikiSearch() throws Exception {

        // do some searching and display results...
        String[] searchList = {"test", "mouse", "silly"};
        for (String search : searchList) {
            Collection<SearchResult> results = wikiSearchService.searchInputString(search, "title", 100);
            assertTrue("no results", results != null && !results.isEmpty());
            for (SearchResult result : results) {
                logger.info("searching: '" + search + "' resulted: '" + result.getContext() + "' with " + result.getRelevance() + "% relevance");
            }
        }

    }

    public void testWiktionarySearch() throws Exception {

        // do some searching and display results...
        String[] searchList = {"test", "mouse", "silly"};
        for (String search : searchList) {
            Collection<SearchResult> results = wiktionarySearchService.searchInputString(search, "title", 100);
            assertTrue("no results", results != null && !results.isEmpty());
            for (SearchResult result : results) {
                logger.info("searching: '" + search + "' resulted: '" + result.getContext() + "' with " + result.getRelevance() + "% relevance");
            }
        }

    }
}
