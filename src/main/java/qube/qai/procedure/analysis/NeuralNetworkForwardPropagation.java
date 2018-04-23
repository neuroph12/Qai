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
import qube.qai.data.TimeSequence;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static qube.qai.main.QaiConstants.BASE_URL;

/**
 * Created by zenpunk on 11/28/15.
 */
@Iri(BASE_URL + "NeuralNetworkForwardPropagation")
public class NeuralNetworkForwardPropagation extends Procedure implements ProcedureConstants {

    public String NAME = "Neural-network forward-propagation";

    public String DESCRIPTION = "Calls forward-propagation routine of the " +
            "given neural-network with a given starting matrix to given number of steps.";

    public static String INPUT_START_VECTOR = "start vector";

    public static String INPUT_DATES_FOR_STEPS = "dates for iteration steps";

    /**
     * this takes a neural-network and runs forward-propagation
     * for as many steps as required, persisting the results
     * and making those available for other processes, or
     * for anyone interested
     */
    public NeuralNetworkForwardPropagation() {
        super("Neural-network forward-propagation");
    }


//    public NeuralNetworkForwardPropagation(Procedure procedure) {
//        super(NAME, procedure);
//    }

    @Override
    public void buildArguments() {
        /*getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(INPUT_NEURAL_NETWORK, MIMETYPE_NEURAL_NETWORK));
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(INPUT_START_VECTOR, MIMETYPE_VECTOR));
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(INPUT_START_VECTOR, MIMETYPE_VECTOR));
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(INPUT_NAMES, MIMETYPE_STRING_LIST));
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(INPUT_DATES_FOR_STEPS, MIMETYPE_DATE_LIST));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(MAP_OF_TIME_SEQUENCE, MIMETYPE_TIME_SEQUENCE_MAP));*/
    }

    @Override
    public void execute() {

        NeuralNetwork neuralNetwork = (NeuralNetwork) getInputValueOf(INPUT_NEURAL_NETWORK);
        Vector inputVector = (Vector) getInputValueOf(INPUT_START_VECTOR);
        List<String> names = (List<String>) getInputValueOf(INPUT_NAMES);
        List<Date> dates = (List<Date>) getInputValueOf(INPUT_DATES_FOR_STEPS);

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
                    timeSequence = new TimeSequence(key);
                    timeSeriesMap.put(key, timeSequence);
                }
                timeSequence.add(dates.get(i), outArray[j]);
            }
            in = out;
        }

        //setResultValueOf(MAP_OF_TIME_SEQUENCE, timeSeriesMap);
    }

    @Override
    public Procedure createInstance() {
        return new NeuralNetworkForwardPropagation();
    }
}
