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

package qube.qai.network.neural.trainer;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.TimeSequence;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.persistence.StockQuote;

import java.util.*;

/**
 * Created by rainbird on 11/23/15.
 */
public class BasicNetworkTrainer implements NeuralNetworkTrainer {

    private Logger logger = LoggerFactory.getLogger("BasicNetworkTrainer");

    private double ERROR_TOLERANCE = 0.3;

    private double MAXIMUM_EPOCH = 10000;

    private NeuralNetwork network;

    private ResilientPropagation train;

    private MLDataSet trainingSet;

    private long epoch;

    public BasicNetworkTrainer(NeuralNetwork neuralNetwork) {
        this.network = neuralNetwork;
    }

    public void trainNetwork() {

        if (network == null || network.getNetwork() == null) {
            throw new IllegalArgumentException("No network set to train");
        }

        if (trainingSet == null) {
            throw new IllegalArgumentException("No training set created set to train the network with");
        }

        epoch = 1;
        train = new ResilientPropagation(network.getNetwork(), trainingSet);

        do {
            train.iteration();
            logger.debug("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
            if (epoch >= MAXIMUM_EPOCH) {
                logger.info("Maximum number of iterations have been arrived- stopping training");
                break;
            }
        } while (train.getError() > ERROR_TOLERANCE);

        train.finishTraining();

        // i don't know, is this really necessary...
        // well, can't really harm, i guess.
        Encog.getInstance().shutdown();
    }

    public void createTrainingSet(Map<String, Collection> trainingData) {

        List<Date> dates = new ArrayList<Date>();
        Map<Date, double[]> dataSet = spliceToDates(trainingData, dates);
        createTrainingSet(dates, dataSet);

    }

    public void createTrainingSet(List<Date> dates, Map<Date, double[]> dataSet) {

        trainingSet = new BasicMLDataSet();

        for (int i = 0; i < dates.size(); i++) {
            Date current = dates.get(i);
            if (i + 1 < dates.size()) {
                Date next = dates.get(i + 1);
                MLData inData = new BasicMLData(dataSet.get(current));
                MLData outData = new BasicMLData(dataSet.get(next));
                MLDataPair datapair = new BasicMLDataPair(inData, outData);
                trainingSet.add(datapair);
            }
        }
    }

    /**
     * splices the data to dates and double-arrays
     *
     * @param map      the map with data
     * @param dateList an empty list which will be filled by the routine
     * @return
     */
    public static Map<Date, double[]> spliceToDates(Map<String, Collection> map, List<Date> dateList) {
        Map<Date, double[]> dataSet = new TreeMap<Date, double[]>();

        // begin with collecting dates
        HashSet<Date> dates = new HashSet<Date>();
        for (String name : map.keySet()) {
            Collection<StockQuote> quotes = map.get(name);
            // we are hoping that date.equals works right
            // and even if all the dates are not same, we want to
            // cover those which do have the same dates
            for (StockQuote quote : quotes) {
                if (dates.add(quote.getQuoteDate())) {
                    dateList.add(quote.getQuoteDate());
                }
            }
        }

        // prepare the keys with the names of entities- their order may not be different!!!
        Vector<String> names = new Vector<String>();
        for (String name : map.keySet()) {
            names.add(name);
        }

        // now we have the dates and we need arrays of doubles
        for (Date date : dates) {
            double[] dailyAverages = new double[names.size()];
            for (int i = 0; i < names.size(); i++) {
                Collection<StockQuote> quotes = map.get(names.get(i));
                StockQuote dailyQuote = null;
                for (StockQuote quote : quotes) {
                    if (quote.equals(date)) {
                        dailyQuote = quote;
                        break;
                    }
                }
                if (dailyQuote != null) {
                    dailyAverages[i] = dailyQuote.getAdjustedClose();
                } else {
                    dailyAverages[i] = 0.0;
                }
            }
            // and finally add the thing to the map
            dataSet.put(date, dailyAverages);
        }


        return dataSet;
    }

    public static Map<Date, double[]> spliceToDates(List<Date> dates, Map<String, TimeSequence> timeSeriesMap) {
        Map<Date, double[]> dataSet = new TreeMap<Date, double[]>();
        for (Date date : dates) {
            double[] daily = new double[timeSeriesMap.size()];
            int i = 0;
            for (String name : timeSeriesMap.keySet()) {
                TimeSequence timeSequence = timeSeriesMap.get(name);
                // this is date's value for each name
                daily[i] = timeSequence.getValue(date).doubleValue();
                dataSet.put(date, daily);
                i++;
            }
        }

        return dataSet;
    }

    public NeuralNetwork getNeuralNetwork() {
        return network;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.network = neuralNetwork;
    }

    public MLDataSet getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(MLDataSet trainingSet) {
        this.trainingSet = trainingSet;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public double getERROR_TOLERANCE() {
        return ERROR_TOLERANCE;
    }

    public void setERROR_TOLERANCE(double ERROR_TOLERANCE) {
        this.ERROR_TOLERANCE = ERROR_TOLERANCE;
    }

    public double getMAXIMUM_EPOCH() {
        return MAXIMUM_EPOCH;
    }

    public void setMAXIMUM_EPOCH(double MAXIMUM_EPOCH) {
        this.MAXIMUM_EPOCH = MAXIMUM_EPOCH;
    }
}