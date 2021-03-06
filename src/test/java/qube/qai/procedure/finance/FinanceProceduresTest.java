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
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureLibraryInterface;
import qube.qai.procedure.analysis.ChangePoints;
import qube.qai.procedure.nodes.ProcedureDescription;
import qube.qai.procedure.utils.SelectForAll;
import qube.qai.procedure.utils.SelectForEach;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.QaiInjectorService;
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

    public void testChangePointAnalysisTemplate() throws Exception {

        int numberToPick = 1;
        ArrayList<SearchResult> searchResults = pickStocks(numberToPick);

        SelectForEach procedure = ProcedureLibrary.changePointAnalysisTemplate.createProcedure();
        procedure.setResults(searchResults);

        assertNotNull("there has to be search results", searchResults);
        assertTrue("there has to be search results", !searchResults.isEmpty() && searchResults.size() == numberToPick);
        procedure.setResults(searchResults);

        procedureRunner.submitProcedure(procedure);
    }

    public void testChangePointAnalysis() throws Exception {

        int numberToPick = 1;
        Collection<QaiDataProvider> providers = pickStockProviders(numberToPick);

        assertNotNull("there has to be search results", providers);
        assertTrue("there has to be search results", !providers.isEmpty() && providers.size() == numberToPick);

        ChangePoints changePoint = new ChangePoints();
        changePoint.setInputs(providers);
        QaiInjectorService.getInstance().injectMembers(changePoint);

        changePoint.execute();

        assertTrue("has to mark as executed", changePoint.hasExecuted());
        //assertTrue("there has to be some results", changePoint.getMarkers() != null && !changePoint.getMarkers().isEmpty());
    }

    public void testStockQuoteUpdaterTemplate() throws Exception {

        int numberToPick = 10;

        SelectForAll procedure = ProcedureLibrary.stockQuoteUpdaterTemplate.createProcedure();

        ArrayList<SearchResult> searchResults = pickStocks(numberToPick);

        assertNotNull("there has to be search results", searchResults);
        assertTrue("there has to be search results", !searchResults.isEmpty() && searchResults.size() == numberToPick);
        procedure.setResults(searchResults);

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        procedureRunner.submitProcedure(procedure);

    }

    public void testStockQuoteUpdater() throws Exception {

        int numberToPick = 10;

        Collection<QaiDataProvider> searchResults = pickStockProviders(numberToPick);

        assertNotNull("there has to be search results", searchResults);
        assertTrue("there has to be some search results", !searchResults.isEmpty() && searchResults.size() == numberToPick);

        StockQuoteUpdater updater = new StockQuoteUpdater();
        QaiInjectorService.getInstance().injectMembers(updater);
        updater.setInputs(searchResults);
        updater.execute();

        assertTrue("we have completed execution", updater.hasExecuted());
        assertTrue("there has to be some inserts", updater.numberOfInserts >= 0);
    }

    public void testAverageSequenceTemplate() throws Exception {

        int numberToPick = 10;

        SelectForAll procedure = ProcedureLibrary.averageSequenceTemplate.createProcedure();

        ArrayList<SearchResult> searchResults = pickStocks(numberToPick);

        assertNotNull("there has to be search results", searchResults);
        assertTrue("there has to be search results", !searchResults.isEmpty() && searchResults.size() == numberToPick);
        procedure.setResults(searchResults);

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        procedureRunner.submitProcedure(procedure);

    }

    public void testAverageSequence() throws Exception {

        int numberToPick = 10;
        Collection<QaiDataProvider> searchResults = pickStockProviders(numberToPick);

        assertNotNull("there has to be search results", searchResults);
        assertTrue("there has to be some search results", !searchResults.isEmpty() && searchResults.size() == numberToPick);

        StringBuffer stockNames = new StringBuffer();
        for (QaiDataProvider<StockEntity> result : searchResults) {
            stockNames.append(result.getData().getName());
            stockNames.append(" ");
        }

        // set the input search-result to the procedure
        AverageSequence average = new AverageSequence();
        QaiInjectorService.getInstance().injectMembers(average);
        average.setInputs(searchResults);

        // now we're ready to start the procedure
        average.execute();

        assertTrue("we have to believe this", average.hasExecuted());
        logger.info("started execution of procedure with: " + stockNames.toString());

        assertTrue("There have to dates!", average.getAllDates() != null && average.getAllDates().length > 0);
        assertTrue("There has to be an entity", average.getChildEntity() != null);
        //assertTrue("there have to to be stocks", !average.getChildEntity().getQuotes().isEmpty());
    }

    protected ArrayList<SearchResult> pickStocks(int numberToPick) {

        ArrayList<SearchResult> searchResults = new ArrayList<>();

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

    protected Collection<QaiDataProvider> pickStockProviders(int numberToPick) {

        Collection<QaiDataProvider> searchResults = new ArrayList<>();

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
                searchResults.add(new DataProvider(entity));
            }
            break;
        }

        return searchResults;
    }
}
