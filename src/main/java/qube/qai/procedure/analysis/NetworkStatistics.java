package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/29/15.
 */
public class NetworkStatistics extends ProcedureChain {

    public static String NAME = "Network Statistics";

    /**
     * builds a network from a given matrix and displays
     * the network along with its statistical properties.
     */
    public NetworkStatistics() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments(INPUT_NETWORK);
    }

    @Override
    public void run() {

    }
}
