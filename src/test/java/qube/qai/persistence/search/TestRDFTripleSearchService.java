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

package qube.qai.persistence.search;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 6/1/16.
 */
public class TestRDFTripleSearchService extends TestCase {

    private boolean debug = true;

    private String[][] rdfids = {{"http://dbpedia.org/resource/Aristotle", "region"},
            {"http://dbpedia.org/resource/Autism", "diseasesdb"},
            {"http://dbpedia.org/resource/Autism", "emedicineTopic"},
            {"http://dbpedia.org/resource/Aristotle", "deathYear"}};

    public void restSearchService() throws Exception {

        // begin with creating the search service
        Injector injector = Guice.createInjector(new JpaPersistModule("DBPEDIA"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        RDFTriplesSearchService searchService = new RDFTriplesSearchService();
        injector.injectMembers(searchService);

        // now we can proceed with the proper testing
        for (int i = 0; i < rdfids.length; i++) {
            String term = rdfids[i][0] + "|" + rdfids[i][1];
            Collection<SearchResult> results = searchService.searchInputString(term, null, 100);
            assertNotNull(results);
            assertTrue(!results.isEmpty());
            for (SearchResult result : results) {
                log("found uuid: " + result.getUuid());
            }
        }

    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
