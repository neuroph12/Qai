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

package qube.qai.procedure.nodes;

import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.Name;
import qube.qai.parsers.antimirov.nodes.NameNode;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 4/5/17.
 */
public class ValueNode<T extends Object> extends NameNode {

    private T value;

    public ValueNode(String name) {
        super(new Name(name));
    }

    public ValueNode(String name, T value) {
        this(name);
        this.value = value;
    }

    public ValueNode(String name, BaseNode child) {
        this(name);
        setFirstChild(child);
    }

    public T getValue() {
        if (value == null) {
            if (getFirstChild() instanceof Procedure) {
                Procedure procedure = (Procedure) getFirstChild();
                ValueNode resultNode = procedure.getProcedureResults().getNamedResult(name.getName());
                value = ((T) resultNode.getValue());
            }

        }
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
