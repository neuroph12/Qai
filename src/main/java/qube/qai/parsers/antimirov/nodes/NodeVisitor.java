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

package qube.qai.parsers.antimirov.nodes;

/**
 * Created by rainbird on 1/26/17.
 */
public interface NodeVisitor {

    void visit(AlternationNode node);

    void visit(ConcatenationNode node);

    void visit(EmptyNode node);

    void visit(IterationNode node);

    void visit(Node node);

    void visit(NameNode node);

    void visit(NoneNode node);

    void visit(PrimitiveNode node);

}
