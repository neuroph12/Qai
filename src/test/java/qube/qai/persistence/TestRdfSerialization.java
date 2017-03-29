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

package qube.qai.persistence;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.parsers.AntimirovParser;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.ConcatenationNode;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;
import qube.qai.procedure.utils.SelectionProcedure;
import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

/**
 * Created by rainbird on 3/29/17.
 */
public class TestRdfSerialization extends TestCase {

    private static Logger logger = LoggerFactory.getLogger("TestRdfSerialization");

    public void estRdfSerializationOfAntimirovNodes() throws Exception {

        // we make it easy for ourself creating the ast-tree
        BaseNode parsedNode = parseExpression("(foo baz[integer] bar[double])");
        String uuid = parsedNode.getUuid();
        logger.info("parsedNode with uuid: " + uuid + " " + parsedNode.toString());

        Model model = ModelFactory.createDefaultModel();
        Bean2RDF writer = new Bean2RDF(model);

        writer.save(parsedNode);
        model.write(System.out);

        // and now the tricky part
        RDF2Bean reader = new RDF2Bean(model);
        BaseNode storedNode = reader.load(ConcatenationNode.class, uuid);

        assertNotNull("there has to be a return value", storedNode);
        assertTrue("the nodes must be equal", parsedNode.toString().equals(storedNode.toString()));
        assertTrue("the nodes must be equal", parsedNode.equals(storedNode));
    }

    public void testRdfSerializationOfProcedures() throws Exception {

        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = new NeuralNetworkAnalysis();
        procedure.setFirstChild(selection);
        String uuid = procedure.getUuid();
        logger.info("procedure uuid: " + uuid + " " + procedure.toString());

        Model model = ModelFactory.createDefaultModel();
        Bean2RDF writer = new Bean2RDF(model);

        writer.save(procedure);
        model.write(System.out);

        RDF2Bean reader = new RDF2Bean(model);
        Procedure storedProcedure = reader.load(NeuralNetworkAnalysis.class, uuid);
        assertNotNull("three has to be something sotred after all", storedProcedure);
        assertEquals("the procedures must be equal", procedure.equals(storedProcedure));

    }

    private BaseNode parseExpression(String expression) {
        AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = parser.paranthesis(parser.expr()).parse(expression);
        return parsedNode;
    }
}
