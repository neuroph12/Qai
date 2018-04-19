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

package qube.qai.network.finance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.encog.ml.data.MLDataPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by zenpunk on 12/25/15.
 */
public class FinanceNetworkBuilderTest extends QaiTestBase {

    private Logger logger = LoggerFactory.getLogger("FinanceNetworkBuilderTest");

    @Inject
    private HazelcastInstance hazelcastInstance;

    /**
     * well this is actually pretty much it...
     * this is almost the moment of truth we have been waiting for...
     */
    public void testMarketBuilder() throws Exception {

        int numberOfEntities = 10;
        String[] names = new String[numberOfEntities];

        Collection<QaiDataProvider> workingSet = pickStockProviders(numberOfEntities);

        assertNotNull("there has to be a working set", workingSet);
        assertTrue("there has to be the right number of elements", !workingSet.isEmpty() && workingSet.size() == numberOfEntities);

        logger.info("picked entities: " + array2String(names));

        FinanceNetworkBuilder networkBuilder = new FinanceNetworkBuilder();
        networkBuilder.setInputs(workingSet);

        injector.injectMembers(networkBuilder);

        networkBuilder.execute();

        assertTrue("all must have gone well and execution complete", networkBuilder.hasExecuted());

        FinanceNetworkTrainer[] spawn = networkBuilder.getSpawn();
        assertNotNull("spawn may not be null", spawn);
        assertTrue("there has to be some spawn", spawn.length > 0);

        for (FinanceNetworkTrainer trainer : spawn) {

            assertNotNull("trainer may not be null", trainer);

            NeuralNetwork network = trainer.getNetwork();

            assertNotNull("spawn must have trained network", network);

            network.getVertices();

            // ok now we take a look at the results
            int displayCount = 0;
            int maxCount = 10;
            for (MLDataPair pair : trainer.getTrainer().getTrainingSet()) {
                double[] output = network.propagate(pair.getInput().getData());
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < output.length; i++) {
                    buffer.append("entity name: " + names[i]);
                    buffer.append(" input: " + pair.getInput().getData(i));
                    buffer.append(" output: " + output[i]);
                    buffer.append(" ideal: " + pair.getIdeal().getData(i));
                    buffer.append("\n");
                }
                log(buffer.toString());
                if (displayCount >= maxCount) {
                    break;
                }
                displayCount++;
            }
        }
    }

    private String array2String(String[] names) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < names.length; i++) {
            buffer.append(names[i]).append(" ");
        }
        return buffer.toString();
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
