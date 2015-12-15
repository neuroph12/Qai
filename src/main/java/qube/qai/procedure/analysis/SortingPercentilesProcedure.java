package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.data.Statistics;
import qube.qai.data.TimeSeries;
import qube.qai.procedure.ProcedureChain;

import java.util.*;

/**
 * Created by rainbird on 12/2/15.
 */
public class SortingPercentilesProcedure extends ProcedureChain {

    public static String NAME = "Sorting Percentiles Procedure";

    public static String DESCRIPTION = "Selects out the specified items out of a given collection" +
            "but for the beginning, it will be taking time-series, and sorting them in order of their mean" +
            "values. top and bottom ten and the mean will be the results.";

    public SortingPercentilesProcedure() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(FROM, CRITERIA);
        arguments.putResultNames(SELECTED_ITEMS, AVERAGE_TIME_SERIES);
    }

    @Override
    public void run() {

        // we are of course assuming the selector is already initialized
        if (!arguments.isSatisfied()) {
            throw new RuntimeException("Process: " + name + " has not been initialized properly- missing argument");
        }

        Map<String, Selector> timeSeriesMap = (Map<String, Selector>) arguments.getSelector(FROM).getData();
        Map<String, Statistics> statisticsMap = new TreeMap<String, Statistics>();

        Date[] dates = null;
        Number[] averages = null;
        int count = 0;
        // now do the actual Statistics calculation for each entry
        for (String name : timeSeriesMap.keySet()) {

            logger.info("Now calculating the statistics of: " + name + " the " + count + "th entry");

            TimeSeries timeSeries = (TimeSeries) timeSeriesMap.get(name).getData();
            Number[] values = timeSeries.toArray();

            // keep copy of the first items to be used later
            // and add the slots in the first kept array on the later round
            if (count == 0) {
                dates = timeSeries.toDates();
                averages = timeSeries.toArray();
            } else {
                for (int i = 0; i < values.length; i++) {
                    averages[i] = averages[i].doubleValue() + values[i].doubleValue();
                }
            }

            // now do the statistics calculation
            Statistics stats = new Statistics(values);
            stats.calculate();
            statisticsMap.put(name, stats);

            count++;
        }

        // now divide each element with the number of time-series to find their average
        TimeSeries<Double> averageSeries = new TimeSeries<Double>();
        double factor = timeSeriesMap.size();
        for (int i = 0; i < averages.length; i++) {
            averages[i] = averages[i].doubleValue() / factor;
            if (dates.length > i) {
                averageSeries.add(dates[i], averages[i].doubleValue());
            }
        }

        // after all is done copy the map in a fresh one whole it gets sorted
        Map<String, Statistics> sortedTimeSeries = new TreeMap<String, Statistics>(new MapSorter(statisticsMap));
        sortedTimeSeries.putAll(statisticsMap);
        // and add the results to the arguments
        arguments.addResult(SELECTED_ITEMS, sortedTimeSeries);
        arguments.addResult(AVERAGE_TIME_SERIES, averageSeries);
    }

    /**
     * class for sorting the map entities after their
     * statistical averages
     */
    class MapSorter implements Comparator<String> {

        Map<String, Statistics> map;

        public MapSorter(Map<String, Statistics> map) {
            this.map = map;
        }

        public int compare(String key1, String key2) {
            Statistics stats1 = map.get(key1);
            Statistics stats2 = map.get(key2);
            Double val1 = stats1.getAverage();
            Double val2 = stats2.getAverage();
            return val2.compareTo(val1);
        }
    }
}
