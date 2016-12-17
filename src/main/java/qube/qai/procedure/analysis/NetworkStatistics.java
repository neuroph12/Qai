package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.network.Network;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureDecorator;

/**
 * Created by rainbird on 11/29/15.
 */
public class NetworkStatistics extends ProcedureDecorator {

    public static String NAME = "Network Statistics";

    public static String DESCRIPTION = "Calculates the metrics for the given network";

    /**
     * builds a network from a given matrix and displays
     * the network along with its statistical properties.
     */
    public NetworkStatistics(Procedure procedure) {
        super(procedure);
    }

    @Override
    public void buildArguments() {
        name = NAME;
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_NETWORK);
        arguments.putResultNames(NETWORK_METRICS);
    }

    @Override
    public void execute() {

        toDecorate.execute();

        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(toDecorate.getArguments());
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
}
