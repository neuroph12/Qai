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

package qube.qai.procedure.analysis;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.encog.ml.data.MLDataPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.DataSelectionOperator;
import qube.qai.main.QaiTestBase;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by rainbird on 12/25/15.
 */
public class MarketNetworkBuilderTest extends QaiTestBase {

    private Logger logger = LoggerFactory.getLogger("MarketNetworkBuilderTest");

    @Inject
    private HazelcastInstance hazelcastInstance;

    /**
     * well this is actually pretty much it...
     * this is almost the moment of truth we have been waiting for...
     */
    public void testMarketBuilder() throws Exception {

        int numberOfEntities = 10;
        String[] names = new String[numberOfEntities];

        IMap<String, StockGroup> groupMap = hazelcastInstance.getMap(STOCK_GROUPS);
        assertTrue("there has to be a group to loaded", !groupMap.keySet().isEmpty());
        StockGroup groupPicked = null;
        for (String groupKey : groupMap.keySet()) {
            StockGroup group = groupMap.get(groupKey);
            if (!group.getEntities().isEmpty()) {
                groupPicked = group;
                break;
            }
        }

        assertNotNull("there has to be a group with some members", groupMap);
        Collection<StockEntity> entityList = groupPicked.getEntities();

        // now we have the list of entities with which we want to build
        // the network for, we can simply pick, say 100 of them
        // and make a trial go with the thing
        Collection<StockEntity> workingSet = pickRandomFrom(numberOfEntities, entityList, names);
        logger.info("picked entities: " + array2String(names));

        SelectionOperator<Collection> selectionOperator = new DataSelectionOperator<Collection>(workingSet);
        MarketNetworkBuilder networkBuilder = new MarketNetworkBuilder();
        injector.injectMembers(networkBuilder);
        NeuralNetwork network = (NeuralNetwork) networkBuilder.buildNetwork(selectionOperator);
        assertNotNull("duh!", network);

        network.getVertices();

        // ok now we take a look at the results
        int displayCount = 0;
        int maxCount = 10;
        for (MLDataPair pair : networkBuilder.getTrainer().getTrainingSet()) {
            double[] output = network.propagate(pair.getInput().getData());
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < output.length; i++) {
                buffer.append("entity name: " + names[i]);
                buffer.append(" input: " + pair.getInput().getData(i));
                buffer.append(" output: " + output[i]);
                buffer.append(" ideal: " + pair.getIdeal().getData(i));
                buffer.append("\n");
            }
            logger.info(buffer.toString());
            if (displayCount >= maxCount) {
                break;
            }
            displayCount++;
        }
    }

    private String array2String(String[] names) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < names.length; i++) {
            buffer.append(names[i]).append(" ");
        }
        return buffer.toString();
    }

    private Collection<StockEntity> pickRandomFrom(int number, Collection<StockEntity> original, String[] names) {
        Set<StockEntity> picked = new HashSet<StockEntity>();
        Random random = new Random();
        int addCount = 0;
        while (picked.size() < number) {
            int pick = random.nextInt(original.size());
            Iterator<StockEntity> it = original.iterator();
            for (int j = 0; it.hasNext(); j++) {
                StockEntity entity = it.next();
                if (j == pick) {
                    if (picked.add(entity)) {
                        names[addCount] = entity.getTickerSymbol();
                        addCount++;
                        break;
                    }
                }
            }
        }

        return picked;
    }
}
