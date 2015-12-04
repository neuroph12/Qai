package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/29/15.
 */
public class NetworkStatistics extends ProcedureChain {

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
    public void run() {

    }
}
