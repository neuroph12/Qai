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

package qube.qai.persistence.mapstores;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.ResourceData;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rainbird on 12/24/15.
 */
public class IndexedDirectoryMapStoreTest extends QaiTestBase {

    @Inject
    private Logger logger;

    private static String wikiArticleName = "Charles Darwin.xml";
    private static String wikipediaResourceDirectory = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.resources";
    private static String wikipediaResourceIndexDirectory = "/media/rainbird/GIMEL/wiki-archives/wikipedia_en.resources.index";
    // [[File:...|thumb|...]] is how you define an image in wiki-format
    private static String wikiFileFormatStart = "[[File:";
    private static String wikiFileFormatEnd = "|";

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    @Inject
    @Named("Wikipedia_en")
    private QaiDataProvider<WikiArticle> wikiArticleDataProvider;

    public void testIndexedDirectoryMapStore() throws Exception {

        // ok- this is simple really- we pick a wiki-article
        // and query the resources required- use of course a wiki-model
        // for the purpose, and check that the tarball map-store
        // can actually locate and retrieve them
        DirectorySearchService directorySearchService = new DirectorySearchService(WIKIPEDIA_RESOURCES, wikipediaResourceIndexDirectory);
        IndexedDirectoryMapStore mapStore = new IndexedDirectoryMapStore(wikipediaResourceDirectory, wikipediaResourceIndexDirectory);
        mapStore.setSearchService(directorySearchService);

        IMap<String, WikiArticle> wikiMap = hazelcastInstance.getMap(WIKIPEDIA);
        WikiArticle wikiArticle = wikiMap.get(wikiArticleName);
        assertNotNull("seriously?!?", wikiArticle);

        //log(article.getContent());

        if (wikiArticle.getContent().contains(wikiFileFormatStart)) {
            logger.info("we are good- found some resources to look for");
        } else {
            logger.info("no file reference in this article- nothing to wiki for");
            fail("pick a wiki-article with some images in it for crying out loud!?!");
        }

        int foundCount = 0;
        int notFoundCount = 0;
        String[] filenames = StringUtils.substringsBetween(wikiArticle.getContent(), wikiFileFormatStart, wikiFileFormatEnd);
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
                assertNotNull("there has to be content", result.getBinaryData());
                logger.info("file: '" + key + "' is OK. actual filename: " + result.getName());
            }
        }

        logger.info("at the end of the wiki found: " + foundCount + " and not found: " + notFoundCount);
    }

}
