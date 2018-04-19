/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

import qube.qai.data.TimeSequence;
import qube.qai.matrix.Matrix;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.nodes.ProcedureDescription;
import qube.qai.procedure.nodes.ProcedureTestBase;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.procedure.utils.SelectForEach;
import qube.qai.procedure.utils.SimpleProcedure;

import java.util.Iterator;

/**
 * Created by rainbird on 11/30/15.
 */
public class AnalysisProceduresTest extends ProcedureTestBase {

    //Logger logger = LoggerFactory.getLogger("AnalysisProceduresTest");

    //@Inject
    //private ProcedureSourceInterface procedureSource;

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

        fail("the rest of the test should be implemented");

        //checkProcedureResults(statistics.getProcedureDescription());
    }

    /**
     * do the testing for the MatrixAnalysis class
     *
     * @throws Exception
     */
    public void testMatrixStatistics() throws Exception {

        MatrixStatistics procedure = new MatrixStatistics();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("arguments may not be null", description);
        log("description as text: " + description.getDescription());

        BaseNode matrixIn = description.getProcedureInputs().getNamedInput(ProcedureConstants.INPUT_MATRIX);
        assertNotNull("there has to be an input matrix", matrixIn);

        //checkProcedureInputs(description);

        Matrix matrix = Matrix.createMatrix(true, 100, 100);
        description.getProcedureInputs().getNamedInput(ProcedureConstants.INPUT_MATRIX).setFirstChild(new ValueNode("", matrix));
        procedure.execute();

        fail("the rest of the test should be implemented");

        //checkProcedureResults(description);
    }


    public void testChangePointAnalysis() throws Exception {

        SelectForEach procedure = ProcedureLibrary.changePointAnalysisTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        fail("the rest of the test should be implemented");

        //checkProcedureInputs(description);

        //checkProcedureResults(description);

    }

    /**
     * do the testing for the NeuralNetworkForwardPropagation class
     *
     * @throws Exception
     */
    public void testTimeSeriesAnalysis() throws Exception {

        TimeSequenceAnalysis procedure = new TimeSequenceAnalysis();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        fail("the rest of the test should be implemented");

        //checkProcedureInputs(description);

        //checkProcedureResults(description);

        String[] quoteNames = {"GOOG", "APP", "whatever..."};

        fail("test appears to be incomplete");
    }

    /**
     * do the testing for the NetworkStatistics class
     *
     * @throws Exception
     */
    public void testNetworkStatistics() throws Exception {

        NetworkStatistics procedure = new NetworkStatistics();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        fail("the rest of the test should be implemented");

        //checkProcedureInputs(description);

        //checkProcedureResults(description);
    }

    /**
     * do the testing for neural-network analysis
     */
    public void testNeuralNetworkAnalysis() throws Exception {

        NeuralNetworkAnalysis procedure = new NeuralNetworkAnalysis();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        fail("the rest of the test should be implemented");

        //checkProcedureInputs(description);

        //checkProcedureResults(description);
    }

    /**
     * do the testing for the NeuralNetworkForwardPropagation class
     *
     * @throws Exception
     */
    public void testNeuralNetworkForwardPropagation() throws Exception {

        NeuralNetworkForwardPropagation procedure = new NeuralNetworkForwardPropagation();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        fail("the rest of the test should be implemented");

        //checkProcedureInputs(description);

        //checkProcedureResults(description);
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
     * do the testing for the SortPercentiles class
     *
     * @throws Exception
     */
    public void testSortingPercentilesProcedure() throws Exception {

        SortPercentiles procedure = new SortPercentiles();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        fail("the rest of the test should be implemented");

        //checkProcedureInputs(description);

        // checkProcedureResults(description);

        // now we do the real testing here-
        // we're supposed to pass a set of stock-entities which it should be working on
        // and visualize the results in a time-series chart.
    }

    /**
     * convert the sequence to a string
     *
     * @param series
     * @return
     */
    private String t2String(TimeSequence series) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<Double> it = series.iterator(); it.hasNext(); ) {
            Number number = it.next();
            buffer.append(number.doubleValue());
            buffer.append(",");
        }

        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

}
