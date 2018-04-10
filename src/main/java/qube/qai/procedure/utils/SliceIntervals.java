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

import qube.qai.procedure.Procedure;
import qube.qai.procedure.SpawningProcedure;
import qube.qai.procedure.analysis.ChangePoints;
import qube.qai.procedure.analysis.SortPercentiles;

import java.util.Set;

public class SliceIntervals extends Procedure implements SpawningProcedure {

    private ChangePoints changePoint;

    private SortPercentiles sortPercentiles;

    /**
     * @TODO pretty much everything is missing here
     * this is where the slices which will be used for neural-network training
     * will be prepared.
     */
    public SliceIntervals() {
    }

    @Override
    public void execute() {

    }

    @Override
    public Procedure createInstance() {
        return new SliceIntervals();
    }

    @Override
    protected void buildArguments() {

    }

    @Override
    public Set<String> getSpawnedProcedureUUIDs() {
        return null;
    }

    @Override
    public boolean haveChildrenExceuted() {
        return false;
    }
}
