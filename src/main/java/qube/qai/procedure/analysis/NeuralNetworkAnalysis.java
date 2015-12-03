package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.ProcedureChain;
import qube.qai.procedure.ProcedureFactory;

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
    }

    @Override
    public void run() {

        // we are of course assuming the selector is already initialized
        if (!arguments.isSatisfied()) {
            throw new RuntimeException("Process: " + name + " has not been initialized properly- missing argument");
        }

        // and when we are done,we set the return value
        Selector<Double> result = new DataSelector<Double>(Double.valueOf(0));
        String argumentName = name + ".result";
        arguments.setArgument(argumentName, result);
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
            SelectProcedure selectProcedure = new SelectProcedure();
            neuralNetworkAnalysis.addChild(selectProcedure);

            // change-point analysis of the given time series as well
            ChangePointAnalysis changePointAnalysis = new ChangePointAnalysis();
            selectProcedure.addChild(changePointAnalysis);

            // time-series analysis for the generated data
            TimeSeriesAnalysis timeSeriesAnalysis = new TimeSeriesAnalysis();
            changePointAnalysis.addChild(timeSeriesAnalysis);
            // forward propagation which will be producing the data for time-series analysis
            NeuralNetworkForwardPropagation forwardPropagation = new NeuralNetworkForwardPropagation();
            timeSeriesAnalysis.addChild(forwardPropagation);



            return neuralNetworkAnalysis;
        }
    };
}
