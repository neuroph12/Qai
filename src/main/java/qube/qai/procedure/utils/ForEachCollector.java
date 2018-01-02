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
import qube.qai.persistence.QaiDataProvider;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.SpawningProcedure;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class ForEachCollector extends Procedure implements SpawningProcedure {

    public static String NAME = "ForEachCollector";

    public static String DESCRIPTION = "To be used for passing a collection of a parameter to a child process";

    @Inject
    private transient ProcedureRunnerInterface procedureRunner;

    private QaiDataProvider<Collection> targetCollectionProvider;

    private String targetInputName;

    private Procedure targetProcedure;

    @Override
    public void execute() {

        if (!StringUtils.isBlank(targetInputName)
                || !StringUtils.isBlank((String) getInputValueOf(TARGET_INPUT_NAME))) {

            targetProcedure.getProcedureDescription().getProcedureInputs().getNamedInput(targetInputName).setValue(targetCollectionProvider);
            procedureRunner.submitProcedure(targetProcedure);
        }
    }

    @Override
    public Procedure createInstance() {
        return new ForEachCollector();
    }

    @Override
    public boolean haveChildrenExceuted() {

        if (targetProcedure instanceof SpawningProcedure) {
            return ((SpawningProcedure) targetProcedure).haveChildrenExceuted();
        }

        ProcedureState state = procedureRunner.queryState(targetProcedure.getUuid());
        if (ProcedureState.ENDED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public Set<String> getSpawnedProcedureUUIDs() {
        Set<String> uuids = new TreeSet<>();
        uuids.add(targetProcedure.getUuid());
        return uuids;
    }

    @Override
    protected void buildArguments() {

    }
}
