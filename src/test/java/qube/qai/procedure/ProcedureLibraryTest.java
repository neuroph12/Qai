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
 */
public class ProcedureLibraryTest extends QaiTestBase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureRunnerInterface procedureRunner;


    public void estMarketNetworkBuilder() throws Exception {

        FinanceNetworkBuilder networkBuilder = ProcedureLibrary.marketNetworkBuilderTemplate.createProcedure();
        assertNotNull(networkBuilder);

        fail("implement the rest of the test");
    }

    public void estChangePointAnalysis() throws Exception {

        ChangePointAnalysis changePointAnalysis = ProcedureLibrary.changePointAnalysisTemplate.createProcedure();
        assertNotNull(changePointAnalysis);

        fail("implement the rest of the test");
    }

    public void testSortingPercentilesTemplate() throws Exception {

        Set<StockEntity> pickedEntities = pickRandomFrom(5);

        assertNotNull("entitites must have been initialized", pickedEntities);
        assertTrue("there has to be entities", !pickedEntities.isEmpty());

        QaiDataProvider<Collection> entityProvider = new DummyQaiDataProvider<>(pickedEntities);

        ForEach forEach = ProcedureLibrary.sortingPercentilesTemplate.createProcedure();
        assertNotNull("duh!", forEach);

        forEach.setTargetCollectionProvider(entityProvider);

        procedureRunner.submitProcedure(forEach);

        IMap<String, Procedure> procedures = hazelcastInstance.getMap(PROCEDURES);

        Procedure procedure = procedures.get(forEach.getUuid());
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
