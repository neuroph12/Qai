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
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureLibraryInterface;
import qube.qai.procedure.nodes.ProcedureDescription;
import qube.qai.procedure.utils.SelectForEach;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rainbird on 4/7/17.
 */
public class FinanceProceduresTest extends QaiTestBase {

    @Inject
    private Logger logger;

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureLibraryInterface procedureLibrary;

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    public void estStockEntityInitialization() throws Exception {

        StockEntityInitialization procedure = null; //procedureLibrary.stockEntityInitializationTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        //checkProcedureInputs(description);

        //checkProcedureResults(description);
    }

    public void testStockQuoteUpdaterRemote() throws Exception {

        int numberToPick = 10;

        SelectForEach procedure = ProcedureLibrary.stockQuoteUpdaterTemplate.createProcedure();

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

        assertNotNull("there has to be search results", searchResults);
        assertTrue("there has to be search results", !searchResults.isEmpty() && searchResults.size() == numberToPick);
        procedure.setResults(searchResults);

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        procedureRunner.submitProcedure(procedure);

    }

    public void testSequenceCollectionAverager() throws Exception {

        // @TODO implement the test
        fail("not yet implemented ");

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
