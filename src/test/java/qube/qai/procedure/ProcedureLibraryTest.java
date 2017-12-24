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
import qube.qai.persistence.DummyQaiDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
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

    public void testSortingPercentilesTemplate() throws Exception {

        ForEach forEach = ProcedureLibrary.sortingPercentilesTemplate.createProcedure();
        assertNotNull("duh!", forEach);

        procedureRunner.submitProcedure(forEach);

        IMap<String, Procedure> procedures = hazelcastInstance.getMap(PROCEDURES);
//        procedures.put(forEach.getUuid(), forEach);
        Procedure procedure = procedures.get(forEach.getUuid());
        assertNotNull("there has to be a persisted procedure ", procedure);
        assertTrue("procedure must have run by now", procedure.hasExecuted());
    }

    public void estStockQuoteRetrieverTemplate() throws Exception {

        ForEach foreach = ProcedureLibrary.stockQuoteRetriverTemplate.createProcedure();
        assertNotNull("duh!", foreach);

        IMap<String, StockGroup> groupMap = hazelcastInstance.getMap(STOCK_GROUPS);
        assertTrue("there has to be some groups", !groupMap.keySet().isEmpty());
        Set<StockEntity> pickedEntities = null;
        for (String uuid : groupMap.keySet()) {
            StockGroup group = groupMap.get(uuid);
            // check whether the group has entities and delete it if not
            if (group.getEntities().isEmpty()) {
                groupMap.delete(group);
                String message = String.format("StockGroup '%s' has no entities- deleting", group.getName());
                log(message);
            }

            pickedEntities = pickRandomFrom(5, group.getEntities());
            break;
        }
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
        log("Have successfully submitted update pricedure for " + names);
    }

    private Set<StockEntity> pickRandomFrom(int size, Set<StockEntity> entities) {

        Set<StockEntity> picked = new HashSet<>();

        for (StockEntity entity : entities) {
            picked.add(entity);
            if (picked.size() >= size) {
                break;
            }
        }

        return picked;
    }
}
