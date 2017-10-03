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

package qube.qai.procedure.nodes;

import junit.framework.TestCase;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.analysis.MatrixStatistics;
import qube.qai.procedure.analysis.NetworkStatistics;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;
import qube.qai.procedure.utils.SimpleProcedure;

import java.util.Collection;

/**
 * Created by rainbird on 3/30/17.
 */
public class ProcedureInputsAndResultsTest extends TestCase {

    public void testProcedureInputs() throws Exception {
        ProcedureInputs inputs = new ProcedureInputs();
        inputs.addInput(createNamedNode("foo"));
        inputs.addInput(createNamedNode("baz"));
        inputs.addInput(createNamedNode("bar"));
        inputs.addInput(createNamedNode("rad"));

        log(inputs.toString());
        //assertEquals("foo[integer] baz[integer] bar[double] rad[double]", inputs.toString());

    }

    public void testProcedureResults() throws Exception {

        ProcedureResults results = new ProcedureResults();
        results.addResult(createNamedNode("foo"));
        results.addResult(createNamedNode("baz"));
        results.addResult(createNamedNode("bar"));
        results.addResult(createNamedNode("rad"));

        log(results.toString());

        ValueNode n1 = results.getNamedResult("foo");
        assertNotNull("there has to be foo node", n1);
        assertTrue("nodes must be equal", "foo".equals(n1.getName().getName()));

        ValueNode n2 = results.getNamedResult("baz");
        assertNotNull("there has to be baz node", n2);
        assertTrue("nodes must be equal", "baz".equals(n2.getName().getName()));

        ValueNode n3 = results.getNamedResult("bar");
        assertNotNull("there has to be bar node", n3);
        assertTrue("nodes must be equal", "bar".equals(n3.getName().getName()));

        ValueNode n4 = results.getNamedResult("rad");
        assertNotNull("there has to be rad node", n4);
        assertTrue("nodes must be equal", "rad".equals(n4.getName().getName()));

        ValueNode quapil = results.getNamedResult("quapil");
        assertTrue("there is no quapil- of course...", quapil == null);
    }

    private ValueNode createNamedNode(String name) {
        ValueNode node = new ValueNode(name);
        return node;
    }

    public void testInputParameters() throws Exception {
        ProcedureInputs inputs = new ProcedureInputs();

        inputs.addInput(new ValueNode(SimpleProcedure.NAME, new SimpleProcedure()));
        inputs.addInput(new ValueNode(ChangePointAnalysis.NAME, new ChangePointAnalysis()));
        inputs.addInput(new ValueNode(MatrixStatistics.NAME, new MatrixStatistics()));
        inputs.addInput(new ValueNode(NetworkStatistics.NAME, new NetworkStatistics()));
        inputs.addInput(new ValueNode(NeuralNetworkAnalysis.NAME, new NeuralNetworkAnalysis()));

        Procedure p1 = (Procedure) inputs.getNamedInput(SimpleProcedure.NAME).getFirstChild();
        assertNotNull("there has to be a procedure", p1);
        assertTrue("the name has to be right", SimpleProcedure.NAME.equals(p1.getName().getName()));

        Procedure p2 = (Procedure) inputs.getNamedInput(ChangePointAnalysis.NAME).getFirstChild();
        assertNotNull("there has to be a procedure", p2);
        assertTrue("the name has to be right", ChangePointAnalysis.NAME.equals(p2.getName().getName()));

        Procedure p3 = (Procedure) inputs.getNamedInput(MatrixStatistics.NAME).getFirstChild();
        assertNotNull("there has to be a procedure", p3);
        assertTrue("the name has to be right", MatrixStatistics.NAME.equals(p3.getName().getName()));

        Procedure p4 = (Procedure) inputs.getNamedInput(NetworkStatistics.NAME).getFirstChild();
        assertNotNull("there has to be a procedure", p4);
        assertTrue("the name has to be right", NetworkStatistics.NAME.equals(p4.getName().getName()));

        Procedure p5 = (Procedure) inputs.getNamedInput(NeuralNetworkAnalysis.NAME).getFirstChild();
        assertNotNull("there has to be a procedure", p5);
        assertTrue("the name has to be right", NeuralNetworkAnalysis.NAME.equals(p5.getName().getName()));

        ValueNode quapil = inputs.getNamedInput("quapil");
        assertTrue("quapil has to be null", quapil == null);

        Collection<String> names = inputs.getInputNames();
        assertNotNull("names may not be null", names);
        assertTrue(names.contains(SimpleProcedure.NAME));
        assertTrue(names.contains(ChangePointAnalysis.NAME));
        assertTrue(names.contains(MatrixStatistics.NAME));
        assertTrue(names.contains(NetworkStatistics.NAME));

    }

    private void log(String message) {
        System.out.println(message);
    }
}
