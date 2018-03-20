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

package qube.qai.services.implementation;

import junit.framework.TestCase;

import java.util.Collection;

public class WikiSearchServiceTest extends TestCase {

    private String WIKIPEDIA_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";

    private String WIKIPEDIA_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    private String WIKTIONARY_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";

    private String WIKTIONARY_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";

    private String[] searchFor = {"mouse", "test", "experiment"};

    public void testWikipediaSearchService() throws Exception {

        WikiSearchService wikiSearch = new WikiSearchService("Wikipedia", WIKIPEDIA_DIRECTORY, WIKIPEDIA_ARCHIVE);
        wikiSearch.initialize();
        assertTrue("something has gone wrong with initialization", wikiSearch.isInitialized());

        for (int i = 0; i < searchFor.length; i++) {
            Collection<SearchResult> results = wikiSearch.searchInputString(searchFor[i], "title", 100);
            assertNotNull("results may not be null", results);
            assertTrue("there has to be some results", !results.isEmpty());

            log("wiki results for: " + searchFor[i]);
            for (SearchResult result : results) {
                log(result.getTitle());
            }
        }

    }

    public void testWiktionarySearchService() throws Exception {

        WikiSearchService wikiSearch = new WikiSearchService("Wiktionary", WIKTIONARY_DIRECTORY, WIKTIONARY_ARCHIVE);
        wikiSearch.initialize();
        assertTrue("something has gone wrong with initialization", wikiSearch.isInitialized());

        for (int i = 0; i < searchFor.length; i++) {
            Collection<SearchResult> results = wikiSearch.searchInputString(searchFor[i], "title", 100);
            assertNotNull("results may not be null", results);
            assertTrue("there has to be some results", !results.isEmpty());

            log("wiki results for: " + searchFor[i]);
            for (SearchResult result : results) {
                log(result.getTitle());
            }
        }

    }

    private void log(String message) {
        System.out.println(message);
    }

}
