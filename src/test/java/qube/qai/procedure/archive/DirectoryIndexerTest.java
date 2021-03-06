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

package qube.qai.procedure.archive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;

/**
 * Created by rainbird on 12/25/15.
 */
public class DirectoryIndexerTest extends QaiTestBase {

    private Logger logger = LoggerFactory.getLogger("DirectoryIndexerTest");

    private static String wikipediaResourceDirectory = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.resources";
    private static String wikipediaResourceIndexDirectory = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.resources.index";

    private static String wiktionaryResourceDirectory = "/media/rainbird/GIMEL/wiki-archives/wiktionary_en.resources";
    private static String wiktionaryResourceIndexDirectory = "/media/rainbird/GIMEL/wiki-archives/wiktionary_en.resources.index";

    private static String testResourceDirectory = "test/dummy.resources";
    private static String testResourceIndexDirectory = "test/dummy.resources.index";

    private String[] wikipediaFilesToSearch = {"€2_commemorative_coin_Greece_2007_TOR.jpg",
            "3D_Hot_Rally_boxart.PNG",
            "1_on_1.jpg",
            "5Five-regular.jpg",
            "3_Group_badge.jpg",
            "3-D_Docking_Mission.png",
            "50_Foot_Wave_-_50_Foot_Wave_EP.jpg",
            "50_Years_of_Comparative_Wealth_E.P._cover.jpg",
            "8th_Weapons_Squadron.jpg",
            "Whutcha_Want.jpg"};

    private String[] wiktionaryFilesToSearch = {"YR's_scripts.PNG",
            "Wiktionary-favicon-en-colored.png",
            "ipa-rendering-ff.png",
            "Wiktionary-favicon-en.png",
            "Writing_star.svg"};


    public void restDirectoryIndexer() throws Exception {

        DirectoryIndexer indexer = new DirectoryIndexer(testResourceDirectory, testResourceIndexDirectory);
        indexer.execute();

//        String indexDirectory = (String) indexer.getArguments().getResult(DirectoryIndexer.INDEX_DIRECTORY);
//        assertNotNull(indexDirectory);
//        assertTrue(testResourceIndexDirectory.equals(indexDirectory));
//
//        File baseDirectory = new File(testResourceDirectory);
//        if (!baseDirectory.exists() || !baseDirectory.isDirectory()) {
//            fail("has to be a directory man... and has to exist as well");
//        }
//
//        String[] filenames = baseDirectory.list();
//        DirectorySearchService searchService = new DirectorySearchService(testResourceIndexDirectory);
//        for (String searchFor : filenames) {
//            logger.info("searching for: " + searchFor);
//            Collection<SearchResult> results = searchService.searchInputString(searchFor, "file", 100);
//            assertNotNull("there has to be something", results);
//            assertTrue(searchFor + " must be there!!!", !results.isEmpty());
//            for (SearchResult result : results) {
//                logger.info("found: " + result.getUuid());
//            }
//        }
    }

    /**
     * this does the actual indexing- not really a test
     *
     * @throws Exception
     */
    public void restWikipediaResource() throws Exception {

        DirectoryIndexer indexer = new DirectoryIndexer(wikipediaResourceDirectory, wikipediaResourceIndexDirectory);
        indexer.execute();

//        String indexDirectory = (String) indexer.getArguments().getResult(DirectoryIndexer.INDEX_DIRECTORY);
//        assertNotNull(indexDirectory);
//        assertTrue(wikipediaResourceIndexDirectory.equals(indexDirectory));
//
//        DirectorySearchService searchService = new DirectorySearchService(wikipediaResourceIndexDirectory);
//        for (String searchFor : wikipediaFilesToSearch) {
//            logger.info("searching for: " + searchFor);
//            Collection<SearchResult> results = searchService.searchInputString(searchFor, "file", 100);
//            assertNotNull("there has to be something", results);
////            assertTrue(searchFor + " must be there!!!", !results.isEmpty());
//            for (SearchResult result : results) {
//                logger.info("found: " + result.getUuid());
//            }
//        }
    }

    /**
     * this does the actual indexing- not really a test
     *
     * @throws Exception
     */
    public void testWikionaryResource() throws Exception {

//        DirectoryIndexer indexer = new DirectoryIndexer(wiktionaryResourceDirectory, wiktionaryResourceIndexDirectory);
//        indexer.execute();
//
//        String indexDirectory = (String) indexer.getArguments().getResult(DirectoryIndexer.INDEX_DIRECTORY);
//        assertNotNull(indexDirectory);
//        assertTrue(wiktionaryResourceIndexDirectory.equals(indexDirectory));
//
//        DirectorySearchService searchService = new DirectorySearchService(wiktionaryResourceIndexDirectory);
//        for (String searchFor : wiktionaryFilesToSearch) {
//            logger.info("searching for: " + searchFor);
//            Collection<SearchResult> results = searchService.searchInputString(searchFor, "file", 100);
//            assertNotNull("there has to be something", results);
////            assertTrue(searchFor + " must be there!!!", !results.isEmpty());
//            for (SearchResult result : results) {
//                logger.info("found: " + result.getUuid());
//            }
//        }
    }
}
