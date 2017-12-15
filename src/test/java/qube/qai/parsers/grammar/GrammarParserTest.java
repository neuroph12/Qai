/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

package qube.qai.parsers.grammar;

import junit.framework.TestCase;
import org.jparsec.Parser;
import qube.qai.parsers.antimirov.nodes.AlternationNode;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.ConcatenationNode;
import qube.qai.parsers.antimirov.nodes.NameNode;

public class GrammarParserTest extends TestCase {

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

    public void testGrammarParser() throws Exception {

        GrammarParser grammar = new GrammarParser();
        assertNotNull(grammar);

        Parser<BaseNode> parser = grammar.rule();

        assertParser(parser, "foo", NameNode.class, "foo");
        assertParser(parser, "'foo'", NameNode.class, "'foo'");
        assertParser(parser, "foo bar", ConcatenationNode.class, "foo bar");
        assertParser(parser, "foo bar | baz |'foo'", AlternationNode.class, "(foo bar | baz | 'foo')");
        assertParser(parser, "foo bar | \n  baz |'foo'", AlternationNode.class, "(foo bar | (baz | 'foo'))");
        assertParser(parser, "foo bar  baz |'foo'", AlternationNode.class, "(foo bar baz | 'foo')");
        assertParser(parser, "foo bar \n  baz |'foo'", ConcatenationNode.class, "foo bar (baz | 'foo')");
        assertParser(parser, "foo bar (baz |'foo')", ConcatenationNode.class, "foo bar (baz | 'foo')");
        assertParser(parser, "foo \n| bar", AlternationNode.class, "(foo | bar)");
        assertParser(parser, "foo | \n  bar | baz \n| 'foo'",
                AlternationNode.class, "(foo | (bar | baz) | 'foo')");
    }

    private static void assertParser(Parser<?> parser, String source, Class<?> expectedClass, String string) {
        Object result = TerminalParser.parse(parser, source);
        assertTrue("expression '" + source + "' expecting " + expectedClass + " found " + result, expectedClass.isInstance(result));
        assertEquals(string, result.toString());
    }
}
