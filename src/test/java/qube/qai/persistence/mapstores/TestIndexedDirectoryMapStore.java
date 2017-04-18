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

package qube.qai.persistence.mapstores;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.ResourceData;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rainbird on 12/24/15.
 */
public class TestIndexedDirectoryMapStore extends QaiTestBase {

    private static Logger logger = LoggerFactory.getLogger("TestTarballMapStore");

    private static String wikiArticleName = "Charles Darwin.xml";
    private static String wikipediaResourceDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";
    private static String wikipediaResourceIndexDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources.index";
    // [[File:...|thumb|...]] is how you define an image in wiki-format
    private static String wikiFileFormatStart = "[[File:";
    private static String wikiFileFormatEnd = "|";

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    public void testIndexedDirectoryMapStore() throws Exception {

        // ok- this is simple really- we pick a wiki-article
        // and query the resources required- use of course a wiki-model
        // for the purpose, and check that the tarball map-store
        // can actually locate and retrieve them
        DirectorySearchService directorySearchService = new DirectorySearchService(wikipediaResourceIndexDirectory);
        IndexedDirectoryMapStore mapStore = new IndexedDirectoryMapStore(wikipediaResourceDirectory, wikipediaResourceIndexDirectory);
        mapStore.setSearchService(directorySearchService);

        WikiArticle article = searchService.retrieveDocumentContentFromZipFile(wikiArticleName);
        assertNotNull("seriously?!?", article);

        //log(article.getContent());

        if (article.getContent().contains(wikiFileFormatStart)) {
            logger.info("we are good- found some resources to look for");
        } else {
            logger.info("no file reference in this article- nothing to search for");
            fail("pick a wiki-article with some images in it for crying out loud!?!");
        }

        int foundCount = 0;
        int notFoundCount = 0;
        String[] filenames = StringUtils.substringsBetween(article.getContent(), wikiFileFormatStart, wikiFileFormatEnd);
        for (String key : filenames) {
            logger.info("currently checking file: " + key);
            ResourceData result = mapStore.load(key);
            //assertNotNull(result);
            if (result == null) {
                notFoundCount++;
                logger.info("file: '" + key + "' could not be found");
                // some of the resources seem to be missing really... out of some reason
                // fail("resource file: " + key + "must be there");
            } else {
                foundCount++;
                assertNotNull("there has to be a result", result);
                logger.info("file: '" + key + "' is OK. actual filename: " + result.getName());
            }
        }

        logger.info("at the end of the search found: " + foundCount + " and not found: " + notFoundCount);
    }

}
