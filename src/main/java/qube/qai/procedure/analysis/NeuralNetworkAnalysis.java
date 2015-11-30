package qube.qai.procedure.analysis;

import qube.qai.data.Selector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.ProcedureChain;
import qube.qai.procedure.ProcedureFactory;

import java.util.Collection;

/**
 * Created by rainbird on 11/28/15.
 */
public class NeuralNetworkAnalysis extends ProcedureChain {

    private Selector<Collection<NeuralNetwork>> networkSelector;

    public NeuralNetworkAnalysis(String name) {
        super(name);
    }

    @Override
    public void run() {

        // we are of course assuming the selector is already initialized

    }



    /**
     * implement a static factory-class so that they can be constructed right
     */
    public static ProcedureFactory Factory = new ProcedureFactory() {
        public ProcedureChain constructProcedure() {

            // tha parent procedure
            NeuralNetworkAnalysis neuralNetworkAnalysis = new NeuralNetworkAnalysis("Neural Network Analysis");

            // begin with basic-matrix statistics
            MatrixStatistics matrixStatistics = new MatrixStatistics("Matrix Statistics");
            neuralNetworkAnalysis.addChild(matrixStatistics);

            // we want to have the statistics of the network which is created
            NetworkStatistics networkStatistics = new NetworkStatistics("Network Statistics");
            neuralNetworkAnalysis.addChild(networkStatistics);

            // time-series analysis for the generated data
            TimeSeriesAnalysis timeSeriesAnalysis = new TimeSeriesAnalysis("Time-Series Analysis");
            neuralNetworkAnalysis.addChild(timeSeriesAnalysis);

            // change-point analysis of the given time series as well
            ChangePointAnalysis changePointAnalysis = new ChangePointAnalysis("Change Point Analysis");
            timeSeriesAnalysis.addChild(changePointAnalysis);

            // forward propagation which will be producing the data for time-series analysis
            NeuralNetworkForwardPropagation forwardPropagation = new NeuralNetworkForwardPropagation("Neural-Network Forward Propagation");
            changePointAnalysis.addChild(forwardPropagation);



            return neuralNetworkAnalysis;
        }
    };
}
