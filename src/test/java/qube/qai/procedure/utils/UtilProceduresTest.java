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

package qube.qai.procedure.utils;

import qube.qai.persistence.DummyQaiDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.nodes.ProcedureDescription;
import qube.qai.procedure.nodes.ProcedureTestBase;

import java.util.Collection;

/**
 * Created by rainbird on 3/12/17.
 */
public class UtilProceduresTest extends ProcedureTestBase {

    public void testAttachProcedure() throws Exception {

        AttachProcedure procedure = ProcedureLibrary.attachTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        checkProcedureInputs(description);

        checkProcedureResults(description);
    }

    public void testSelectionProcedure() throws Exception {

        SelectionProcedure procedure = ProcedureLibrary.selectionTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        checkProcedureInputs(description);

        checkProcedureResults(description);
    }

    public void testSimpleProcedure() throws Exception {

        SimpleProcedure procedure = ProcedureLibrary.simpleTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        //checkProcedureInputs(description);

        //checkProcedureResults(description);
    }

    public void testForEachProcedure() throws Exception {

        /**
         * @TODO this is in fact just the beginning, obviously security is an issue.
         * as the for-each procedures will actually be spawning children
         * procedures the user-settings and user-rights has to be tested as well
         * particularly for this procedure security is also an issue
         */

        ForEach procedure = new ForEach();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be an description", description);

        fail("Security issues are not yet tested!");

        Collection<StockEntity> stockEntities = null;
        QaiDataProvider<Collection<StockEntity>> entityProvider = new DummyQaiDataProvider<>(stockEntities);

    }

    public void testSliceProcedure() throws Exception {

        /**
         * @TODO obviously this test is entirely missing
         */

        SliceProcedure procedure = new SliceProcedure();
        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be an description", description);

        fail("rest of the class is not yet tested!");
    }


    public void testCreateUserProcedure() throws Exception {

        CreateUserProcedure procedure = ProcedureLibrary.createUserTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        checkProcedureInputs(description);

        checkProcedureResults(description);
    }
}
