package qube.qai.network.neural;

import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.Network;
import qube.qai.network.neural.function.ActivationFunction;
import qube.qai.network.neural.function.DiffSigmoidFunction;
import qube.qai.network.neural.function.LogitFunction;
import qube.qai.network.neural.function.SigmoidFunction;
import qube.qai.network.neural.trainer.NeuralNetworkTrainer;

/**
 * Created by rainbird on 11/23/15.
 */
public class NeuralNetwork extends Network {

    protected boolean debug = true;

    private Vector error;

    private Vector bias;

    protected Matrix weights;

    private ActivationFunction activationFunction;

    private ActivationFunction inverseFunction;

    private ActivationFunction diffActivationFunction;

    private NeuralNetworkTrainer networkTrainer;

    private String description;

    /**
     * a neural-network in our case, has a bias-vector
     * and a interaction part with the usual weight and all.
     * during the training each part has different algorithms for correction
     * and that has to be considered.
     * in our case, the neural-networks have always the same number of output nodes as input
     * because training is mainly for optimizing the coefficients of the Lotka-Volterra system
     */
    public NeuralNetwork() {
        activationFunction = new SigmoidFunction();
        inverseFunction = new LogitFunction();
        diffActivationFunction = new DiffSigmoidFunction();
        error = new Vector();
        bias = new Vector();
    }

    public NeuralNetwork(Matrix weights) {
        this();
        this.weights = weights;
    }

    /**
     * forward propagation of the graph
     * @param input
     * @return
     */
    public Vector propagate(Vector input) {

        // @TODO optimize this part of the code
        //  a*s1(t) + b*s1(t)*s2(t) (where t is the input value)
        Vector inSig = (Vector) input.modify(activationFunction);
        Vector temp = (Vector) bias.multiplyElements(inSig);

        Matrix delta = weights.multiply(inSig).add(temp).modify(inverseFunction);

        log("delta: " + delta.getMatrix().toString());

        Vector retVal = new Vector();
        // use the inverse function so that the result is in the same range as input
        retVal.setMatrix(delta.add(input).getMatrix()); //

        return retVal;
    }


    protected void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }


    /**
     * one step training of the graph to one input and output
     * in order to train the graph you need to have a huge set
     * of inputs and outputs and iterate over the whole thing
     * will have to do the overall error of the graph as well
     * to follow if there is any improvement at all, and how much the overall
     * performance of the graph is with the given training set
     */
    /*
    public void trainNetwork(Vector input, Vector target) {

        // training the graph consists of propagating the input
        // calculating the error and correcting the coefficients
        // until a targeted error is achieved... or enough iterations are made
        Vector result = (Vector) propagate(input).negate().add(input).modify(inverseFunction);

        Vector passError = (Vector) result.negate().add(target);
        Matrix delta = weights.multiplyLeft(input.modify(diffActivationFunction)).multiplyElements(passError);

        // first correct the weights in the builder with Widrow-Hoff factors
        long rowCount = weights.getMatrix().countRows();
        long columnCount = weights.getMatrix().countColumns();
        Access2D.Builder weightBuilder = PrimitiveMatrix.getBuilder((int) rowCount, (int) columnCount);
        for (int i = 0; i < weights.getMatrix().countRows(); i++) {
            for (int j = 0; j < weights.getMatrix().countColumns(); j++) {
                double oldValue = weights.getMatrix().get(i, j);
                double value = oldValue + delta.getMatrix().get(i) * ETA * (1.0/rowCount);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    weightBuilder.set(i, j, oldValue);
                    continue;
                } else {
                    weightBuilder.set(i, j, value);
                }
            }
        }
        // finally build the matrix and set it as the new weightMatrix
        weights.setMatrix((BasicMatrix) weightBuilder.build());


        // correct the bias vector with Hebbian
        columnCount = bias.getMatrix().countColumns();
        Access2D.Builder biasBuilder = PrimitiveMatrix.getBuilder((int) bias.getMatrix().countRows(), (int) columnCount);
        for (int i = 0 ; i < bias.getMatrix().countColumns(); i++) {
            double oldValue = bias.getMatrix().get(0, i);
            double value = oldValue + ETA * passError.getMatrix().get(0, i) *(1.0/columnCount);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                biasBuilder.set(i, 0, oldValue);
                continue;
            } else {
                biasBuilder.set(i, 0, value);
            }
        }
        // finally build the matrix and set it as the new biasMatrix
        bias.setMatrix((BasicMatrix<Double>) biasBuilder.build());

        // this is the cumulative error of the pass
        // check first if initialized, and initialize if not
        if (error == null || error.getMatrix() == null) {
            error.setMatrix(PrimitiveMatrix.FACTORY.makeZero(passError.getMatrix().countRows(), passError.getMatrix().countColumns()));
        }
        error = (Vector) error.add(passError.multiplyElements(passError));
    }
    */

    public Vector getError() {
        return error;
    }

    public Vector getBias() {
        return bias;
    }

    public void setBias(Vector bias) {
        this.bias = bias;
    }

    public Matrix getWeights() {
        return weights;
    }

    public void setWeights(Matrix weights) {
        this.weights = weights;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    public ActivationFunction getInverseFunction() {
        return inverseFunction;
    }

    public void setInverseFunction(ActivationFunction inverseFunction) {
        this.inverseFunction = inverseFunction;
    }

    public ActivationFunction getDiffActivationFunction() {
        return diffActivationFunction;
    }

    public void setDiffActivationFunction(ActivationFunction diffActivationFunction) {
        this.diffActivationFunction = diffActivationFunction;
    }

    public void setError(Vector error) {
        this.error = error;
    }

    public NeuralNetworkTrainer getNetworkTrainer() {
        return networkTrainer;
    }

    public void setNetworkTrainer(NeuralNetworkTrainer networkTrainer) {
        this.networkTrainer = networkTrainer;
    }
}
