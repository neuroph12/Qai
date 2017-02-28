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

import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * Created by rainbird on 1/27/17.
 */
public class TestNodeVisitors extends TestCase {

    Logger logger = Logger.getLogger("TestNodeVisitors");
    StringBuffer buffer;

    public void testVisitorPattern() throws Exception {

        buffer = new StringBuffer();
        BaseNode node = new ConcatenationNode(new ConcatenationNode(new Node(new Name("foo")),
                new Node(new Name("baz"), new PrimitiveNode(new Name("integer")))),
                new Node(new Name("bar"), new PrimitiveNode(new Name("double"))));
        ;
        NodeVisitor visitor = createVisitor();
        node.childrenAccept(visitor);
        log("buffer collected: '" + buffer.toString() + "'");
        assertTrue("concatenation concatenation foo baz integer bar double ".equals(buffer.toString()));
    }

    private NodeVisitor createVisitor() {
        NodeVisitor visitor = new NodeVisitor() {

            @Override
            public void visit(AlternationNode node) {
                buffer.append(node.getName() + " ");
            }

            @Override
            public void visit(ConcatenationNode node) {
                buffer.append(node.getName() + " ");
            }

            @Override
            public void visit(EmptyNode node) {
                buffer.append(node.getName() + " ");
            }

            @Override
            public void visit(IterationNode node) {
                buffer.append(node.getName() + " ");
            }

            @Override
            public void visit(Node node) {
                buffer.append(node.getName() + " ");
            }

            @Override
            public void visit(NameNode node) {
                buffer.append(node.getName());
            }

            @Override
            public void visit(NoneNode node) {
                buffer.append(node.getName() + " ");
            }

            @Override
            public void visit(PrimitiveNode node) {
                buffer.append(node.getName() + " ");
            }
        };

        return visitor;
    }

    private void log(String message) {
        //System.out.println(message);
        logger.info(message);
    }
}
