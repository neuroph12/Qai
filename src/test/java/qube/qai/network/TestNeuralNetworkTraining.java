package qube.qai.network;

import junit.framework.TestCase;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.network.neural.trainer.BasicNetworkTrainer;

/**
 * Created by rainbird on 12/13/15.
 */
public class TestNeuralNetworkTraining extends TestCase {

    private boolean debug = true;

    Vector inputOne;
    Vector targetOne;

    public void testNeuralNetworkTrainer() throws Exception {

        initInputPatterns();

        // create the neural-network and add the elements
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        // weight matrix
        Matrix weights = Matrix.buildFromArray(new double[][] {{0.5, 0.5}, {0.5, 0.5}});
        neuralNetwork.setWeights(weights);
        // bias
        Vector bias = Vector.buildFromArray(new double[]{0.01, 0.01});
        neuralNetwork.setBias(bias);
        // error
        Vector error = Vector.buildFromArray(new double[]{0.0, 0.0});
        neuralNetwork.setError(error);

        BasicNetworkTrainer trainer = new BasicNetworkTrainer(neuralNetwork);
        trainer.trainNetwork(inputOne, targetOne);
        log("weights:");
        log(neuralNetwork.getWeights().toString());
        log("bias");
        log(neuralNetwork.getBias().toString());
        log("error:");
        log(neuralNetwork.getError().toString());
    }

    private void initInputPatterns() {
        inputOne = Vector.buildFromArray(new double[]{1.0, 1.0});
        targetOne = Vector.buildFromArray(new double[]{1.0, 1.0});
    }

    // made this to toString() method in Vector
    /*private void log(Vector vector) {

        boolean areChildren = false;
        StringBuffer buffer = new StringBuffer("(");

        for (Number number : vector.getElementsAsList()) {
            buffer.append(number);
            buffer.append(", ");
            areChildren = true;
        }

        if (areChildren) {
            buffer.deleteCharAt(buffer.length()-1);
            buffer.deleteCharAt(buffer.length()-1);
        }
        buffer.append(")");
        log(buffer.toString());
    }*/

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
