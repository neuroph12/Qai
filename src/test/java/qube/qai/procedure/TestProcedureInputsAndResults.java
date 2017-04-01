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

package qube.qai.procedure;

import junit.framework.TestCase;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.Name;
import qube.qai.parsers.antimirov.nodes.Node;
import qube.qai.parsers.antimirov.nodes.PrimitiveNode;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.analysis.MatrixStatistics;
import qube.qai.procedure.analysis.NetworkStatistics;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;
import qube.qai.procedure.utils.SimpleProcedure;

/**
 * Created by rainbird on 3/30/17.
 */
public class TestProcedureInputsAndResults extends TestCase {

    public void estProcedureInputs() throws Exception {
        ProcedureInputs inputs = new ProcedureInputs();
        inputs.addInput(createNamedNode("foo", "integer"));
        inputs.addInput(createNamedNode("baz", "integer"));
        inputs.addInput(createNamedNode("bar", "double"));
        inputs.addInput(createNamedNode("rad", "double"));

        log(inputs.toString());
        assertEquals("foo[integer] baz[integer] bar[double] rad[double]", inputs.toString());

    }

    public void estProcedureResults() throws Exception {

        ProcedureResults results = new ProcedureResults();
        results.addResult(createNamedNode("foo", "integer"));
        results.addResult(createNamedNode("baz", "integer"));
        results.addResult(createNamedNode("bar", "double"));
        results.addResult(createNamedNode("rad", "double"));

        log(results.toString());
        assertEquals("foo[integer] baz[integer] bar[double] rad[double]", results.toString());
    }

    private BaseNode createNamedNode(String name, String type) {
        BaseNode node = null;
        if (type != null && type.length() > 0) {
            node = new Node(new Name(name), new PrimitiveNode((new Name(type))));
        } else {
            node = new Node(new Name(name));
        }

        return node;
    }

    public void testInputPararmeters() throws Exception {
        ProcedureInputs inputs = new ProcedureInputs();

        inputs.addInput(new SimpleProcedure());
        inputs.addInput(new ChangePointAnalysis());
        inputs.addInput(new MatrixStatistics(null));
        inputs.addInput(new NetworkStatistics());
        inputs.addInput(new NeuralNetworkAnalysis());

        Procedure p1 = inputs.getNamedInput(SimpleProcedure.NAME);
        assertNotNull("there has to be a procedure", p1);
        assertTrue("the name has to be right", SimpleProcedure.NAME.equals(p1.getName().getName()));

        Procedure p2 = inputs.getNamedInput(ChangePointAnalysis.NAME);
        assertNotNull("there has to be a procedure", p2);
        assertTrue("the name has to be right", ChangePointAnalysis.NAME.equals(p2.getName().getName()));

        Procedure p3 = inputs.getNamedInput(MatrixStatistics.NAME);
        assertNotNull("there has to be a procedure", p3);
        assertTrue("the name has to be right", MatrixStatistics.NAME.equals(p3.getName().getName()));

        Procedure p4 = inputs.getNamedInput(NetworkStatistics.NAME);
        assertNotNull("there has to be a procedure", p4);
        assertTrue("the name has to be right", NetworkStatistics.NAME.equals(p4.getName().getName()));

        Procedure p5 = inputs.getNamedInput(NeuralNetworkAnalysis.NAME);
        assertNotNull("there has to be a procedure", p5);
        assertTrue("the name has to be right", NeuralNetworkAnalysis.NAME.equals(p5.getName().getName()));

        Procedure dummy = inputs.getNamedInput("quapil");
        assertTrue("there is no dummy procedure to be found", dummy == null);
    }

    private void log(String message) {
        System.out.println(message);
    }
}
