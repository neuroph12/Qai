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

import qube.qai.data.Arguments;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 12/27/15.
 */
public class SelectionProcedure extends Procedure {

    public static String NAME = "Selection Procedure";

    /**
     * this is mainly to pass the children the argument
     * represents user preparing, or choosing a certain
     * input for the children to process
     */
    public SelectionProcedure() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments();
    }

    @Override
    public void execute() {
        // do nothing as well...
    }

    @Override
    @thewebsemantic.Id
    public String getUuid() {
        return this.uuid;
    }
}
