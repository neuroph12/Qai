package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.TimeSeries;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.ProcedureChain;

import java.util.*;

/**
 * Created by rainbird on 11/28/15.
 */
public class NeuralNetworkForwardPropagation extends ProcedureChain {

    public static String NAME = "Neural-Network Forward-Propagation";

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
    public NeuralNetworkForwardPropagation() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_NEURAL_NETWORK, INPUT_START_VECTOR, INPUT_NAMES, INPUT_DATES_FOR_STEPS);
        arguments.putResultNames(MAP_OF_TIME_SERIES);
    }

    @Override
    public void run() {

        if (!arguments.isSatisfied()) {
            throw new RuntimeException("Process: " + name + " has not been initialized properly- missing argument");
        }

        NeuralNetwork neuralNetwork = (NeuralNetwork) arguments.getSelector(INPUT_NEURAL_NETWORK).getData();
        Vector inputVector = (Vector) arguments.getSelector(INPUT_START_VECTOR).getData();
        List<String> names = (List<String>) arguments.getSelector(INPUT_NAMES).getData();
        List<Date> dates = (List<Date>) arguments.getSelector(INPUT_DATES_FOR_STEPS).getData();

        // the time-series generated should be assigned to the named entities they represent
        Map<String, TimeSeries> timeSeriesMap = new HashMap<String, TimeSeries>();
        Vector in = inputVector;
        for (int i = 0; i < dates.size(); i++) {
            // generate day's output
            Vector out = neuralNetwork.propagate(in);
            double[] outArray = out.toArray();
            // append the result in each entity's time-series
            for (int j = 0; j < outArray.length; j++) {
                String key = names.get(j);
                TimeSeries timeSeries = timeSeriesMap.get(key);
                if (timeSeries == null) {
                    timeSeries = new TimeSeries();
                    timeSeriesMap.put(key, timeSeries);
                }
                timeSeries.add(dates.get(i), outArray[j]);
            }
            in = out;
        }

        arguments.addResult(MAP_OF_TIME_SERIES, timeSeriesMap);

    }
}
