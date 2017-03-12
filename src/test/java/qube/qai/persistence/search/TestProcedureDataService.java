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

package qube.qai.persistence.search;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;
import qube.qai.procedure.utils.SelectionProcedure;
import qube.qai.util.ProcedureToRdfConverter;

import java.util.logging.Logger;

/**
 * Created by rainbird on 1/25/17.
 */
public class TestProcedureDataService extends TestCase {

    Logger logger = Logger.getLogger("TestProcedureDataService");

    public void testProcedureDataService() throws Exception {

        ProcedureDataService dataService = new ProcedureDataService();

//        Collection<SearchResult> procedures = dataService.searchInputString("procedure", "*", 0);
//        assertNotNull("there has to be something", procedures);
//        assertTrue("the list may not be empty", !procedures.isEmpty());
        ProcedureToRdfConverter converter = new ProcedureToRdfConverter();
        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = NeuralNetworkAnalysis.Factory.constructProcedure(selection);
        Model model = converter.createProcedureModel(procedure);

        dataService.save(model);

        dataService.searchInputString("*", "uuid", 0);
    }
}
