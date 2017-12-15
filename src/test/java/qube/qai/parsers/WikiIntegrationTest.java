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

package qube.qai.parsers;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 12/24/15.
 */
public class WikiIntegrationTest extends QaiTestBase {

    private static Logger logger = LoggerFactory.getLogger("TestWikiArticleIntegration");

    private String stockListingPage = "Lists of companies by stock exchange listing.xml";

    private String SnP500Page = "List of S&P 500 companies.xml";

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    @Inject
    @Named("Wikipedia_en")
    private QaiDataProvider<WikiArticle> wikiArticleDataProvider;

    public void testStripTableData() throws Exception {

        WikiArticle snp500 = wikiArticleDataProvider.getData(SnP500Page);
        assertNotNull("we are here to play with this file", snp500);

        WikiIntegration wiki = new WikiIntegration();
        String html = wiki.wikiToHtml(snp500.getContent());
        String[][] data = wiki.stripTableData(html);
        assertNotNull("really?!?", data);
        assertTrue("don't believe everything you see", data.length > 0 && data[0].length > 0);

        for (int i = 0; i < data.length; i++) {
            log("currently reading" + data[i][0]);
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < data[i].length; j++) {
                buffer.append(data[i][j]);
                buffer.append("|"); // why not
            }
            log(buffer.toString());
        }

    }

    public void testStripHeader() {

        Set<String> titles = headerTitles();

        WikiArticle snp500 = wikiArticleDataProvider.getData(SnP500Page);
        assertNotNull("we are here to play with this file", snp500);

        WikiIntegration wiki = new WikiIntegration();
        String html = WikiIntegration.wikiToHtml(snp500.getContent());
        String[] header = wiki.stripHeader(html);
        assertNotNull("should not be null", header);

        for (String name : header) {
            logger.info("header title: " + name);
            assertTrue("has to be here somewhere", titles.contains(name));
        }
    }

    public void testLinkFinder() throws Exception {

        WikiArticle snp500 = wikiArticleDataProvider.getData(SnP500Page);
        assertNotNull("we are here to play with this file", snp500);

        Collection<String> links = WikiIntegration.getLinksOf(snp500);
        assertNotNull("this is after all what we are testing", links);
        assertTrue("there has to be many links in there", !links.isEmpty());
        for (String link : links) {
            logger.info("found link: " + link);
        }
    }

    /**
     * these are the header titles on S&P 500 page table
     *
     * @return
     */
    private Set<String> headerTitles() {
        String[] headerTitles = {"Ticker symbol", "Security",
                "SEC filings", "GICS", "GICS Sub Industry",
                "Address of Headquarters", "Date first added", "CIK"};

        Set<String> titles = new HashSet<String>();
        for (String title : headerTitles) {
            titles.add(title);
        }

        return titles;
    }

    /**
     * this time it is even right to have this
     * logger-lines are terrible for reading
     * and getting this test done requires a lot of reading
     *
     * @param message
     */
    @Override
    protected void log(String message) {
        System.out.println(message);
    }

}
