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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureEvent;
import qube.qai.procedure.event.ProcedureEnded;
import qube.qai.procedure.event.ProcedureError;
import qube.qai.procedure.event.ProcedureInterrupted;
import qube.qai.procedure.event.ProcedureStarted;

public class TestProcedureManager implements ProcedureManagerInterface {

    private Logger logger = LoggerFactory.getLogger("TestProcedureManager");

    private String logtemplate = "Procedure-event emitted from: '%s' with uuid: '%s' with state: '%s'";

    @Override
    public String registerProcedure(Procedure procedure) {
        return procedure.getUuid();
    }

    /**
     * meant to be used only for testing!
     *
     * @param procedure
     * @return
     */
    @Override
    public boolean isProcedureAndUserAuthorized(Procedure procedure) {
        return true;
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureInterrupted interrupted) {
        logEvent(procedure, interrupted);
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureError error) {
        logEvent(procedure, error);
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureStarted started) {
        logEvent(procedure, started);
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureEnded ended) {
        logEvent(procedure, ended);
    }

    @Override
    public void processEvent(Procedure procedure, ProcedureEvent event) {
        logEvent(procedure, event);
    }

    private void logEvent(Procedure procedure, ProcedureEvent event) {
        String message = String.format(logtemplate, procedure.getProcedureName(), procedure.getUuid(), event.ofState());
        logger.info(message);
    }
}
