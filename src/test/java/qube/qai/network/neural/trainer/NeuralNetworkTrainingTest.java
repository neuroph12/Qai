/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
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

package qube.qai.network.neural.trainer;

import junit.framework.TestCase;
import org.encog.ml.data.MLDataPair;
import org.joda.time.DateTime;
import org.ojalgo.random.Normal;
import org.ojalgo.random.RandomNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.TimeSequence;
import qube.qai.matrix.Matrix;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.persistence.StockQuote;

import java.util.*;

/**
 * Created by rainbird on 12/13/15.
 */
public class NeuralNetworkTrainingTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger("NeuralNetworkTrainingTest");

    String[] names = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "nineth", "tenth"};

    public void testNetworkTraining() throws Exception {

        Map<String, TimeSequence> timeSeriesMap = createTimeSeriesMap(names);
        assertNotNull(timeSeriesMap);

        NeuralNetwork network = new NeuralNetwork(names.length);
        BasicNetworkTrainer trainer = new BasicNetworkTrainer(network);
        List<Date> dates = createDates();
        Map<Date, double[]> dataSet = trainer.spliceToDates(dates, timeSeriesMap);
        // create the network and train the thing- remember this is 10 random entities
        trainer.createTrainingSet(dates, dataSet);

        trainer.trainNetwork();
        for (MLDataPair pair : trainer.getTrainingSet()) {
            double[] output = network.propagate(pair.getInput().getData());
            for (int i = 0; i < output.length; i++) {
                log("entity name: " + names[i]);
                log("input: " + pair.getInput().getData(i));
                log("output: " + output[i]);
                log("ideal: " + pair.getIdeal().getData(i));
            }
        }

        // now we want to see the adjacency-matrix values
        network.buildAdjacencyMatrix();
        Matrix adjacencyMatrix = network.getAdjacencyMatrix();
        assertNotNull(adjacencyMatrix);
        log("adjacency matrix after training: ");
        log(adjacencyMatrix.toString());
    }

    public void testNetworkTrainingWithStockQuotes() throws Exception {

        Map<String, Collection> dataMap = createStockQuoteMap(names);

        NeuralNetwork network = new NeuralNetwork(names.length);
        BasicNetworkTrainer trainer = new BasicNetworkTrainer(network);
        trainer.createTrainingSet(dataMap);

        trainer.trainNetwork();
        for (MLDataPair pair : trainer.getTrainingSet()) {
            double[] output = network.propagate(pair.getInput().getData());
            for (int i = 0; i < output.length; i++) {
                log("entity name: " + names[i]);
                log("input: " + pair.getInput().getData(i));
                log("output: " + output[i]);
                log("ideal: " + pair.getIdeal().getData(i));
            }
        }

        // now we want to see the adjacency-matrix values
        network.buildAdjacencyMatrix();
        Matrix adjacencyMatrix = network.getAdjacencyMatrix();
        assertNotNull(adjacencyMatrix);
        log("adjacency matrix after training: ");
        log(adjacencyMatrix.toString());
    }

    private List<Date> createDates() {

        Date start = DateTime.parse("2015-1-1").toDate();
        Date end = DateTime.parse("2015-12-31").toDate();
        return TimeSequence.createDates(start, end);
    }

    private Map<String, Collection> createStockQuoteMap(String... names) {

        Map<String, Collection> map = new HashMap<String, Collection>();
        RandomNumber generator = new Normal(0.5, 0.1);
        DateTime start = DateTime.parse("2015-1-1");
        DateTime end = DateTime.parse("2015-12-31");
        for (String name : names) {
            DateTime tmp = start;
            Collection<StockQuote> quotes = new ArrayList<StockQuote>();
            while (tmp.isBefore(end) || tmp.equals(end)) {
                StockQuote quote = new StockQuote();
                quote.setTickerSymbol(name);
                quote.setQuoteDate(tmp.toDate());
                quote.setAdjustedClose(generator.doubleValue());
                quotes.add(quote);
                tmp = tmp.plusDays(1);
            }
            map.put(name, quotes);
        }
        return map;
    }

    private Map<String, TimeSequence> createTimeSeriesMap(String... names) {

        Map<String, TimeSequence> timeSeriesMap = new HashMap<String, TimeSequence>();
        for (String name : names) {
            Date start = DateTime.parse("2015-1-1").toDate();
            Date end = DateTime.parse("2015-12-31").toDate();
            TimeSequence timeSequence = TimeSequence.createTimeSeries(start, end);
            timeSeriesMap.put(name, timeSequence);
        }
        return timeSeriesMap;
    }

    private void log(String message) {
        // the bloody slf4j
        //logger.info(message);
        System.out.println(message);
    }
}
