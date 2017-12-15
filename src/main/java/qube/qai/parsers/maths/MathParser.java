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

package qube.qai.parsers.maths;

import org.jparsec.*;

/**
 * Created by rainbird on 10/8/16.
 */
public class MathParser {

    static final Parser<Double> NUMBER =
            Terminals.DecimalLiteral.PARSER.map(Double::valueOf);

    private static final Terminals OPERATORS =
            Terminals.operators("+", "-", "*", "/", "(", ")");

    static final Parser<Void> IGNORED = Parsers.or(
            Scanners.JAVA_LINE_COMMENT,
            Scanners.JAVA_BLOCK_COMMENT,
            Scanners.WHITESPACES).skipMany();

    static final Parser<?> TOKENIZER =
            Parsers.or(Terminals.DecimalLiteral.TOKENIZER, OPERATORS.tokenizer());

    static Parser<?> term(String... names) {
        return OPERATORS.token(names);
    }

    static final Parser<?> WHITESPACE_MUL = term("+", "-", "*", "/").not();

    static <T> Parser<T> op(String name, T value) {
        return term(name).retn(value);
    }

    static Parser<Double> calculator(Parser<Double> atom) {
        Parser.Reference<Double> ref = Parser.newReference();
        Parser<Double> unit = ref.lazy().between(term("("), term(")")).or(atom);
        Parser<Double> parser = new OperatorTable<Double>()
                .infixl(op("+", (l, r) -> l + r), 10)
                .infixl(op("-", (l, r) -> l - r), 10)
                .infixl(Parsers.or(term("*"), WHITESPACE_MUL).retn((l, r) -> l * r), 20)
                .infixl(op("/", (l, r) -> l / r), 20)
                .prefix(op("-", v -> -v), 30)
                .build(unit);
        ref.set(parser);
        return parser;
    }

    public static final Parser<Double> CALCULATOR =
            calculator(NUMBER).from(TOKENIZER, IGNORED);

}
