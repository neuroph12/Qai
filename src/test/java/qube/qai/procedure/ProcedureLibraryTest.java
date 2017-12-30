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

package qube.qai.procedure;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import qube.qai.main.QaiTestBase;
import qube.qai.network.finance.FinanceNetworkBuilder;
import qube.qai.persistence.DummyQaiDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.finance.SequenceCollectionAverager;
import qube.qai.procedure.utils.ForEach;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * this is meant to be the collection tests for the templates which are created
 * by the ProcedureLibrary- those will be the used in the user interface directly
 * after all, therefore making this close to a integration test
 * @Deprecated moved the whole test to Qoan-Module so that it can serve the integration test role there properly
 */
@Deprecated
public class ProcedureLibraryTest extends QaiTestBase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureRunnerInterface procedureRunner;


    public void estMarketNetworkBuilder() throws Exception {

        FinanceNetworkBuilder networkBuilder = ProcedureLibrary.financeNetworkBuilderTemplate.createProcedure();
        assertNotNull(networkBuilder);

        fail("implement the rest of the test");
    }

    public void estChangePointAnalysis() throws Exception {

        Set<StockEntity> entities = pickRandomFrom(1);
        assertTrue("there has to be a stock entity", !entities.isEmpty());

        ChangePointAnalysis changePointAnalysis = ProcedureLibrary.changePointAnalysisTemplate.createProcedure();
        assertNotNull("duh!", changePointAnalysis);

        QaiDataProvider<StockEntity> provider = new DummyQaiDataProvider<>(entities.iterator().next());
        changePointAnalysis.setEntityProvider(provider);

        procedureRunner.submitProcedure(changePointAnalysis);

        IMap<String, Procedure> procedureMap = hazelcastInstance.getMap(PROCEDURES);
        Procedure copy = procedureMap.get(changePointAnalysis.getUuid());
        assertNotNull("there has to be a copy and all that", copy);
        Collection markers = ((ChangePointAnalysis) copy).getMarkers();
        assertNotNull("if the procedure has actually been executed and save there have to be markers", markers);
    }

    public void testSequenceAverager() throws Exception {

        Set<StockEntity> entities = pickRandomFrom(10);
        assertTrue("there has to be a stock entity", !entities.isEmpty());

        SequenceCollectionAverager procedure = ProcedureLibrary.sequenceAveragerTemplate.createProcedure();
        assertNotNull("duh!", procedure);

        QaiDataProvider<Collection> provider = new DummyQaiDataProvider<>(entities);
        ForEach forEach = new ForEach();
        forEach.setTargetCollectionProvider(provider);
        procedure.setCollectorForEach(forEach);

        procedureRunner.submitProcedure(procedure);
    }

    /**
     * right now i don't see how this is required
     *
     * @throws Exception
     */
    @Deprecated
    public void estSortingPercentilesTemplate() throws Exception {

        Set<StockEntity> pickedEntities = pickRandomFrom(5);

        assertNotNull("entitites must have been initialized", pickedEntities);
        assertTrue("there has to be entities", !pickedEntities.isEmpty());

        QaiDataProvider<Collection> entityProvider = new DummyQaiDataProvider<>(pickedEntities);

        ForEach forEach = ProcedureLibrary.sortingPercentilesTemplate.createProcedure();
        assertNotNull("duh!", forEach);

        forEach.setTargetCollectionProvider(entityProvider);

        procedureRunner.submitProcedure(forEach);

        IMap<String, Procedure> procedureMap = hazelcastInstance.getMap(PROCEDURES);

        Procedure procedure = procedureMap.get(forEach.getUuid());
        assertNotNull("there has to be a persisted procedure ", procedure);
        assertTrue("procedure must have run by now", procedure.hasExecuted());
    }

    public void estStockQuoteRetrieverTemplate() throws Exception {

        ForEach foreach = ProcedureLibrary.stockQuoteUpdaterTemplate.createProcedure();
        assertNotNull("duh!", foreach);


        Set<StockEntity> pickedEntities = pickRandomFrom(5);

        assertNotNull("entitites must have been initialized", pickedEntities);
        assertTrue("there has to be entities", !pickedEntities.isEmpty());

        QaiDataProvider<Collection> entityProvider = new DummyQaiDataProvider<>(pickedEntities);
        foreach.setTargetCollectionProvider(entityProvider);
        procedureRunner.submitProcedure(foreach);
        // and hope all has gone well, i suppose
        String names = "";
        for (StockEntity entity : pickedEntities) {
            names += entity.getTickerSymbol() + " ";
        }
        log("Have successfully submitted update procedure for " + names);
    }

    private Set<StockEntity> pickRandomFrom(int size) {

        Set<StockEntity> picked = new HashSet<>();
        IMap<String, StockGroup> groupMap = hazelcastInstance.getMap(STOCK_GROUPS);
        assertTrue("there has to be some groups", !groupMap.keySet().isEmpty());

        for (String uuid : groupMap.keySet()) {
            StockGroup group = groupMap.get(uuid);
            // check whether the group has entities and delete it if not

            if (group.getEntities().isEmpty()) {
                groupMap.delete(group);
                String message = String.format("StockGroup '%s' has no entities- deleting", group.getName());
                log(message);
            }

            for (StockEntity entity : group.getEntities()) {
                picked.add(entity);
                if (picked.size() >= size) {
                    break;
                }
            }

            break;
        }

        return picked;
    }
}
