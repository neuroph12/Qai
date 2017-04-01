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
import qube.qai.network.Network;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;

/**
 * Created by rainbird on 11/29/15.
 */
public class NetworkStatistics extends Procedure implements ProcedureConstants {

    public static String NAME = "Network Statistics";

    public static String DESCRIPTION = "Calculates the metrics for the given network";

    /**
     * builds a network from a given matrix and displays
     * the network along with its statistical properties.
     */
    public NetworkStatistics() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_NETWORK);
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

        Network network = (Network) arguments.getSelector(INPUT_NETWORK).getData();
        if (network == null) {
            logger.error("Input network has not been initialized properly: null value");
            return;
        }

        Metrics metrics = network.buildMetrics();
        logger.info("adding " + NETWORK_METRICS + " to return values");
        arguments.addResult(NETWORK_METRICS, metrics);
    }

    @thewebsemantic.Id
    public String getUuid() {
        return this.uuid;
    }
}
