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

package qube.qai.network.neural;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.PrimitiveMatrix;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.Network;

/**
 * Created by rainbird on 11/23/15.
 */
public class NeuralNetwork extends Network {

    protected boolean debug = true;

    protected int size;

    protected BasicNetwork network;

    private String description;

    /**
     * a neural-network in our case, does not have a bias
     * and a interaction part with the usual weight and all.
     * during the training each part has different algorithms for correction
     * and that has to be considered.
     * in our case, the neural-networks have always the same number of output nodes as input
     * because training is mainly for optimizing the coefficients of the Lotka-Volterra system
     */
    public NeuralNetwork() {
    }

    /**
     * this network will be assigned its values when the
     * actual training occurs
     *
     * @param size
     */
    public NeuralNetwork(int size) {
        this.size = size;
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, false, size));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, size));
        network.getStructure().finalizeStructure();
        network.reset();
    }

    /**
     * this network will build the both the neural network
     * using the given adjacency-matrix, and the graph-network
     * again using the same adjacency-matrix
     *
     * @param weights
     */
    public NeuralNetwork(Matrix weights) {
        this.adjacencyMatrix = weights;
        // this might seem strange,
        // but this way, both graph and encog networks are both set to the same thing
        this.buildFromAdjacencyMatrix();
        super.buildFromAdjacencyMatrix();
    }

    /**
     * forward propagation of the graph
     *
     * @param input
     * @return
     */
    public Vector propagate(Vector input) {

        double[] in = input.toArray();
        double[] out = propagate(in);

        Vector result = Vector.buildFromArray(out);
        return result;
    }

    public double[] propagate(double[] input) {
        double[] output = new double[input.length];
        network.compute(input, output);
        return output;
    }

    protected void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

    public BasicNetwork getNetwork() {
        return network;
    }

    @Override
    public void buildAdjacencyMatrix() {

        int arrayLength = network.encodedArrayLength();
        double[] array = new double[arrayLength];
        network.encodeToArray(array);

        Access2D.Builder<PrimitiveMatrix> builder = PrimitiveMatrix.FACTORY.getBuilder(size, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int index = j * size + i;
                builder.set(i, j, array[index]);
            }
        }

        adjacencyMatrix = new Matrix(builder.build());
    }

    public void buildFromAdjacencyMatrix() {

        size = (int) adjacencyMatrix.getMatrix().countRows();
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, false, size));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, size));
        network.getStructure().finalizeStructure();

        double[] flatArray = new double[size * size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int index = j * size + i;
                flatArray[index] = adjacencyMatrix.getValueAt(i, j);
            }
        }

        network.decodeFromArray(flatArray);
    }
}
