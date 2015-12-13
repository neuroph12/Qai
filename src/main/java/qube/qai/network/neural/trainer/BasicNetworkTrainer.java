package qube.qai.network.neural.trainer;

import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;

/**
 * Created by rainbird on 11/23/15.
 */
public class BasicNetworkTrainer implements NeuralNetworkTrainer {

    private double ETA = 0.1;

    private NeuralNetwork neuralNetwork;

    public BasicNetworkTrainer(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    /**
     * @param input
     * @param target
     */
    public void trainNetwork(Vector input, Vector target) {

        // @TODO optimize this part of the code
        // training the graph consists of propagating the input
        // calculating the error and correcting the coefficients
        // until a targeted error is achieved... or enough iterations are made
        Vector result = (Vector) neuralNetwork.propagate(input).negate().add(input).modify(neuralNetwork.getInverseFunction());

        Vector passError = (Vector) result.negate().add(target);
        Matrix delta = neuralNetwork.getWeights().
                multiply(input.modify(neuralNetwork.getDiffActivationFunction())).
                multiplyElements(passError);

        // first correct the weights in the builder with Widrow-Hoff factors
        long rowCount = neuralNetwork.getWeights().getMatrix().countRows();
        long columnCount = neuralNetwork.getWeights().getMatrix().countColumns();
        Access2D.Builder weightBuilder = PrimitiveMatrix.getBuilder((int) rowCount, (int) columnCount);
        for (int i = 0; i < neuralNetwork.getWeights().getMatrix().countRows(); i++) {
            for (int j = 0; j < neuralNetwork.getWeights().getMatrix().countColumns(); j++) {
                double oldValue = (Double) neuralNetwork.getWeights().getMatrix().get(i, j);
                double value = oldValue + (Double) delta.getMatrix().get(i) * ETA * (1.0/rowCount);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    weightBuilder.set(i, j, oldValue);
                    continue;
                } else {
                    weightBuilder.set(i, j, value);
                }
            }
        }
        // finally build the matrix and set it as the new weightMatrix
        neuralNetwork.getWeights().setMatrix((BasicMatrix) weightBuilder.build());


        // correct the bias vector with Hebbian
        columnCount = neuralNetwork.getBias().getMatrix().countColumns();
        Access2D.Builder biasBuilder = PrimitiveMatrix.getBuilder((int) neuralNetwork.getBias().getMatrix().countRows(), (int) columnCount);
        for (int i = 0 ; i < neuralNetwork.getBias().getMatrix().countColumns(); i++) {
            double oldValue = (Double) neuralNetwork.getBias().getMatrix().get(0, i);
            double value = oldValue + ETA * (Double) passError.getMatrix().get(0, i) *(1.0/columnCount);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                biasBuilder.set(i, 0, oldValue);
                continue;
            } else {
                biasBuilder.set(i, 0, value);
            }
        }
        // finally build the matrix and set it as the new biasMatrix
        neuralNetwork.getBias().setMatrix((BasicMatrix) biasBuilder.build());

        // this is the cumulative error of the pass
        // check first if initialized, and initialize if not
        if (neuralNetwork.getError() == null || neuralNetwork.getError().getMatrix() == null) {
            neuralNetwork.getError().setMatrix(PrimitiveMatrix.FACTORY.makeZero(passError.getMatrix().countRows(), passError.getMatrix().countColumns()));
        }
        neuralNetwork.setError((Vector) neuralNetwork.getError().add(passError.multiplyElements(passError)));
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }
}