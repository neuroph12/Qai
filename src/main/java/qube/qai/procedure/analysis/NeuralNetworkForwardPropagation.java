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
import qube.qai.data.TimeSequence;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureDecorator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 11/28/15.
 */
public class NeuralNetworkForwardPropagation extends ProcedureDecorator {

    public static String NAME = "Neural-network forward-propagation";

    public static String DESCRIPTION = "Calls forward-propagation routine of the " +
            "given neural-network with a given starting matrix to given number of steps.";

    public static String INPUT_START_VECTOR = "start vector";

    public static String INPUT_DATES_FOR_STEPS = "dates for iteration steps";

    /**
     * this takes a neural-network and runs forward-propagation
     * for as many steps as required, persisting the results
     * and making those available for other processes, or
     * for anyone interested
     */
    public NeuralNetworkForwardPropagation(Procedure procedure) {
        super(NAME, procedure);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_NEURAL_NETWORK, INPUT_START_VECTOR, INPUT_NAMES, INPUT_DATES_FOR_STEPS);
        arguments.putResultNames(MAP_OF_TIME_SEQUENCE);
    }

    @Override
    public void execute() {

        toDecorate.execute();

        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(toDecorate.getArguments());
        }

        NeuralNetwork neuralNetwork = (NeuralNetwork) arguments.getSelector(INPUT_NEURAL_NETWORK).getData();
        Vector inputVector = (Vector) arguments.getSelector(INPUT_START_VECTOR).getData();
        List<String> names = (List<String>) arguments.getSelector(INPUT_NAMES).getData();
        List<Date> dates = (List<Date>) arguments.getSelector(INPUT_DATES_FOR_STEPS).getData();

        // the time-series generated should be assigned to the named entities they represent
        Map<String, TimeSequence> timeSeriesMap = new HashMap<String, TimeSequence>();
        Vector in = inputVector;
        for (int i = 0; i < dates.size(); i++) {
            // generate day's output
            Vector out = neuralNetwork.propagate(in);
            double[] outArray = out.toArray();
            // append the result in each entity's time-series
            for (int j = 0; j < outArray.length; j++) {
                String key = names.get(j);
                TimeSequence timeSequence = timeSeriesMap.get(key);
                if (timeSequence == null) {
                    timeSequence = new TimeSequence();
                    timeSeriesMap.put(key, timeSequence);
                }
                timeSequence.add(dates.get(i), outArray[j]);
            }
            in = out;
        }

        arguments.addResult(MAP_OF_TIME_SEQUENCE, timeSeriesMap);

    }
}
