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

package qube.qai.procedure.visitor;

import org.apache.commons.lang3.StringUtils;
import qube.qai.parsers.antimirov.nodes.*;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 4/1/17.
 */
public class NameSearchingVisitor implements NodeVisitor {

    private Procedure found;

    @Override
    public Object visit(AlternationNode node, Object data) {
        return visitAll(node, data);
    }

    @Override
    public Object visit(ConcatenationNode node, Object data) {
        return visitAll(node, data);
    }

    @Override
    public Object visit(EmptyNode node, Object data) {
        return visitAll(node, data);
    }

    @Override
    public Object visit(IterationNode node, Object data) {
        return visitAll(node, data);
    }

    @Override
    public Object visit(Node node, Object data) {
//        if (node instanceof Procedure) {
//            Procedure p = (Procedure) node;
//            if (p.getName().getName().equals(data)) {
//                found = p;
//                return data;
//            }
//        }
        return visitAll(node, data);
    }

    @Override
    public Object visit(NameNode node, Object data) {
        return visitAll(node, data);
    }

    @Override
    public Object visit(NoneNode node, Object data) {
        return visitAll(node, data);
    }

    @Override
    public Object visit(PrimitiveNode node, Object data) {
        return visitAll(node, data);
    }


    private Object visitAll(BaseNode node, Object data) {
        if (data instanceof String && StringUtils.isNoneEmpty((String) data)) {
            return data;
        }
        return null;
    }

    public Procedure getFound() {
        return found;
    }

    public void setFound(Procedure found) {
        this.found = found;
    }
}
