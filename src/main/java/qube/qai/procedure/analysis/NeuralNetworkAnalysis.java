package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.SelectionOperator;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.*;

import java.util.Collection;

/**
 * Created by rainbird on 11/28/15.
 */
public class NeuralNetworkAnalysis extends ProcedureDecorator {

    public static String NAME = "Neural-Network Analysis";

    public static String DESCRIPTION = "Neural-network analysis " +
            "does a statistical analysis of the weights, " +
            "their network structure, etc.";

    private SelectionOperator<Collection<NeuralNetwork>> networkSelectionOperator;

    public NeuralNetworkAnalysis(Procedure procedure) {
        super(procedure);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_NEURAL_NETWORK);
        arguments.putResultNames(NETWORK_METRICS);
    }

    @Override
    public void execute() {

        toDecorate.execute();

        // we are of course assuming the selector is already initialized
        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(toDecorate.getArguments());
        }

        NeuralNetwork neuralNetwork = (NeuralNetwork) arguments.getSelector(INPUT_NEURAL_NETWORK).getData();
        if (neuralNetwork == null) {
            logger.error("Input neural-network has not been initialized properly: null value");
            return;
        }

        Metrics networkMetrics = neuralNetwork.buildMetrics();
        logger.info("adding '" + NETWORK_METRICS + "' and '" + MATRIX_METRICS + "' to return values");
        arguments.addResult(NETWORK_METRICS, networkMetrics);

    }

    /**
     * implement a static factory-class so that they can be constructed right
     */
    public static ProcedureFactory Factory = new ProcedureFactory() {

        public Procedure constructProcedure(SelectionProcedure selection) {

            if (selection == null) {
                selection = new SelectionProcedure();
            }
            MatrixStatistics matrix = new MatrixStatistics(selection);
            NetworkStatistics network = new NetworkStatistics(matrix);
            NeuralNetworkAnalysis neural = new NeuralNetworkAnalysis(network);

            return neural;
        }
    };

}
