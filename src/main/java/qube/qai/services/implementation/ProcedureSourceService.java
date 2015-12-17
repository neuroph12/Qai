package qube.qai.services.implementation;

import org.joda.time.DateTime;
import org.ojalgo.random.Normal;
import org.ojalgo.random.RandomNumber;
import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.data.TimeSequence;
import qube.qai.data.selectors.DataSelector;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.Network;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.*;
import qube.qai.services.ProcedureSource;

import java.util.*;

/**
 * Created by rainbird on 12/16/15.
 */
public class ProcedureSourceService implements ProcedureSource {

    private int number = 100;
    private int size = 10;
    private String startDate = "2000-1-1";
    private String endDate = "2000-12-31";

    private String[] procedureNames = { ChangePointAnalysis.NAME,
            MatrixStatistics.NAME,
            NetworkStatistics.NAME,
            NeuralNetworkAnalysis.NAME,
            NeuralNetworkForwardPropagation.NAME,
            SortingPercentilesProcedure.NAME,
            TimeSequenceAnalysis.NAME };

    public Procedure getProcedureWithName(String name) {

        Procedure procedure = null;
        Date start = DateTime.parse(startDate).toDate();
        Date end = DateTime.parse(endDate).toDate();

        if (ChangePointAnalysis.NAME.equals(name)) {
            procedure = new ChangePointAnalysis();

            TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(start, end);
            Selector<TimeSequence> selector = new DataSelector<TimeSequence>(timeSequence);
            procedure.getArguments().setArgument(ChangePointAnalysis.INPUT_TIME_SEQUENCE, selector);

        } else if (MatrixStatistics.NAME.equals(name)) {

            procedure = new MatrixStatistics();

            Matrix matrix = Matrix.createMatrix(true, 100, 100);
            Selector<Matrix> selector = new DataSelector<Matrix>(matrix);
            procedure.getArguments().setArgument(MatrixStatistics.INPUT_MATRIX, selector);

        } else if (NetworkStatistics.NAME.equals(name)) {
            procedure = new NetworkStatistics();

            Network network = Network.createTestNetwork();
            Selector<Network> selector = new DataSelector<Network>(network);
            procedure.getArguments().setArgument(NetworkStatistics.INPUT_NETWORK, selector);

        } else if (NeuralNetworkAnalysis.NAME.equals(name)) {

            procedure = new NeuralNetworkAnalysis();

            Matrix matrix = Matrix.createMatrix(true, 100, 100);
            NeuralNetwork network = new NeuralNetwork(matrix);

            Selector<NeuralNetwork> selector = new DataSelector<NeuralNetwork>(network);
            procedure.getArguments().setArgument(NeuralNetworkAnalysis.INPUT_NEURAL_NETWORK, selector);

        } else if (NeuralNetworkForwardPropagation.NAME.equals(name)) {

            procedure = new NeuralNetworkForwardPropagation();
            Arguments arguments = procedure.getArguments();

            Matrix matrix = Matrix.createMatrix(true, size, size);
            NeuralNetwork neuralNetwork = new NeuralNetwork(matrix);
            Selector<NeuralNetwork> networkSelector = new DataSelector<NeuralNetwork>(neuralNetwork);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_NEURAL_NETWORK, networkSelector);

            double[] firstDay = new double[size];
            RandomNumber generator = new Normal(0.5, 0.1);
            for (int i = 0; i < firstDay.length; i++) {
                firstDay[i] = generator.doubleValue();
            }
            Vector startVector = Vector.buildFromArray(firstDay);
            Selector<Vector> startVectorSelector = new DataSelector<Vector>(startVector);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_START_VECTOR, startVectorSelector);

            List<String> names = new ArrayList<String>();
            String[] nameStrings = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eightth", "nineth", "tenth"};
            for (String n : nameStrings) {
                names.add(n);
            }
            Selector<List> namesSelector = new DataSelector<List>(names);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_NAMES, namesSelector);

            Date startDate = DateTime.parse("2015-1-1").toDate();
            Date endDate = DateTime.parse("2015-1-10").toDate();

            List<Date> dates = TimeSequence.createDates(startDate, endDate);;
            Selector<List> stepsSelector = new DataSelector<List>(dates);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_DATES_FOR_STEPS, stepsSelector);

        } else if (SortingPercentilesProcedure.NAME.equals(name)) {

            procedure = new SortingPercentilesProcedure();


            Date startDate = DateTime.parse("2015-1-1").toDate();
            Date endDate = DateTime.now().toDate();

            Map<String, Selector> timeSeriesMap = new HashMap<String, Selector>();
            for (int i = 0; i < number; i++) {
                TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(startDate, endDate);
                Selector<TimeSequence> selector = new DataSelector<TimeSequence>(timeSequence);
                String key = "entity_" + i;
                timeSeriesMap.put(key, selector);
            }

            Selector<Map> collectionSelector = new DataSelector<Map>(timeSeriesMap);
            procedure.getArguments().setArgument(SortingPercentilesProcedure.FROM, collectionSelector);

            // @TODO is this really required in the latest form of the class and what it does?!?
            Selector<String> criteria = new DataSelector<String>("criteria");
            procedure.getArguments().setArgument(SortingPercentilesProcedure.CRITERIA, criteria);

        } else if (TimeSequenceAnalysis.NAME.equals(name)) {

            procedure = new TimeSequenceAnalysis();

            TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(start, end);
            Selector<TimeSequence> selector = new DataSelector<TimeSequence>(timeSequence);

            procedure.getArguments().setArgument(TimeSequenceAnalysis.INPUT_TIME_SEQUENCE, selector);

        }

        return procedure;
    }

    public String[] getProcedureNames() {
        return procedureNames;
    }
}
