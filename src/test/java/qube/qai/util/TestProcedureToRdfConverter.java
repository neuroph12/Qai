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

package qube.qai.util;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.SelectionProcedure;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;
import qube.qai.services.implementation.UUIDService;

import java.util.logging.Logger;

/**
 * Created by rainbird on 1/25/17.
 */
public class TestProcedureToRdfConverter extends TestCase {

    private Logger logger = Logger.getLogger("TestProcedureToRdfConverter");

    public static String MODEL_OUTPUT_TYPE = "RDF/XML-ABBREV";

    public void rtestRdfConverter() throws Exception {

        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = NeuralNetworkAnalysis.Factory.constructProcedure(selection);

        String uuid = UUIDService.uuidString();
        Model model = createDummyModel(uuid);
        assertNotNull("there has to be a model", model);
        model.write(System.out, MODEL_OUTPUT_TYPE);
    }

    public void testModelConversion() throws Exception {

        ProcedureToRdfConverter converter = new ProcedureToRdfConverter();

        Procedure procedure = createDummyProcedure();
        Model model = converter.createProcedureModel(procedure);
        model.write(System.out, ProcedureToRdfConverter.MODEL_OUTPUT_TYPE);

    }

    private Procedure createDummyProcedure() {

        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = NeuralNetworkAnalysis.Factory.constructProcedure(selection);

        return procedure;
    }

    private Model createDummyModel(String uuid) {
        Model model = ModelFactory.createDefaultModel();

        return model;
    }

}
