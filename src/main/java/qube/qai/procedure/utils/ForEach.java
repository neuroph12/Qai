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

import org.apache.commons.lang3.StringUtils;
import qube.qai.persistence.DummyQaiDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import java.util.Collection;

public class ForEach extends Procedure {

    public static String NAME = "For-Each Iterator Procedure";

    public static String DESCRIPTION = "A procedure which applies given procedure template to each element in the given " +
            "collection of objects which will be passed to each of the procedures and those will be forwarded " +
            "to ProcedureRunner for execution. If no procedure template has been supplied, in its simplest form this " +
            "procedure can be used simply to pass collections to other procedures which need a collection input.";

//    public static String PROCEDURE_TEMPLATE = "PROCEDURE_TEMPLATE";
//
//    public static String TARGET_INPUT_NAME = "TARGET_INPUT_NAME";

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    protected QaiDataProvider<Collection> targetCollectionProvider;

    protected String targetInputName;

    protected ProcedureTemplate template;

    /**
     * A procedure which applies given procedure template to each element in the given
     * collection of objects which will be passed to each of the procedures and those will be forwarded
     * to ProcedureRunner for execution.
     */
    public ForEach() {
        super(NAME);
    }

//    public ForEach(QaiDataProvider<Collection> targetCollectionProvider, String targetInputName, ProcedureTemplate template) {
//        this.targetCollectionProvider = targetCollectionProvider;
//        this.targetInputName = targetInputName;
//        this.template = template;
//    }

    @Override
    protected void buildArguments() {

    }

    @Override
    public void execute() {

        // check first whether we actually are given a procedure-template
        if (template != null || getInputValueOf(PROCEDURE_TEMPLATE) != null) {

            // if we do not have a procedure-runner we simply cannot go on
            if (procedureRunner == null) {
                String message = "No ProcedureRunner supplied- severe configuration error. Stopping execution";
                error(message);
                throw new IllegalStateException(message);
            }

            // all we need left to do is to have the input parameter name
            if (StringUtils.isBlank(targetInputName)
                    && StringUtils.isBlank((String) getInputValueOf(TARGET_INPUT_NAME))) {
                String message = "Input filed name in target procedures must be supplied. Stopping execution";
                error(message);
                throw new IllegalArgumentException(message);
            }

            Collection objects = targetCollectionProvider.getData();
            for (Object param : objects) {
                Procedure procedure = template.createProcedure();
                QaiDataProvider provider = new DummyQaiDataProvider(param);
                procedure.getProcedureDescription().getProcedureInputs().getNamedInput(targetInputName).setValue(provider);
                procedureRunner.submitProcedure(procedure);
                String templStr = "Child procedure: '%s' with UUID: '%s' with input param: '%s' with value: '%s' has been submitted";
                info(String.format(templStr, procedure.getProcedureName(), procedure.getUuid(), targetInputName, param));
            }
        }

    }

    public QaiDataProvider<Collection> getTargetCollectionProvider() {
        return targetCollectionProvider;
    }

    public void setTargetCollectionProvider(QaiDataProvider<Collection> targetCollectionProvider) {
        this.targetCollectionProvider = targetCollectionProvider;
    }

    public String getTargetInputName() {
        return targetInputName;
    }

    public void setTargetInputName(String targetInputName) {
        this.targetInputName = targetInputName;
    }

    public ProcedureTemplate getTemplate() {
        return template;
    }

    public void setTemplate(ProcedureTemplate template) {
        this.template = template;
    }

    public ProcedureRunnerInterface getProcedureRunner() {
        return procedureRunner;
    }

    public void setProcedureRunner(ProcedureRunnerInterface procedureRunner) {
        this.procedureRunner = procedureRunner;
    }
}
