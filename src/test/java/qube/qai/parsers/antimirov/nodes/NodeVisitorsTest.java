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
public class NodeVisitorsTest extends TestCase {

    Logger logger = Logger.getLogger("NodeVisitorsTest");
    StringBuffer buffer;

    public void testVisitorPattern() throws Exception {

        buffer = new StringBuffer();
        BaseNode node = new ConcatenationNode(new ConcatenationNode(new Node(new Name("foo")),
                new Node(new Name("baz"), new PrimitiveNode(new Name("integer")))),
                new Node(new Name("bar"), new PrimitiveNode(new Name("double"))));

        NodeVisitor visitor = createVisitor();
        node.childrenAccept(visitor, null);
        log("buffer collected: '" + buffer.toString() + "'");
        assertTrue("concatenation concatenation foo baz integer bar double ".equals(buffer.toString()));
    }

    private NodeVisitor createVisitor() {
        NodeVisitor visitor = new NodeVisitor() {

            @Override
            public Object visit(AlternationNode node, Object data) {
                buffer.append(node.getName() + " ");
                return data;
            }

            @Override
            public Object visit(ConcatenationNode node, Object data) {
                buffer.append(node.getName() + " ");
                return data;
            }

            @Override
            public Object visit(EmptyNode node, Object data) {
                buffer.append(node.getName() + " ");
                return data;
            }

            @Override
            public Object visit(IterationNode node, Object data) {
                buffer.append(node.getName() + " ");
                return data;
            }

            @Override
            public Object visit(Node node, Object data) {
                buffer.append(node.getName() + " ");
                return data;
            }

            @Override
            public Object visit(NameNode node, Object data) {
                buffer.append(node.getName());
                return data;
            }

            @Override
            public Object visit(NoneNode node, Object data) {
                buffer.append(node.getName() + " ");
                return data;
            }

            @Override
            public Object visit(PrimitiveNode node, Object data) {
                buffer.append(node.getName() + " ");
                return data;
            }

        };

        return visitor;
    }

    private void log(String message) {
        //System.out.println(message);
        logger.info(message);
    }
}
