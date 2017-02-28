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

package qube.qai.procedure.visitor;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.SelectionProcedure;
import qube.qai.procedure.analysis.MatrixStatistics;
import qube.qai.procedure.analysis.NetworkStatistics;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;

/**
 * Created by rainbird on 12/1/15.
 */
public class TestProcedureVisitors extends TestCase {

    private Logger logger = LoggerFactory.getLogger("TestProcedureVisitors");

    private boolean debug = true;

    public void testSimpleProcedureVisitor() throws Exception {

        // test the simple visitor on network analysis
        SelectionProcedure selection = new SelectionProcedure();
        MatrixStatistics matrix = new MatrixStatistics(selection);
        NetworkStatistics network = new NetworkStatistics(matrix);
        NeuralNetworkAnalysis neural = new NeuralNetworkAnalysis(network);

        SimpleProcedureVisitor visitor = new SimpleProcedureVisitor();
        Object result = neural.accept(visitor, null);
        //assertNotNull("there has to be a result", result);
        logger.info(visitor.toString());
    }

//    private void log(String message) {
//        if (debug) {
//            System.out.println(message);
//        }
//    }

}
