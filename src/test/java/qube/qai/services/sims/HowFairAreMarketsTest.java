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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import qube.qai.main.QaiTestBase;
import qube.qai.network.finance.FinanceNetworkBuilder;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.persistence.search.ModelSearchService;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by zenpunk on 1/12/16.
 */
public class HowFairAreMarketsTest extends QaiTestBase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    private int numberOfStockToPick = 10;

    private String procedureDirectory = "/media/rainbird/ALEPH/qai-persistence.db/model_persistence";

    private Collection<StockEntity> entities;

    /**
     *
     * here we want to see how the tests run on the grid
     * to begin with- persist the results of those
     * so that the can be displayed on the gui-layer.
     */
    public void testHowFairAreMarkets() throws Exception {

        //PersistentModelMapStore mapStore = new PersistentModelMapStore(procedureDirectory,
        //        Procedure.class, FinanceNetworkBuilder.class, FinanceNetworkTrainer.class);
        //mapStore.init();
        ModelSearchService modelService = new ModelSearchService(PROCEDURES, procedureDirectory);
        modelService.init();

        // this is the beginning point of the proposed procedure
        int iteratioNumber = 0;
        Set<String> pickedTickerSymbols = new TreeSet<>();
        boolean goOn = true;

        while (goOn) {
            Collection<QaiDataProvider> entityProviders = pickStockProviders(pickedTickerSymbols, numberOfStockToPick);
            if (entityProviders == null || entityProviders.isEmpty()) {
                goOn = false;
                break;
            }
            assertNotNull("entities may not be null", entityProviders);
            assertTrue("there has to be entities for the test network", !entityProviders.isEmpty());

            StringBuffer nameBuffer = new StringBuffer();
            // collect the names of the entites we thus far have
            for (QaiDataProvider<StockEntity> provider : entityProviders) {
                injector.injectMembers(provider);
                String tickerSymbol = provider.getData().getTickerSymbol();
                log("iteration " + iteratioNumber + " adding: '" + tickerSymbol + "' to the entities to be analized");
                nameBuffer.append(tickerSymbol).append(" ");
                pickedTickerSymbols.add(tickerSymbol);
            }

            FinanceNetworkBuilder networkBuilder = new FinanceNetworkBuilder();
            networkBuilder.setInputs(entityProviders);

            injector.injectMembers(networkBuilder);

            log("Starting sims with: " + nameBuffer.toString());

            networkBuilder.execute();

            log("Procedure with uuid: '" + networkBuilder.getUuid() + "' of iteration " + iteratioNumber + " has completed excution- now saving it in map");
            //mapStore.store(networkBuilder.getUuid(), networkBuilder);
            modelService.save(FinanceNetworkBuilder.class, networkBuilder);

            Collection<SearchResult> results = modelService.searchInputString(networkBuilder.getProcedureName(), PROCEDURES, 10);
            assertNotNull("results may not be null", results);
            assertTrue("there have to be some results to find", !results.isEmpty());

            iteratioNumber++;
        }

    }

    protected Collection<QaiDataProvider> pickStockProviders(Set<String> pickedTickerSymbols, int numberToPick) {

        Collection<QaiDataProvider> searchResults = null;

        if (entities == null || entities.isEmpty()) {
            IMap<String, StockGroup> stockGroupMap = hazelcastInstance.getMap(STOCK_GROUPS);

            for (String key : stockGroupMap.keySet()) {
                StockGroup group = stockGroupMap.get(key);
                entities = group.getEntities();
            }
        }

        searchResults = pickOutOf(entities, pickedTickerSymbols, numberToPick);


        return searchResults;
    }

    private Collection<QaiDataProvider> pickOutOf(Collection<StockEntity> entities, Set<String> pickedTickerSymbols, int numberToPick) {

        Collection<QaiDataProvider> searchResults = new ArrayList<>();
        if (entities == null || entities.isEmpty()) {
            return searchResults;
        }

        for (Iterator<StockEntity> iterator = entities.iterator(); iterator.hasNext(); ) {
            StockEntity entity = iterator.next();
            if (!pickedTickerSymbols.contains(entity.getTickerSymbol())) {
                searchResults.add(new DataProvider(STOCK_ENTITIES, entity));
            }
            if (searchResults.size() == numberToPick) {
                break;
            }
        }

        return searchResults;

    }
}
