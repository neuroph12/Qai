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

        // s-1[ b_i x w_ij x s(in)_i ]
        Vector inActivate = input.modify(activationFunction);
        Vector delta = inActivate.multiplyLeft(weights).multiplyElements(bias).modify(inverseFunction);

        return delta;
    }


    protected void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

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
