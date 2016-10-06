package qube.qai.parsers;

/**
 * Created by rainbird on 9/27/16.
 */

import junit.framework.TestCase;
import org.jmathplot.gui.plotObjects.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.parsers.antimirov.TestCases;
import qube.qai.parsers.antimirov.nodes.*;

public class TestAntimirovParser extends TestCase {

    private static Logger logger = LoggerFactory.getLogger("TestAntimirovParser");

    private boolean debug = true;

    /*
    the test cases in question are as follows:
    node with index: 0 'foo[string] (bar[integer] foo[string])*'
    node with index: 1 '(foo[string] bar[integer])* foo[string]'
    node with index: 2 'integer integer'
    node with index: 3 '(integer)*'
    node with index: 4 '(integer)*'
    node with index: 5 't05: [(integer t05 | e)]'
    node with index: 6 'foo[string] (bar[integer] foo[string])*'
    node with index: 7 't: [((foo[string] bar[integer])* foo[string] | baz)]'
    node with index: 8 't08: [(foo baz t08 | e)]'
    node with index: 9 't09: [(foo bar t09 | e)]'
    node with index: 10 't06: [((foo bar baz t06 | foo t06) | e)]'
    node with index: 11 't07: [((foo bar baz t07 | foo bar t07) | e)]'
    node with index: 12 't11: [(e | integer t11)]'
    node with index: 13 't10: [((e | integer t10) | string integer t10)]'
    node with index: 14 't12: [((integer)* (string)* t12 | e)]'
    node with index: 15 't14: [(integer string t14 | e)]'
    node with index: 16 't14: [(integer string t14 | e)]'
    node with index: 17 't13: [(integer (string)* t13 | e)]'
    node with index: 18 't15: [((integer | string (t15)*) foo | integer t15)]'
    node with index: 19 't16: [(integer | string t16) foo]'
    node with index: 20 '(foo[bar[baz]] integer | string integer)'
    node with index: 21 '(foo[bar[bla]] integer | string integer)'
    node with index: 22 't20: [(foo[bar[baz]] lex[(bla | blub[integer])] t20 | e)]'
    node with index: 23 't21: [(foo[bar[baz]] lex[(bla | blub[string])] t21 | e)]'
    node with index: 24 '(none | e)'
    node with index: 25 'e'
    node with index: 26 't23: [((foo | bar) t23 | e)]'
    node with index: 27 't24: [((foo | bar) t23: [((foo | bar) t23 | e)] | e)]'
    node with index: 28 'foo (bar{7-9})'
    node with index: 29 'foo (bar{6-10})'
    node with index: 30 't24: [((foo | bar) t23: [((foo | bar) t23 | e)] | e)]'
    node with index: 31 't27: [((foo | bar) t27 | e)]'
    node with index: 32 '(foo{2-5})'
    node with index: 33 'foo (foo{0-3}) foo'
    node with index: 34 '(none | e)'
    node with index: 35 'foo (bar{7-9})'
    node with index: 36 '(foo)*'
    node with index: 37 'e'
    node with index: 38 't33: [(foo t33 | e)]'
    node with index: 39 't34: [(foo t34 | e)]'
    */

    public void restAllCases() throws Exception {

        BaseNode[] nodes = TestCases.getProveTestTypes();

        AntimirovParser parser = new AntimirovParser();

        for (int i = 0; i < nodes.length; i++) {
            BaseNode node = nodes[i];
            String expression = node.toString();

            log("node with index: " + i + " '" + expression + "'");

//            BaseNode expNode = parser.parseExpression(expression);
//            assertNotNull(expNode);
//            assertTrue(nodes[i].equals(expNode));
        }

    }

    public void testNameParser() throws Exception {
        AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = parser.name().parse("foo");
        assertNotNull("parsed node should not be null", parsedNode);
        BaseNode shouldNode = new Node(new Name("foo"));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));

        parsedNode = parser.name().parse("e");
        assertNotNull("parsed node should not be null", parsedNode);
        shouldNode = new EmptyNode();
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));
    }

    public void testType() throws Exception {
        AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = parser.type().parse("[integer]");
        assertNotNull("parsed node should not be null", parsedNode);
        BaseNode shouldNode = new PrimitiveNode((new Name("integer")));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));
    }

    public void testTypedName() throws Exception {
        AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = parser.typedName().parse("foo[integer]");
        assertNotNull("parsed node should not be null", parsedNode);
        BaseNode shouldNode = new Node(new Name("foo"), new PrimitiveNode((new Name("integer"))));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));
    }

    public void testElement() throws Exception {
        AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = parser.element().parse("foo[integer]");
        assertNotNull("parsed node should not be null", parsedNode);
        BaseNode shouldNode = new Node(new Name("foo"), new PrimitiveNode((new Name("integer"))));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));

        parsedNode = parser.element().parse("baz");
        assertNotNull("parsed node should not be null", parsedNode);
        shouldNode = new Node(new Name("baz"));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));
    }

    public void testConcatenation() throws Exception {
//        AntimirovParser parser = new AntimirovParser();
//        BaseNode parsedNode = parser.concatenation().parse("foo[double] baz[double]");
//        assertNotNull("parsed node should not be null", parsedNode);
//        BaseNode shouldNode = new ConcatenationNode(new Node(new Name("foo"), new PrimitiveNode(new Name("double"))),
//                new Node(new Name("baz"), new PrimitiveNode(new Name("double"))));
        //assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));

        AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = parser.concatenation().parse("foo baz[integer] bar[double]");
        assertNotNull("parsed node should not be null", parsedNode);
        BaseNode shouldNode = new ConcatenationNode(new ConcatenationNode(new Node(new Name("foo")),
                new Node(new Name("baz"), new PrimitiveNode(new Name("integer")))),
                new Node(new Name("bar"), new PrimitiveNode(new Name("double"))));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));
    }

    public void testIteration() throws Exception {
        AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = parser.iteration().parse("int*");
        assertNotNull("parsed node should not be null", parsedNode);
        BaseNode shouldNode = new IterationNode(new PrimitiveNode(new Name("int")));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));

        parser = new AntimirovParser();
        parsedNode = parser.iteration().parse("baz[integer]*");
        assertNotNull("parsed node should not be null", parsedNode);
        shouldNode = new IterationNode(new Node(new Name("baz"), new PrimitiveNode(new Name("integer"))));
        assertTrue("found: " + parsedNode.toString() + " should be: " + shouldNode.toString(), parsedNode.equals(shouldNode));
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        } else {
            logger.debug(message);
        }
    }
}
