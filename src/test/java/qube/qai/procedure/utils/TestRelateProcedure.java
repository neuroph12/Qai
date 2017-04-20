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

package qube.qai.procedure.utils;

import org.apache.jena.rdf.model.Model;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.ProcedureDescription;
import qube.qai.procedure.TestProcedureBase;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 3/12/17.
 */
public class TestRelateProcedure extends TestProcedureBase {

    public void testRelateProcedure() throws Exception {

        RelateProcedure procedure = new RelateProcedure();
        DataServiceInterface dummyDateStore = new DataServiceInterface() {
            @Override
            public void save(Model model) {

            }

            @Override
            public void remove(Model model) {

            }

            @Override
            public Model createDefaultModel() {
                return null;
            }

            @Override
            public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {
                return null;
            }

            @Override
            public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
                return null;
            }

            @Override
            public void save(Class baseCLass, Object data) {

            }

            @Override
            public void remove(Class baseClass, Object toRemove) {

            }
        };
        procedure.setDataService(dummyDateStore);
        // @TODO this is to be completed- not really urgent right now
        //procedure.execute();
        //fail("currently code for checking the result is missing");
    }

    public void testStockEntityInitialization() throws Exception {

        RelateProcedure procedure = new RelateProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        checkProcedureInputs(description);

        checkProcedureResults(description);
    }
}
