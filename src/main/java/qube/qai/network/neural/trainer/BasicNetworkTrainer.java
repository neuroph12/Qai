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

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 11/23/15.
 */
public class BasicNetworkTrainer implements NeuralNetworkTrainer, Serializable {

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

    /**
     * this method has to be called before the training can be
     *
     * @param startDate
     * @param endDate
     * @param dates
     * @param sequences
     */
    public void createTrainingSet(Date startDate, Date endDate, Date[] dates, TimeSequence[] sequences) {

        trainingSet = new BasicMLDataSet();

        int dummy = 0;
        tickerSymbols = new String[sequences.length];
        for (TimeSequence sequence : sequences) {
            tickerSymbols[dummy] = sequence.getSequenceOf();
            dummy++;
        }

        boolean ignoreDates = false;
        if (!startDate.before(endDate)) {
            logger.info("Start date " + startDate.toString() + " is not before end date: " + endDate.toString() + " will ignore dates");
            ignoreDates = true;
        }


        for (int i = 0; i < dates.length; i++) {

            Date date = dates[i];
            Date nextDate = null;
            if (i + 1 < dates.length) {
                nextDate = dates[i + 1];
            }


            if (!ignoreDates && date.before(startDate)) {
                continue;
            }

            double[] inDoubles = new double[sequences.length];
            double[] outDoubles = new double[sequences.length];
            int index = 0;

            for (int j = 0; j < tickerSymbols.length; j++) {
                TimeSequence sequence = sequences[j];
                if (!Double.isNaN(sequence.getValue(date))) {
                    Double inVal = (Double) sequence.getValue(date);
                    if (inVal == null) {
                        inDoubles[index] = 0.0;
                    } else {
                        inDoubles[index] = inVal;
                    }

                    if (nextDate == null) {
                        continue;
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

            if (!ignoreDates && endDate != null && date.compareTo(endDate) == 0) {
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