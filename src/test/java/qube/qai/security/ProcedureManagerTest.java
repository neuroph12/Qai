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

package qube.qai.security;

import qube.qai.main.QaiTestBase;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibraryInterface;
import qube.qai.user.User;

import javax.inject.Inject;

public class ProcedureManagerTest extends QaiTestBase {

    @Inject
    private ProcedureManagerInterface procedureManager;

    @Inject
    private ProcedureLibraryInterface procedureLibrary;

    public void testProcedureManager() throws Exception {

        User user = new User();
        Procedure procedure = procedureLibrary.getTemplateMap().values().iterator().next().createProcedure();
        procedure.setUser(user);
        assertTrue("with this setting we expect no result", !procedureManager.isProcedureAndUserAuthorized(procedure));

        procedureManager.isProcedureAndUserAuthorized(procedure);
    }
}
