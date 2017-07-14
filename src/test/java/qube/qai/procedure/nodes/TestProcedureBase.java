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

import junit.framework.TestCase;
import qube.qai.data.Metrics;
import qube.qai.parsers.antimirov.nodes.BaseNode;

import java.util.Collection;

/**
 * Created by rainbird on 4/7/17.
 */
public class TestProcedureBase extends TestCase {

    private boolean debug = true;

    protected void log(Metrics metrics) {
        if (debug) {
            for (String name : metrics.getNames()) {
                String line = name + ": " + metrics.getValue(name);
                System.out.println(line);
            }
        }
    }

    protected void checkProcedureInputs(ProcedureDescription description) {
        Collection<String> names = description.getProcedureInputs().getInputNames();
        assertTrue("there has to be input names", !names.isEmpty());
        for (String name : names) {
            BaseNode node = description.getProcedureInputs().getNamedInput(name);
            assertNotNull("there has to be a node", node);
            log("input named: " + name + " and corresponding node: " + node.toString());
        }
    }

    protected void checkProcedureResults(ProcedureDescription description) {
        Collection<String> names = description.getProcedureResults().getResultNames();
        assertTrue("there has to be result names", !names.isEmpty());
        for (String name : names) {
            BaseNode node = description.getProcedureResults().getNamedResult(name);
            assertNotNull("there has to be a node with name: " + name, node);
            log("result name: " + name + " " + node.toString());
        }
    }

    protected void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
