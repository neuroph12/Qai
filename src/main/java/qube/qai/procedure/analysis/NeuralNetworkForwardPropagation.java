package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.TimeSeries;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/28/15.
 */
public class NeuralNetworkForwardPropagation extends ProcedureChain {

    public static String NAME = "Neural-Network Forward-Propagation";

    public static String DESCRIPTION = "Calls forward-propagation routine of the " +
            "given neural-network with a given starting matrix to given number of steps.";

    public static String INPUT_START_VECTOR = "start vector";

    public static String INPUT_NUMBER_OF_STEPS = "number of iteration steps";

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
        arguments = new Arguments(INPUT_NEURAL_NETWORK, INPUT_START_VECTOR, INPUT_NUMBER_OF_STEPS);
        arguments.putResultNames(TIME_SERIES);
    }

    @Override
    public void run() {

        if (!arguments.isSatisfied()) {
            throw new RuntimeException("Process: " + name + " has not been initialized properly- missing argument");
        }

        NeuralNetwork neuralNetwork = (NeuralNetwork) arguments.getSelector(INPUT_NEURAL_NETWORK).getData();
        TimeSeries timeSeries = (TimeSeries) arguments.getSelector(INPUT_START_VECTOR).getData();
        int numberOfIterations = (Integer) arguments.getSelector(INPUT_NUMBER_OF_STEPS).getData();

        Vector in = new Vector();
        for (int i = 0; i < numberOfIterations; i++) {
            Vector out = neuralNetwork.propagate(in);
            in = out;
        }


    }
}
