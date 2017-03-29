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

package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.SelectionOperator;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.ProcedureFactory;
import qube.qai.procedure.utils.SelectionProcedure;

import java.util.Collection;

/**
 * Created by rainbird on 11/28/15.
 */
public class NeuralNetworkAnalysis extends Procedure implements ProcedureConstants {

    public static String NAME = "Neural-Network Analysis";

    public static String DESCRIPTION = "Neural-network analysis " +
            "does a statistical analysis of the weights, " +
            "their network structure, etc.";

    private SelectionOperator<Collection<NeuralNetwork>> networkSelectionOperator;

    public NeuralNetworkAnalysis() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_NEURAL_NETWORK);
        arguments.putResultNames(NETWORK_METRICS);
    }

    @Override
    public void execute() {

        if (getFirstChild() != null) {
            ((Procedure) getFirstChild()).execute();
        }

        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(((Procedure) getFirstChild()).getArguments());
        }

        NeuralNetwork neuralNetwork = (NeuralNetwork) arguments.getSelector(INPUT_NEURAL_NETWORK).getData();
        if (neuralNetwork == null) {
            logger.error("Input neural-network has not been initialized properly: null value");
            return;
        }

        Metrics networkMetrics = neuralNetwork.buildMetrics();
        logger.info("adding '" + NETWORK_METRICS + "' and '" + MATRIX_METRICS + "' to return values");
        arguments.addResult(NETWORK_METRICS, networkMetrics);

    }

    /**
     * implement a static factory-class so that they can be constructed right
     */
    public static ProcedureFactory Factory = new ProcedureFactory() {

        public Procedure constructProcedure(SelectionProcedure selection) {

            if (selection == null) {
                selection = new SelectionProcedure();
            }
            MatrixStatistics matrix = new MatrixStatistics(selection);

            NetworkStatistics network = new NetworkStatistics();
            network.setFirstChild(matrix);

            NeuralNetworkAnalysis neural = new NeuralNetworkAnalysis();
            neural.setFirstChild(network);


            return neural;
        }
    };

}
