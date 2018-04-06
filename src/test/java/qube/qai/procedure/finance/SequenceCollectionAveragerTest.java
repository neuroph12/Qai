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

package qube.qai.procedure.finance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.procedure.utils.SelectForAll;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SequenceCollectionAveragerTest extends QaiTestBase {

    @Inject
    private Logger logger;

    @Inject
    HazelcastInstance hazelcastInstance;

    public void testSequenceAverager() throws Exception {

        SelectForAll select = new SelectForAll();

        ProcedureTemplate<SequenceCollectionAverager> template = new ProcedureTemplate<SequenceCollectionAverager>() {
            @Override
            public SequenceCollectionAverager createProcedure() {

                SequenceCollectionAverager averager = new SequenceCollectionAverager();

                return averager;
            }

            @Override
            public String getProcedureName() {
                return "Stock-quote updater";
            }

            @Override
            public String getProcedureDescription() {
                return "update the quotes for given procedures";
            }
        };

        select.setTemplate(template);

        Collection<SearchResult> searchResults = pickStocksToUpdate(10);

        assertNotNull("there has to be search results", searchResults);
        assertTrue("there has to be some search results", searchResults.isEmpty());

        StringBuffer stockNames = new StringBuffer();
        for (SearchResult result : searchResults) {
            stockNames.append(result.getTitle());
            stockNames.append(" ");
        }

        // set the input search-result to the procedure
        select.setResults(searchResults);

        // now we're ready to start the procedure
        select.execute();

        assertNotNull("there has to be spawned procedures", select.getSpawn());
        assertTrue("there has to be one spawned procedure", select.getSpawn().size() == 1);

        logger.info("started execution of procedure with: " + stockNames.toString());
    }

    protected Collection<SearchResult> pickStocksToUpdate(int numberToPick) {

        Collection<SearchResult> searchResults = new ArrayList<>();

        IMap<String, StockGroup> stockGroupMap = hazelcastInstance.getMap(STOCK_GROUPS);

        for (String key : stockGroupMap.keySet()) {
            StockGroup group = stockGroupMap.get(key);
            Collection<StockEntity> entities = group.getEntities();
            if (entities == null || entities.isEmpty()) {
                continue;
            }
            Iterator<StockEntity> iterator = entities.iterator();
            for (int i = 0; i < numberToPick; i++) {
                StockEntity entity = iterator.next();
                SearchResult searchResult = new SearchResult(STOCK_ENTITIES, entity.getName(), entity.getUuid(), "", 1.0);
                searchResults.add(searchResult);
            }
            break;
        }

        return searchResults;
    }

}
