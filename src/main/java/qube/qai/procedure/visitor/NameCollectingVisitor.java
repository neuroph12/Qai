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

import qube.qai.parsers.antimirov.nodes.*;

import java.util.ArrayList;

/**
 * Created by rainbird on 4/2/17.
 */
public class NameCollectingVisitor implements NodeVisitor {

    private ArrayList<String> allFound;

    public NameCollectingVisitor() {
        allFound = new ArrayList<>();
    }

    @Override
    public Object visit(AlternationNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ConcatenationNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(EmptyNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(IterationNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(Node node, Object data) {
        allFound.add(node.getName().getName());
        return data;
    }

    @Override
    public Object visit(NameNode node, Object data) {
        allFound.add(node.getName().getName());
        return data;
    }

    @Override
    public Object visit(NoneNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(PrimitiveNode node, Object data) {
        return null;
    }

//    private Object visitAll(BaseNode node, Object data) {
//        if (node.getName().getName().equals(data)) {
//            allFound.add(node.getName().getName());
//            return data;
//        }
//        return data;
//    }

    public ArrayList<String> getAllFound() {
        return allFound;
    }

    public void setAllFound(ArrayList<String> allFound) {
        this.allFound = allFound;
    }
}
