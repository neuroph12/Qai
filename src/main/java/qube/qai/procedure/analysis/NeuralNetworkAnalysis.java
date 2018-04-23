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

package qube.qai.procedure.analysis;

import org.openrdf.annotations.Iri;
import qube.qai.data.Metrics;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.nodes.ValueNode;

import static qube.qai.main.QaiConstants.BASE_URL;

/**
 * Created by zenpunk on 11/28/15.
 */
@Iri(BASE_URL + "NeuralNetworkAnalysis")
public class NeuralNetworkAnalysis extends Procedure implements ProcedureConstants {

    public String NAME = "Neural-Network Analysis";

    public String DESCRIPTION = "Neural-network analysis " +
            "does a statistical analysis of the weights, " +
            "their network structure, etc.";

    @Iri(BASE_URL + "neuralNetwork")
    private NeuralNetwork neuralNetwork;

    @Iri(BASE_URL + "metrics")
    private Metrics metrics;

    public NeuralNetworkAnalysis() {
        super("Neural-Network Analysis");
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<NeuralNetwork>(INPUT_NEURAL_NETWORK, MIMETYPE_NEURAL_NETWORK) {
            @Override
            public void setValue(NeuralNetwork value) {
                super.setValue(value);
                neuralNetwork = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Metrics>(NETWORK_METRICS, MIMETYPE_METRICS) {
            @Override
            public Metrics getValue() {
                return metrics;
            }
        });
    }

    @Override
    public void execute() {

        if (neuralNetwork == null) {
            error("Input neural-network has not been initialized properly: null value");
            return;
        }

        metrics = neuralNetwork.buildMetrics();
        info("adding '" + NETWORK_METRICS + "' to return values");

    }

    @Override
    public Procedure createInstance() {
        return new NeuralNetworkAnalysis();
    }

}
