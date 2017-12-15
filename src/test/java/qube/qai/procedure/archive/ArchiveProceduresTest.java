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

package qube.qai.procedure.archive;

import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.nodes.ProcedureDescription;
import qube.qai.procedure.nodes.TestProcedureBase;

/**
 * Created by rainbird on 4/7/17.
 */
public class ArchiveProceduresTest extends TestProcedureBase {

    public void testDirectoryIndexer() throws Exception {

        DirectoryIndexer procedure = ProcedureLibrary.directoryIndexerTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        checkProcedureInputs(description);

        checkProcedureResults(description);
    }

    public void testWikiArchiveIndexer() throws Exception {

        WikiArchiveIndexer procedure = ProcedureLibrary.wikiArchiveIndexerTemplate.createProcedure();

        ProcedureDescription description = procedure.getProcedureDescription();
        assertNotNull("there has to be a description", description);
        log("description as text: " + description.getDescription());

        checkProcedureInputs(description);

        checkProcedureResults(description);
    }

}
