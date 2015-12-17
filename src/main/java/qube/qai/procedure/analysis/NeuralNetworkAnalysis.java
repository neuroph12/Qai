package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.Selector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureChain;
import qube.qai.procedure.ProcedureFactory;
import qube.qai.procedure.ProcedureVisitor;

import java.util.Collection;

/**
 * Created by rainbird on 11/28/15.
 */
public class NeuralNetworkAnalysis extends ProcedureChain {

    public static String NAME = "Neural-Network Analysis";

    public static String DESCRIPTION = "Neural-network analysis " +
            "does a statistical analysis of the weights, " +
            "their network structure, etc.";

    private Selector<Collection<NeuralNetwork>> networkSelector;

    public NeuralNetworkAnalysis() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_NEURAL_NETWORK);
        arguments.putResultNames(NETWORK_METRICS);
    }

    @Override
    public void execute() {

        // we are of course assuming the selector is already initialized
        if (!arguments.isSatisfied()) {
            throw new RuntimeException("Process: " + name + " has not been initialized properly- missing argument");
        }

        NeuralNetwork neuralNetwork = (NeuralNetwork) arguments.getSelector(INPUT_NEURAL_NETWORK).getData();
        if (neuralNetwork == null) {
            logger.error("Input neural-network has not been initialized properly: null value");
            return;
        }

        Metrics networkMetrics = neuralNetwork.buildMetrics();
        log("adding '" + NETWORK_METRICS + "' and '" + MATRIX_METRICS + "' to return values");
        arguments.addResult(NETWORK_METRICS, networkMetrics);

    }

    /**
     * implement a static factory-class so that they can be constructed right
     */
    public static ProcedureFactory Factory = new ProcedureFactory() {
        public ProcedureChain constructProcedure() {

            // tha parent procedure
            NeuralNetworkAnalysis neuralNetworkAnalysis = new NeuralNetworkAnalysis();

            // begin with basic-matrix statistics
            MatrixStatistics matrixStatistics = new MatrixStatistics();
            neuralNetworkAnalysis.addChild(matrixStatistics);

            // we want to have the statistics of the network which is created
            NetworkStatistics networkStatistics = new NetworkStatistics();
            neuralNetworkAnalysis.addChild(networkStatistics);

            // select only some of the results which we have
            SortingPercentilesProcedure selectProcedure = new SortingPercentilesProcedure();
            neuralNetworkAnalysis.addChild(selectProcedure);

            // change-point analysis of the given time series as well
            ChangePointAnalysis changePointAnalysis = new ChangePointAnalysis();
            selectProcedure.addChild(changePointAnalysis);

            // time-series analysis for the generated data
            TimeSequenceAnalysis timeSequenceAnalysis = new TimeSequenceAnalysis();
            changePointAnalysis.addChild(timeSequenceAnalysis);
            // forward propagation which will be producing the data for time-series analysis
            NeuralNetworkForwardPropagation forwardPropagation = new NeuralNetworkForwardPropagation();
            timeSequenceAnalysis.addChild(forwardPropagation);



            return neuralNetworkAnalysis;
        }
    };

    /**
     * this is in fact an experiment- has though a few
     * advantages to it, although might seem somewhat
     * unusual- handle each type which you have in the chain
     * the way it should be, and still have access to the fields
     * of the parent-procedure nevertheless
     */
    class NeuralNetworkAnalysisVisitor implements ProcedureVisitor {
        /**
         * this method is nice and will not be called
         * @param procedure
         * @param data
         * @return
         */
        public Object visit(Procedure procedure, Object data) {
            return null;
        }

        public Object visit(MatrixStatistics procedure, Object data) {

            return data;
        }

        public Object visit(NetworkStatistics procedure, Object data) {

            return data;
        }

        public Object visit(SortingPercentilesProcedure procedure, Object data) {

            return data;
        }

        public Object visit(ChangePointAnalysis procedure, Object data) {

            return data;
        }

        public Object visit(NeuralNetworkForwardPropagation procedure, Object data) {

            return data;
        }

        public Object visit(TimeSequenceAnalysis procedure, Object data) {

            return data;
        }
    }
}
