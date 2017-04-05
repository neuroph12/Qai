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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Metrics;
import qube.qai.data.TimeSequence;
import qube.qai.main.QaiTestBase;
import qube.qai.matrix.Matrix;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.ProcedureDescription;
import qube.qai.procedure.ValueNode;
import qube.qai.procedure.utils.SimpleProcedure;
import qube.qai.services.ProcedureSourceInterface;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rainbird on 11/30/15.
 */
public class TestAnalysisProcedures extends QaiTestBase {

    Logger logger = LoggerFactory.getLogger("TestAnalysisProcedures");

    @Inject
    private ProcedureSourceInterface procedureSource;

    private boolean debug = true;

    /**
     * try out how the procedure-as input for another
     *
     * @throws Exception
     */
    public void testProcedureInputs() throws Exception {

        SimpleProcedure simple = new SimpleProcedure();
        String name = ProcedureConstants.INPUT_MATRIX;
        Matrix matrix = Matrix.createMatrix(true, 100, 100);
        simple.getProcedureDescription().getProcedureResults().addResult(new ValueNode(name, matrix));

        MatrixStatistics statistics = new MatrixStatistics();
        statistics.getProcedureDescription().getProcedureInputs().getNamedInput(name).setFirstChild(simple);

        statistics.execute();

        Collection<String> resultNames = statistics.getProcedureDescription().getProcedureResults().getResultNames();
        for (String result : resultNames) {
            BaseNode node = statistics.getProcedureDescription().getProcedureResults().getNamedResult(result);
            assertNotNull("there has to be a node with name: " + result, node);
            log("result name: " + result + " " + node.toString());
        }
    }

    /**
     * do the testing for the MatrixAnalysis class
     *
     * @throws Exception
     */
    public void testMatrixStatistics() throws Exception {

        MatrixStatistics statistics = new MatrixStatistics();

        // this methd is called already during initialization (constructor) of the base class- procedure.
        //statistics.buildArguments();
        ProcedureDescription description = statistics.getProcedureDescription();
        assertNotNull("arguments may not be null", description);
        log("description as text: " + description.getDescription());

        BaseNode matrixIn = description.getProcedureInputs().getNamedInput(ProcedureConstants.INPUT_MATRIX);
        assertNotNull("there has to be an input matrix", matrixIn);

        Collection<String> names = description.getProcedureInputs().getInputNames();
        for (String name : names) {
            BaseNode node = description.getProcedureInputs().getNamedInput(name);
            assertNotNull("there has to be a node", node);
            log("input named: " + name + " and corresponding node: " + node.toString());
        }

        Matrix matrix = Matrix.createMatrix(true, 100, 100);
        description.getProcedureInputs().getNamedInput(ProcedureConstants.INPUT_MATRIX).setFirstChild(new ValueNode("", matrix));
        statistics.execute();

        Collection<String> resultNames = description.getProcedureResults().getResultNames();
        for (String name : resultNames) {
            BaseNode node = description.getProcedureResults().getNamedResult(name);
            assertNotNull("there has to be a node with name: " + name, node);
            log("result name: " + name + " " + node.toString());
        }
    }

    public void estChangePointAnalysis() throws Exception {

//        ChangePointAnalysis statistics = (ChangePointAnalysis) procedureSource.getProcedureWithName(ChangePointAnalysis.NAME);
//
//        Arguments arguments = statistics.getArguments();
//        assertNotNull("arguments may not be null", arguments);
//
//        assertTrue("input matrix is one of the arguments", arguments.getArgumentNames().contains(ChangePointAnalysis.INPUT_TIME_SEQUENCE));
//
//        checkResultsOf(statistics);
//
//        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
//        log("results:" + statistics.getArguments().getResultNames());
//        for (String name : statistics.getArguments().getResultNames()) {
//            Object result = statistics.getArguments().getResult(name);
//
//            if (result instanceof Collection) {
//                Collection<ChangePointAnalysis.ChangePointMarker> changePoints = (Collection<ChangePointAnalysis.ChangePointMarker>) result;
//                log("the resulting collection isEmpty(): " + changePoints.isEmpty());
//                for (ChangePointAnalysis.ChangePointMarker marker : changePoints) {
//                    log("Change point at: " + marker.getDate() + " with " + marker.getProbability());
//                }
//            }
//        }
    }

    /**
     * do the testing for the NeuralNetworkForwardPropagation class
     *
     * @throws Exception
     */
    public void estTimeSeriesAnalysis() throws Exception {

//        TimeSequenceAnalysis statistics = (TimeSequenceAnalysis) procedureSource.getProcedureWithName(TimeSequenceAnalysis.NAME);
//
//        Arguments arguments = statistics.getArguments();
//        assertNotNull("arguments may not be null", arguments);
//
//        checkResultsOf(statistics);
//
//        log("results:" + statistics.getArguments().getResultNames());
//        for (String name : statistics.getArguments().getResultNames()) {
//            Object result = statistics.getArguments().getResult(name);
//            if (result instanceof Metrics) {
//                log("found metrics: " + name);
//                log((Metrics) result);
//            } else {
//                log("result: " + result + " value: " + result);
//            }
//        }
    }

