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

import qube.qai.data.Metrics;
import qube.qai.network.Network;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.nodes.ValueNode;

/**
 * Created by zenpunk on 11/29/15.
 */
public class NetworkStatistics extends Procedure implements ProcedureConstants {

    public static String NAME = "Network Statistics";

    public static String DESCRIPTION = "Calculates the metrics for the given network";

    private Network network;

    private Metrics metrics;

    /**
     * builds a network from a given matrix and displays
     * the network along with its statistical properties.
     */
    public NetworkStatistics() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<Network>(INPUT_NETWORK, MIMETYPE_NETWORK) {
            @Override
            public void setValue(Network value) {
                network = value;
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

        //network = (Network) getInputValueOf(INPUT_NETWORK);
        if (network == null) {
            error("Input network has not been initialized properly: null value");
            return;
        }

        metrics = network.buildMetrics();
        info("adding " + NETWORK_METRICS + " to return values");
        setResultValueOf(NETWORK_METRICS, metrics);
    }

    @Override
    public Procedure createInstance() {
        return new NetworkStatistics();
    }
}
