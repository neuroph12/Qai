package qube.qai.network.neural.trainer;

import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;

/**
 * Created by rainbird on 11/23/15.
 */
public class BasicNetworkTrainer implements NeuralNetworkTrainer {

    private Logger logger = LoggerFactory.getLogger("BasicNetworkTrainer");

    private double ETA = 0.1;

    private NeuralNetwork neuralNetwork;

    private long weightRows;

    private long weightColumns;

    private long biasColumns;

    private long biasRows;

    private Vector result;

    public BasicNetworkTrainer(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    /**
     * @param input
     * @param target
     */
    public void trainNetwork(Vector input, Vector target) {

        // begin with initializing row and column counts
        weightRows = neuralNetwork.getWeights().getMatrix().countRows();
        weightColumns = neuralNetwork.getWeights().getMatrix().countColumns();
        biasColumns = neuralNetwork.getBias().getMatrix().countColumns();
        biasRows = neuralNetwork.getBias().getMatrix().countRows();
        // training the network consists of propagating the input
        // calculating the error and correcting the coefficients
        // until a targeted error is achieved... or enough iterations are made
        result = neuralNetwork.propagate(input);//.negate().add(input).modify(neuralNetwork.getInverseFunction());

        Vector passDelta = result.negate().add(target);
        Vector passError = passDelta.multiplyElements(passDelta);

        Vector delta = passDelta.multiplyElements(input.modify(neuralNetwork.getDiffActivationFunction()));
        Vector deltaJ = delta.multiplyElements(result);
        // correct the bias vector
        Access2D.Builder biasBuilder = PrimitiveMatrix.getBuilder((int) biasRows, (int) biasColumns);
        for (int i = 0 ; i < biasColumns; i++) {
            double oldValue = (Double) neuralNetwork.getBias().getMatrix().get(0, i);
            double value = oldValue + ETA * (Double) deltaJ.getMatrix().get(0, i) *(1.0/weightColumns);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                biasBuilder.set(i, 0, oldValue);
                logger.info("correction to bias element: " + i + " calculated " + value + " using old value: " + oldValue);
                continue;
            } else {
                biasBuilder.set(i, 0, value);
                logger.info("correction to bias element: " + i + " corrected value: " + value);
            }
        }
        // finally build the matrix and set it as the new biasMatrix
        neuralNetwork.getBias().setMatrix((BasicMatrix) biasBuilder.build());

        // correct the weights in the weight-builder with deltaJ
        Access2D.Builder weightBuilder = PrimitiveMatrix.getBuilder((int) weightRows, (int) weightColumns);
        for (int i = 0; i < weightRows; i++) {
            for (int j = 0; j < weightColumns; j++) {
                double oldValue = (Double) neuralNetwork.getWeights().getMatrix().get(i, j);
                double value = oldValue + (Double) deltaJ.getMatrix().get(i) * ETA * (1.0/weightRows);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    logger.info("correction to weight element: (" + i + ","  + j + ") calculated " + value + " using old value: " + oldValue);
                    weightBuilder.set(i, j, oldValue);
                    continue;
                } else {
                    logger.info("correction to weight element: (" + i + ","  + j + ") corrected value: " + value);
                    weightBuilder.set(i, j, value);
                }
            }
        }
        // finally build the matrix and set it as the new weightMatrix
        neuralNetwork.getWeights().setMatrix((BasicMatrix) weightBuilder.build());

        // this is the cumulative error of the pass
        neuralNetwork.setError(passError);
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public Vector getResult() {
        return result;
    }
}