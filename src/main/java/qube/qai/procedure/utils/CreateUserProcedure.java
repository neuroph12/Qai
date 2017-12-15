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
import qube.qai.procedure.nodes.ValueNode;

/**
 * Created by rainbird on 7/14/17.
 */
public class CreateUserProcedure extends Procedure {

    public static String NAME = "Create User Procedure";

    public static String DESCRIPTION = "Creates a new user in the system";

    private String username;

    private String password;

    private String uuid;

    public CreateUserProcedure() {
        super(NAME);
    }

    @Override
    public void execute() {

    }

    @Override
    protected void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<String>(USER_NAME) {
            @Override
            public void setValue(String value) {
                super.setValue(value);
                username = value;
            }
        });
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<String>(PASSWORD) {
            @Override
            public void setValue(String value) {
                super.setValue(value);
                password = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<String>(USER_UUID) {
            @Override
            public String getValue() {
                return uuid;
            }
        });
    }
}
