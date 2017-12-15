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

package qube.qai.services.sims;

import qube.qai.main.QaiTestBase;
import qube.qai.services.implementation.SearchResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 1/12/16.
 */
public class HowFairAreMarketsDistributedTest extends QaiTestBase {

    /**
     * if we were to write the procedure we want to test in our grammar form
     * <p>
     * marketNetwork := (for-each network-builder epoch)
     * <p>
     * epoch := (slice (change-point-analysis (select "average"
     * (statistical-analysis (for-each fetch-quotes-for
     * (for-each find-entities-of (search-results)))))
     * <p>
     * // these are then the results which we want to see when done.
     * (for-each entropy-analysis (for-each forward-propagation marketNetwork)
     *
     * @throws Exception
     */
    public void testHowFairAreMarkets() throws Exception {

        // this is the beginning point of the proposed procedure
        Collection<SearchResult> results = getSearchResults();

        MarketBuilderSim sim = new MarketBuilderSim(results);
        injector.injectMembers(sim);

        // and go ahead and check hte results.

    }

    private Collection<SearchResult> getSearchResults() {
        Collection<SearchResult> results = new ArrayList<>();
        return results;
    }

}
