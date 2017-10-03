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

package qube.qai.network;

import junit.framework.TestCase;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;

/**
 * Created by rainbird on 11/23/15.
 */
public class NeuralNetworkTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger("NeuralNetworkTest");

    private int EPOCH = 10000;

    protected boolean debug = true;

    @Override
    protected void setUp() throws Exception {
        //super.setUp();
    }

    /**
     * in this test the created the network to compare the results with encog
     * tests the build-routine really
     */
    public void testNeuralNetwork() {

        // decided to use something called Encog-
        // the thing looks to be pretty complete
        double XOR_INPUT[][] = {{0.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}, {1.0, 1.0}};
        double XOR_IDEAL[][] = {{0.0, 0.0}, {1.0, 0.0}, {1.0, 0.0}, {0.0, 0.0}};

        // weights copied from reasonably well-trained network doing the same thing
        double[] weights = {-0.07436579220019145, 0.04068370154784863, -0.6118178821788891, -1.3482376121640667};
        double[][] weightArray = {{-0.07436579220019145, 0.04068370154784863}, {-0.6118178821788891, -1.3482376121640667}};

        // our neural-network
        Matrix matrix = Matrix.buildFromArray(weightArray);
        log("matrix created: " + matrix.toString());
        NeuralNetwork neuralNetwork = new NeuralNetwork(matrix);

        // encog version of the same network
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, false, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));
        network.getStructure().finalizeStructure();
        network.reset();
        network.decodeFromArray(weights);

        // this is in order to test the method- later make this call automatic
        //neuralNetwork.buildFromAdjacencyMatrix();
        for (int i = 0; i < XOR_INPUT.length; i++) {

            Vector inVector = Vector.buildFromArray(XOR_INPUT[i]);
            Vector outVector = neuralNetwork.propagate(inVector);
            log("in vector: " + inVector);
            log("result neural-network: " + outVector);

            double[] outArray = new double[2];
            network.compute(inVector.toArray(), outArray);
            log("result encog: " + arrayAsString(outArray));

            double[] out = outVector.toArray();
            for (int j = 0; j < outArray.length; j++) {
                String message = "pattern: " + i + " output: " + out[j] + " encog computed: " + outArray[j];
                //log(message);
                assertTrue(message, out[j] == outArray[j]);
            }
        }

    }

    public void restEncog_2() throws Exception {
        // decided to use something called Encog-
        // the thing looks to be pretty complete
        double XOR_INPUT[][] = {{0.0, 0.0, 0.0},
                {1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0},
                {1.0, 1.0, 1.0},
                {1.0, 1.0, 0.0},
                {0.0, 1.0, 1.0}};

        double XOR_IDEAL[][] = {{0.0, 0.0, 0.0},
                {1.0, 0.0, 0.0},
                {1.0, 0.0, 0.0},
                {1.0, 0.0, 0.0},
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0}};

        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, false, 3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 3));
        network.getStructure().finalizeStructure();
        network.reset();

        // create training data
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

        // train the neural network
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;

        do {
            train.iteration();
            log("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (train.getError() > 0.2);
        train.finishTraining();

        log("trained network weights:" + network.dumpWeights());
        int arrayLength = network.encodedArrayLength();
        double[] array = new double[arrayLength];
        network.encodeToArray(array);
        int layerCount = network.getLayerCount();
        log("network layer count: " + layerCount + " weight count: " + arrayLength);
        for (int i = 0; i < array.length; i++) {
            log("element i: " + i + " value:" + array[i]);
        }

        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet) {
            final MLData output = network.compute(pair.getInput());
            log(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
    }

    private String arrayAsString(double[] array) {
        StringBuffer buffer = new StringBuffer("(");
        boolean more = false;
        for (int i = 0; i < array.length; i++) {
            buffer.append(array[i]).append(", ");
            more = true;
        }
        if (more) {
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * this is in order to make some experiments with the library encog
     */
    public void restEncog_1() throws Exception {
        // decided to use something called Encog-
        // the thing looks to be pretty complete
        double XOR_INPUT[][] = {{0.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}, {1.0, 1.0}};

        double XOR_IDEAL[][] = {{0.0}, {1.0}, {1.0}, {0.0}};

        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
        network.getStructure().finalizeStructure();
        network.reset();

        // create training data
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

        // train the neural network
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;

        do {
            train.iteration();
            log("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (train.getError() > 0.2);
        train.finishTraining();

        log("trained network weights:" + network.dumpWeights());
        int arrayLength = network.encodedArrayLength();
        double[] array = new double[arrayLength];
        network.encodeToArray(array);
        int layerCount = network.getLayerCount();
        log("network layer count: " + layerCount + " weight count: " + arrayLength);
        for (int i = 0; i < array.length; i++) {
            log("element i: " + i + " value:" + array[i]);
        }

//        for (int layer = 0; layer < network.getLayerCount(); layer++) {
//            int neuronCount = network.getLayerNeuronCount(layer);
//            for (int i = 0; i < neuronCount; i++) {
//                for (int j = 0; j < neuronCount; j++) {
//                    double weight = network.getWeight(layer, i, j);
//                    log("layer:(" + layer + ") weight i-j: (" + i + ", " + j + ") " + weight);
//                }
//            }
//        }

        // test the neural network
        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet) {
            final MLData output = network.compute(pair.getInput());
            log(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
    }

    protected void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.info(message);
        }
    }
}
