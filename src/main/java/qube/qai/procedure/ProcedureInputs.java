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

package qube.qai.procedure;

import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.ConcatenationNode;
import qube.qai.parsers.antimirov.nodes.Name;
import qube.qai.procedure.visitor.NameCollectingVisitor;
import qube.qai.procedure.visitor.NameSearchingVisitor;

import java.util.Collection;

/**
 * Created by rainbird on 3/30/17.
 */
public class ProcedureInputs extends ConcatenationNode {

    public static String NAME = "Procedure Inputs";

    public ProcedureInputs() {
        this.name = new Name(NAME);
    }

    public void addInput(BaseNode input) {
        if (getFirstChild() == null) {
            setFirstChild(input);
        } else if (getSecondChild() == null) {
            setSecondChild(input);
        } else {
            BaseNode tmp = getSecondChild();
            setSecondChild(new ConcatenationNode(tmp, input));
        }
    }

    public BaseNode getNamedInput(String name) {
        NameSearchingVisitor visitor = new NameSearchingVisitor();
        childrenAccept(visitor, name);
        return visitor.getFirstFound();
    }

    public Collection<String> getInputNames() {
        NameCollectingVisitor visitor = new NameCollectingVisitor();
        childrenAccept(visitor, null);
        return visitor.getAllFound();
    }

}
