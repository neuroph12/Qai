package qube.qai.network.neural.trainer;

import junit.framework.TestCase;
import org.encog.ml.data.MLDataPair;
import org.joda.time.DateTime;
import qube.qai.data.TimeSequence;
import qube.qai.matrix.Matrix;
import qube.qai.network.neural.NeuralNetwork;

import java.util.*;

/**
 * Created by rainbird on 12/13/15.
 */
public class TestNeuralNetworkTraining extends TestCase {

    private boolean debug = true;

    public void testNetworkTraining() throws Exception {
        String[] names = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "nineth", "tenth"};
        Map<String, TimeSequence> timeSeriesMap = createTimeSeriesMap(names);
        assertNotNull(timeSeriesMap);

        NeuralNetwork network = new NeuralNetwork(names.length);
        BasicNetworkTrainer trainer = new BasicNetworkTrainer(network);
        List<Date> dates = createDates();
        Map<Date, double[]> dataSet = trainer.spliceToDates(dates, timeSeriesMap);
        // create the network and train the thing- remember this is 10 random entities
        trainer.createTrainingSet(dates, dataSet);

        trainer.trainNetwork();
        for(MLDataPair pair: trainer.getTrainingSet()) {
            double[] output = network.propagate(pair.getInput().getData());
            for (int i = 0; i < output.length; i++) {
                log("entity name: " + names[i]);
                log("input: " + pair.getInput().getData(i));
                log("output: " + output[i]);
                log("ideal: " + pair.getIdeal().getData(i));
            }
        }

        // now we want to see the adjacency-matrix values
        network.buildAdjacencyMatrix();
        Matrix adjacencyMatrix = network.getAdjacencyMatrix();
        assertNotNull(adjacencyMatrix);
        log("adjacency matrix after training: ");
        log(adjacencyMatrix.toString());
    }

    private List<Date> createDates() {
        Date start = DateTime.parse("2015-1-1").toDate();
        Date end = DateTime.parse("2015-12-31").toDate();
        return TimeSequence.createDates(start, end);
    }

    private Map<String, TimeSequence> createTimeSeriesMap(String... names) {
        Map<String, TimeSequence> timeSeriesMap = new HashMap<String, TimeSequence>();
        for (String name : names) {
            Date start = DateTime.parse("2015-1-1").toDate();
            Date end = DateTime.parse("2015-12-31").toDate();
            TimeSequence timeSequence = TimeSequence.createTimeSeries(start, end);
            timeSeriesMap.put(name, timeSequence);
        }
        return timeSeriesMap;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
