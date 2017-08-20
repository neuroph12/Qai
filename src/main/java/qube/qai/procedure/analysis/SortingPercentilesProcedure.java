/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.procedure.analysis;

import qube.qai.data.SelectionOperator;
import qube.qai.data.TimeSequence;
import qube.qai.data.analysis.Statistics;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.nodes.ValueNode;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by rainbird on 12/2/15.
 */
public class SortingPercentilesProcedure extends Procedure implements ProcedureConstants {

    public static String NAME = "Sorting Percentiles Procedure";

    public static String DESCRIPTION = "Selects out the specified items out of a given collection" +
            "but for the beginning, it will be taking time-series, and sorting them in order of their mean" +
            "values. top and bottom ten and the mean will be the results.";

    /**
     * Selects out the specified items out of a given collection
     * but for the beginning, it will be taking time-series, and sorting them in order of their mean
     * values. top and bottom ten and the mean will be the results.
     */
    public SortingPercentilesProcedure() {
        super(NAME);
    }


    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(FROM, MIMETYPE_TIME_SEQUENCE_MAP));
        //getProcedureDescription().getProcedureInputs().addInput(new ValueNode(CRITERIA));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(SORTED_ITEMS, MIMETYPE_TIME_SEQUENCE_MAP));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(AVERAGE_TIME_SEQUENCE, MIMETYPE_TIME_SERIES));
//        arguments = new Arguments(FROM, CRITERIA);
//        arguments.putResultNames(SORTED_ITEMS, AVERAGE_TIME_SEQUENCE);
    }

    @Override
    public void execute() {

        executeInputProcedures();

        Map<String, SelectionOperator> timeSeriesMap = (Map<String, SelectionOperator>) getInputValueOf(FROM);
        Map<String, Statistics> statisticsMap = new TreeMap<String, Statistics>();

        Date[] dates = null;
        Number[] averages = null;
        int count = 0;
        // now do the actual Statistics calculation for each entry
        for (String name : timeSeriesMap.keySet()) {

            debug("Now calculating the statistics of: " + name + " the " + count + "th entry");

            TimeSequence timeSequence = (TimeSequence) timeSeriesMap.get(name).getData();
            Number[] values = timeSequence.toArray();

            // keep copy of the first items to be used later
            // and add the slots in the first kept array on the later round
            if (count == 0) {
                dates = timeSequence.toDates();
                averages = timeSequence.toArray();
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
        TimeSequence<Double> averageSeries = new TimeSequence<Double>();
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
        //arguments.addResult(SORTED_ITEMS, sortedTimeSeries);
        //arguments.addResult(AVERAGE_TIME_SEQUENCE, averageSeries);
        setResultValueOf(SORTED_ITEMS, sortedTimeSeries);
        setResultValueOf(AVERAGE_TIME_SEQUENCE, sortedTimeSeries);
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
