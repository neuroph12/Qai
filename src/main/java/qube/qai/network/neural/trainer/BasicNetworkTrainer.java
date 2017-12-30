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

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by rainbird on 11/23/15.
 */
public class BasicNetworkTrainer implements NeuralNetworkTrainer {

    private Logger logger = LoggerFactory.getLogger("BasicNetworkTrainer");

    private double ERROR_TOLERANCE = 0.3;

    private double MAXIMUM_EPOCH = 10000;

    private String[] tickerSymbols;

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

//    public void createTrainingSet(Map<String, Collection> trainingData) {
//
//        List<Date> dates = new ArrayList<Date>();
//        Map<Date, double[]> dataSet = spliceToDates(trainingData, dates);
//        createTrainingSet(dates, dataSet);
//
//    }

    public void createTrainingSet(Date startDate, Date endDate, Set<Date> dates, Map<String, TimeSequence> sequenceMap) {

        trainingSet = new BasicMLDataSet();

        int dummy = 0;
        tickerSymbols = new String[sequenceMap.size()];
        for (String tickerSymbol : sequenceMap.keySet()) {
            tickerSymbols[dummy] = tickerSymbol;
            dummy++;
        }

        for (Date date : dates) {

            if (date.before(startDate)) {
                continue;
            }

            double[] inDoubles = new double[sequenceMap.size()];
            double[] outDoubles = new double[sequenceMap.size()];
            int index = 0;
            Date nextDate = null;

            for (int i = 0; i < tickerSymbols.length; i++) {
                TimeSequence sequence = sequenceMap.get(tickerSymbols[i]);
                if (sequence.getValue(date) != null) {
                    Double inVal = (Double) sequence.getValue(date);
                    if (inVal == null) {
                        inDoubles[index] = 0.0;
                    } else {
                        inDoubles[index] = inVal;
                    }


                    Double outVal = (Double) sequence.getValue(nextDate);
                    if (outVal == null) {
                        outDoubles[index] = 0.0;
                    } else {
                        outDoubles[index] = outVal;
                    }
                }
            }
            MLData inData = new BasicMLData(inDoubles);
            MLData outData = new BasicMLData(outDoubles);
            MLDataPair datapair = new BasicMLDataPair(inData, outData);
            trainingSet.add(datapair);

            if (date.compareTo(endDate) == 0) {
                break;
            }

        }
    }

    public String[] getTickerSymbols() {
        return tickerSymbols;
    }

    public void setTickerSymbols(String[] tickerSymbols) {
        this.tickerSymbols = tickerSymbols;
    }

    public NeuralNetwork getNetwork() {
        return network;
    }

    public void setNetwork(NeuralNetwork network) {
        this.network = network;
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