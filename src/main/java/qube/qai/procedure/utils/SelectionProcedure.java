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

import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;

/**
 * Created by rainbird on 12/27/15.
 */
public class SelectionProcedure extends Procedure {

    public static String NAME = "Selection Procedure";

    public static String DESCRIPTION = "Selects the data input for other procedures";

    public static String PARAMEETER_ASSIIGN_TO = "ASSIGN_TO";

    public static String PARAMEETER_ASSIIGN_FROM = "ASSIGN_FROM";

    private ValueNode valueFrom;

    private ValueNode valueTo;

    private Object value;

    /**
     * this is mainly to pass the children the argument
     * represents user preparing, or choosing a certain
     * input for the children to process
     */
    public SelectionProcedure() {
        super(NAME);
    }

    public SelectionProcedure(ValueNode targetValue) {
        valueTo = targetValue;
    }

    public SelectionProcedure(ValueNode valueFrom, ValueNode valueTo) {
        this();
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(PARAMEETER_ASSIIGN_TO));
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(PARAMEETER_ASSIIGN_FROM));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(POINTER_OR_DATA_VALUE));
    }

    @Override
    public void execute() {

        // this is all there is to it really, and nothing else ...
        if (valueFrom != null && value == null) {
            value = valueFrom.getValue();
        }

        if (value == null) {
            throw new RuntimeException("Value to set for '" + valueTo.getName() + "' has not beein assigned right- null value");
        }

        valueTo.setValue(value);

    }

    public ValueNode getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(ValueNode valueFrom) {
        this.valueFrom = valueFrom;
    }

    public ValueNode getValueTo() {
        return valueTo;
    }

    public void setValueTo(ValueNode valueTo) {
        this.valueTo = valueTo;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
