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

import qube.qai.data.SelectionOperator;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.network.neural.trainer.BasicNetworkTrainer;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.nodes.ValueNode;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by rainbird on 12/25/15.
 */
public class FinanceNetworkBuilder extends Procedure implements NetworkBuilder, ProcedureConstants {

    public static String NAME = "Market Network Builder";

    public static String DESCRIPTION = "Creates and trains a neural-network out of the quotes of the stocks given";


    private Collection<StockEntity> entities;

    private BasicNetworkTrainer trainer;

    private NeuralNetwork network;

    public FinanceNetworkBuilder() {
        super(NAME);
    }

    @Override
    public void execute() {

        if (entities == null || entities.isEmpty()) {
            error("The list of stock-entites is empty- returning");
            return;
        }

        HashMap<String, Collection> trainingData = new HashMap<String, Collection>();
        network = new NeuralNetwork(entities.size());
        for (StockEntity entity : entities) {
            Network.Vertex vertex = new Network.Vertex(entity.getTickerSymbol());
            // while we are at it we collect the data here as well
            Collection<StockQuote> quotes = entity.getQuotes();
            if (quotes != null || !quotes.isEmpty()) {
                network.addVertex(vertex);
                trainingData.put(entity.getTickerSymbol(), quotes);
            }
        }

        // well, here goes nothing
        trainer = new BasicNetworkTrainer(network);
        trainer.createTrainingSet(trainingData);
        trainer.trainNetwork();

        info("Market-network builder ended with: " + TRAINED_NEURAL_NETWORK);
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<Collection<StockEntity>>(INPUT_STOCK_ENTITY_COLLECTION, MIMETYPE_STOCK_ENITIY_LIST) {
            @Override
            public void setValue(Collection<StockEntity> value) {
                entities = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<NeuralNetwork>(TRAINED_NEURAL_NETWORK, MIMETYPE_NEURAL_NETWORK) {
            @Override
            public NeuralNetwork getValue() {
                return network;
            }
        });
    }

    @Override
    public Network buildNetwork(SelectionOperator source) {
        return null;
    }

    public BasicNetworkTrainer getTrainer() {
        return trainer;
    }

}
