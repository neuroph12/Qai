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

package qube.qai.parsers.grammar;

import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Terminals;
import qube.qai.parsers.antimirov.nodes.*;

import java.util.List;
import java.util.function.Function;

public class GrammarParser {


    static Function<String, BaseNode> createLiteral = new Function<String, BaseNode>() {
        @Override
        public BaseNode apply(String s) {
            return new NameNode(new Name(s));
        }
    };

    static Function<String, BaseNode> createIdent = new Function<String, BaseNode>() {
        @Override
        public BaseNode apply(String s) {
            return new Node(new Name(s));
        }
    };

    final Parser<BaseNode> LITERAL = Terminals.StringLiteral.PARSER.map(createLiteral);

    final Parser<BaseNode> IDENT = Terminals.Identifier.PARSER.notFollowedBy(TerminalParser.term("::=")).map(createIdent);

    Parser<BaseNode> RULE_DEF = Parsers.sequence(
            Terminals.Identifier.PARSER, TerminalParser.term("::="), rule(), (name, __, r) -> new Node(new Name(name), r));

    public Parser<List<BaseNode>> RULE_DEFS = RULE_DEF.many();

    public GrammarParser() {
    }

    public BaseNode parse(String expression) {
        BaseNode result = TerminalParser.parse(rule(), expression);
        return result;
    }

    Parser<BaseNode> rule() {
        Parser.Reference<BaseNode> ref = Parser.newReference();
        Parser<BaseNode> atom = Parsers.or(LITERAL, IDENT, unit(ref.lazy()));
        Parser<BaseNode> parser = alternative(sequential(atom));
        ref.set(parser);
        return parser;
    }

    Parser<BaseNode> unit(Parser<BaseNode> rule) {
        return Parsers.or(
                rule.between(TerminalParser.term("("), TerminalParser.term(")")),
                rule.between(TerminalParser.INDENTATION.indent(), TerminalParser.INDENTATION.outdent()));
    }

    Parser<BaseNode> sequential(Parser<BaseNode> rule) {
        return rule.many1().map(list -> list.size() == 1 ? list.get(0) : new ConcatenationNode(list));
    }

    Parser<BaseNode> alternative(Parser<BaseNode> rule) {
        return rule.sepBy1(TerminalParser.term("|")).map(list -> list.size() == 1 ? list.get(0) : new AlternationNode(list));
    }


}
