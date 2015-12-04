package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
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
    }
}
