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
                new Node(new Name("bar"), new PrimitiveNode(new Name("double"))));;
        NodeVisitor visitor = createVisitor();
        node.childrenAccept(visitor);
        log("buffer collected: '" + buffer.toString() + "'");
        assertTrue("concatenation concatenation concatenation foo foo baz baz integer integer bar bar double double ".equals(buffer.toString()));
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