    /**
     * do the testing for the NetworkStatistics class
     *
     * @throws Exception
     */
    public void estNetworkStatistics() throws Exception {

//        NetworkStatistics statistics = (NetworkStatistics) procedureSource.getProcedureWithName(NetworkStatistics.NAME);
//
//        Arguments arguments = statistics.getArguments();
//        assertNotNull("arguments may not be null", arguments);
//
//        checkResultsOf(statistics);
//
//        log("results:" + statistics.getArguments().getResultNames());
//        for (String name : statistics.getArguments().getResultNames()) {
//            Object result = statistics.getArguments().getResult(name);
//            if (result instanceof Metrics) {
//                log("found metrics: " + name);
//                log((Metrics) result);
//            } else {
//                log("result: " + result + " value: " + result);
//            }
//        }
    }

    /**
     * do the testing for neural-network analysis
     */
    public void estNeuralNetworkAnalysis() throws Exception {

//        NeuralNetworkAnalysis statistics = (NeuralNetworkAnalysis) procedureSource.getProcedureWithName(NeuralNetworkAnalysis.NAME);
//
//        Arguments arguments = statistics.getArguments();
//        assertNotNull("arguments may not be null", arguments);
//
//        checkResultsOf(statistics);
//
//        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
//        log("results:" + statistics.getArguments().getResultNames());
//        for (String name : statistics.getArguments().getResultNames()) {
//            Object result = statistics.getArguments().getResult(name);
//            if (result instanceof Metrics) {
//                log("found metrics: " + name);
//                log((Metrics) result);
//            } else {
//                log("result: " + result + " value: " + result);
//            }
//        }
    }

    /**
     * do the testing for the NeuralNetworkForwardPropagation class
     *
     * @throws Exception
     */
    public void estNeuralNetworkForwardPropagation() throws Exception {

//        NeuralNetworkForwardPropagation statistics = (NeuralNetworkForwardPropagation) procedureSource.getProcedureWithName(NeuralNetworkForwardPropagation.NAME);
//
//        Arguments arguments = statistics.getArguments();
//        assertNotNull("arguments may not be null", arguments);
//
//        List<String> names = new ArrayList<String>();
//        String[] nameStrings = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eightth", "nineth", "tenth"};
//        for (String n : nameStrings) {
//            names.add(n);
//        }
//
//        checkResultsOf(statistics);
//
//        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
//        log("results:" + statistics.getArguments().getResultNames());
//        Map<String, TimeSequence> timeSeriesMap = (Map<String, TimeSequence>) arguments.getResult(NeuralNetworkForwardPropagation.MAP_OF_TIME_SEQUENCE);
//        assertNotNull("time series map cannot be null", timeSeriesMap);
//
//        for (String name : names) {
//            TimeSequence timeSequence = timeSeriesMap.get(name);
//            assertNotNull("time series for: " + name + " may not be null", timeSequence);
//            log("time-series for: " + name + ": (" + t2String(timeSequence) + ")");
//        }
    }


    /**
     * do the testing for the SortingPercentilesProcedure class
     *
     * @throws Exception
     */
    public void estSortingPercentilesProcedure() throws Exception {

//        SortingPercentilesProcedure statistics = (SortingPercentilesProcedure) procedureSource.getProcedureWithName(SortingPercentilesProcedure.NAME);
//
//        Arguments arguments = statistics.getArguments();
//        assertNotNull("arguments may not be null", arguments);
//
//        checkResultsOf(statistics);
//
//        assertTrue("there has to be some results", !statistics.getArguments().getResultNames().isEmpty());
//        log("results:" + statistics.getArguments().getResultNames());
//        for (String name : statistics.getArguments().getResultNames()) {
//            Object result = statistics.getArguments().getResult(name);
//            int rank = 1;
//            if (result instanceof Map) {
//                Map<String, Statistics> statisticsMap = (Map<String, Statistics>) result;
//                for (String key : statisticsMap.keySet()) {
//                    Statistics stats = statisticsMap.get(key);
//                    log("stats: " + key + " average: " + stats.getAverage() + " with rank: " + rank);
//                    rank++;
//                }
//            } else if (result instanceof TimeSequence) {
//                log("The average time series: " + ((TimeSequence) result).toArray());
//            }
//        }
    }

    /**
     * convert the sequence to a string
     *
     * @param series
     * @return
     */
    private String t2String(TimeSequence series) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<Number> it = series.iterator(); it.hasNext(); ) {
            Number number = it.next();
            buffer.append(number.doubleValue());
            buffer.append(",");
        }

        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * run the given procedure and check that every
     * result parameter mentioned in the procedure
     * is actually to be found in the arguments
     *
     * @param statistics
     */
    private void checkResultsOf(Procedure statistics) {

//        long start = System.currentTimeMillis();
//
//        statistics.run();
//        long duration = System.currentTimeMillis() - start;
//
//        log("Process: " + statistics.getName() + " took " + duration + "ms");
//
//        assertTrue("there has to be result names", !statistics.getArguments().getResultNames().isEmpty());
//        // assert also that the given results can actually be accessed
//        for (String resultName : statistics.getArguments().getResultNames()) {
//            assertTrue("result: '" + resultName + "' missing", statistics.getArguments().getResult(resultName) != null);
//        }
    }

    private void log(Metrics metrics) {
        if (debug) {
            for (String name : metrics.getNames()) {
                String line = name + ": " + metrics.getValue(name);
                System.out.println(line);
            }
        }
    }

}
