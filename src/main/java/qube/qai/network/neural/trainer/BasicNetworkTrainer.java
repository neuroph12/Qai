package qube.qai.network.neural.trainer;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.TimeSeries;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;

import java.util.*;

/**
 * Created by rainbird on 11/23/15.
 */
public class BasicNetworkTrainer implements NeuralNetworkTrainer {

    private Logger logger = LoggerFactory.getLogger("BasicNetworkTrainer");

    private double ERROR_TOLERANCE = 0.2;

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
            logger.info("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
            if (epoch >= MAXIMUM_EPOCH) {
                logger.info("Maximum number of iterations have been arrived- stopping training");
                break;
            }
        } while(train.getError() > ERROR_TOLERANCE);

        train.finishTraining();

        // i don't know, is this really necessary...
        // well, can't really harm, i guess.
        Encog.getInstance().shutdown();
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

    public static Map<Date, double[]> spliceToDates(List<Date> dates, Map<String, TimeSeries> timeSeriesMap) {
        Map<Date, double[]> dataSet = new TreeMap<Date, double[]>();
        for (Date date : dates) {
            double[] daily = new double[timeSeriesMap.size()];
            int i = 0;
            for (String name : timeSeriesMap.keySet()) {
                TimeSeries timeSeries = timeSeriesMap.get(name);
                // this is date's value for each name
                daily[i] = timeSeries.getValue(date).doubleValue();
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