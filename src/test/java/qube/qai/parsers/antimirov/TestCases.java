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

package qube.qai.parsers.antimirov;

import qube.qai.parsers.antimirov.nodes.*;

/**
 * Class <code>TestCases</code> provides an array of test types and
 * the expected results for each subtyping test.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Tester
 */
public final class TestCases {


    /**
     * Returns test types for proving. The interpretation is done by
     * class <code>Tester</code>.
     *
     * @return Types for proving.
     */
    public static BaseNode[] getProveTestTypes() {

        Name foo = new Name("foo");
        Name bar = new Name("bar");
        Name baz = new Name("baz");
        Name blub = new Name("blub");
        Name lex = new Name("lex");
        Name bla = new Name("bla");
        Name STR = new Name(Name.STRING);
        Name INT = new Name(Name.INTEGER);

        try {

            // t00 = foo[str] (bar[int] foo[str])*
            BaseNode t00 = new ConcatenationNode(
                    new Node(foo,
                            new PrimitiveNode(STR)),
                    new IterationNode(
                            new ConcatenationNode(
                                    new Node(bar,
                                            new PrimitiveNode(INT)),
                                    new Node(foo,
                                            new PrimitiveNode(STR))
                            )
                    )
            );

            // t01 = (foo[str] bar[int])* foo[str]
            BaseNode t01 = new ConcatenationNode(
                    new IterationNode(
                            new ConcatenationNode(
                                    new Node(foo,
                                            new PrimitiveNode(STR)
                                    ),
                                    new Node(bar,
                                            new PrimitiveNode(INT)
                                    )
                            )
                    ),
                    new Node(foo,
                            new PrimitiveNode(STR)
                    )
            );

            // t02 = (foo[str] bar[int])* foo[str] | baz
            BaseNode t02 =
                    new NameNode(
                            new Name("t"),
                            new AlternationNode(
                                    new ConcatenationNode(
                                            new IterationNode(
                                                    new ConcatenationNode(
                                                            new Node(foo,
                                                                    new PrimitiveNode(STR)
                                                            ),
                                                            new Node(bar,
                                                                    new PrimitiveNode(INT)
                                                            )
                                                    )
                                            ),
                                            new Node(foo,
                                                    new PrimitiveNode(STR)
                                            )
                                    ),
                                    new Node(baz)
                            )
                    );

            // t03: int int
            BaseNode t03 = new ConcatenationNode(
                    new PrimitiveNode(INT),
                    new PrimitiveNode(INT)
            );

            // t04: int*
            BaseNode t04 = new IterationNode(new PrimitiveNode(INT));

            // t05 :  (int t05) | e
            Name t05Name = new Name("t05");
            BaseNode t05 = new NameNode(
                    t05Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new PrimitiveNode(INT),
                                    new NameNode(t05Name)
                            ),
                            new EmptyNode()
                    )
            );

            // t06: foo bar baz t06 | foo t06 | e
            Name t06Name = new Name("t06");
            BaseNode t06 = new NameNode(
                    t06Name,
                    new AlternationNode(
                            new AlternationNode(
                                    new ConcatenationNode(
                                            new ConcatenationNode(
                                                    new ConcatenationNode(
                                                            new Node(foo),
                                                            new Node(bar)
                                                    ),
                                                    new Node(baz)
                                            ),
                                            new NameNode(t06Name)
                                    ),
                                    new ConcatenationNode(
                                            new Node(foo),
                                            new NameNode(t06Name)
                                    )
                            ),
                            new EmptyNode()
                    )
            );

            // t07: foo bar baz t07 | foo bar t07 | e
            Name t07Name = new Name("t07");
            BaseNode t07 = new NameNode(
                    t07Name,
                    new AlternationNode(
                            new AlternationNode(
                                    new ConcatenationNode(
                                            new ConcatenationNode(
                                                    new ConcatenationNode(
                                                            new Node(foo),
                                                            new Node(bar)
                                                    ),
                                                    new Node(baz)
                                            ),
                                            new NameNode(t07Name)
                                    ),
                                    new ConcatenationNode(
                                            new ConcatenationNode(
                                                    new Node(foo),
                                                    new Node(bar)
                                            ),
                                            new NameNode(t07Name)
                                    )
                            ),
                            new EmptyNode()
                    )
            );

            // t08: foo baz t08 | e
            Name t08Name = new Name("t08");
            BaseNode t08 = new NameNode(
                    t08Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new ConcatenationNode(
                                            new Node(foo),
                                            new Node(baz)
                                    ),
                                    new NameNode(t08Name)
                            ),
                            new EmptyNode()
                    )
            );

            // t09: foo bar t09 | e
            Name t09Name = new Name("t09");
            BaseNode t09 = new NameNode(
                    t09Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new ConcatenationNode(
                                            new Node(foo),
                                            new Node(bar)
                                    ),
                                    new NameNode(t09Name)
                            ),
                            new EmptyNode()
                    )
            );

            // t10: e | int t10 | str int t10
            Name t10Name = new Name("t10");
            BaseNode t10 = new NameNode(
                    t10Name,
                    new AlternationNode(
                            new AlternationNode(
                                    new EmptyNode(),
                                    new ConcatenationNode(
                                            new PrimitiveNode(INT),
                                            new NameNode(t10Name)
                                    )
                            ),
                            new ConcatenationNode(
                                    new ConcatenationNode(
                                            new PrimitiveNode(STR),
                                            new PrimitiveNode(INT)
                                    ),
                                    new NameNode(t10Name)
                            )
                    )
            );

            // t11: e | int t11
            Name t11Name = new Name("t11");
            BaseNode t11 = new NameNode(
                    t11Name,
                    new AlternationNode(
                            new EmptyNode(),
                            new ConcatenationNode(
                                    new PrimitiveNode(INT),
                                    new NameNode(t11Name)
                            )
                    )
            );

            // t12 = (int)* ((str)* t12) | e
            Name t12Name = new Name("t12");
            BaseNode t12 = new NameNode(
                    t12Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new IterationNode(
                                            new PrimitiveNode(INT)
                                    ),
                                    new ConcatenationNode(
                                            new IterationNode(
                                                    new PrimitiveNode(STR)
                                            ),
                                            new NameNode(t12Name)
                                    )
                            ),
                            new EmptyNode()
                    )
            );

            // t13 = int ((str)* t13) | e
            Name t13Name = new Name("t13");
            BaseNode t13 = new NameNode(
                    t13Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new PrimitiveNode(INT),
                                    new ConcatenationNode(
                                            new IterationNode(
                                                    new PrimitiveNode(STR)
                                            ),
                                            new NameNode(t13Name)
                                    )
                            ),
                            new EmptyNode()
                    )
            );

            // t14 = int (str t14) | e
            Name t14Name = new Name("t14");
            BaseNode t14 = new NameNode(
                    t14Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new PrimitiveNode(INT),
                                    new ConcatenationNode(
                                            new PrimitiveNode(STR),
                                            new NameNode(t14Name)
                                    )
                            ),
                            new EmptyNode()
                    )
            );

            // t15 = (int | str (t15)*) foo | int t15
            Name t15Name = new Name("t15");
            BaseNode t15 = new NameNode(
                    t15Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new AlternationNode(
                                            new PrimitiveNode(INT),
                                            new ConcatenationNode(
                                                    new PrimitiveNode(STR),
                                                    new IterationNode(
                                                            new NameNode(t15Name)
                                                    )
                                            )
                                    ),
                                    new Node(foo)
                            ),
                            new ConcatenationNode(
                                    new PrimitiveNode(INT),
                                    new NameNode(t15Name)
                            )
                    )
            );

            // t16 = (int | str t16) foo
            Name t16Name = new Name("t16");
            BaseNode t16 = new NameNode(
                    t16Name,
                    new ConcatenationNode(
                            new AlternationNode(
                                    new PrimitiveNode(INT),
                                    new ConcatenationNode(
                                            new PrimitiveNode(STR),
                                            new NameNode(t16Name)
                                    )
                            ),
                            new Node(foo)
                    )
            );

            // t18 = [foo[bar[baz]]] int | str int
            BaseNode t18 = new AlternationNode(
                    new ConcatenationNode(
                            new Node(
                                    foo,
                                    new Node(
                                            bar,
                                            new Node(baz)
                                    )
                            ),
                            new PrimitiveNode(INT)
                    ),
                    new ConcatenationNode(
                            new PrimitiveNode(STR),
                            new PrimitiveNode(INT)
                    )
            );

            // t19 = [foo[bar[bla]]] int | str int
            BaseNode t19 = new AlternationNode(
                    new ConcatenationNode(
                            new Node(
                                    foo,
                                    new Node(
                                            bar,
                                            new Node(bla)
                                    )
                            ),
                            new PrimitiveNode(INT)
                    ),
                    new ConcatenationNode(
                            new PrimitiveNode(STR),
                            new PrimitiveNode(INT)
                    )
            );

            // t20 = foo[bar[baz]] lex[bla | blub[int]] t20 | e
            Name t20Name = new Name("t20");
            BaseNode t20 = new NameNode(
                    t20Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new Node(
                                            foo,
                                            new Node(
                                                    bar,
                                                    new Node(baz)
                                            )
                                    ),
                                    new ConcatenationNode(
                                            new Node(
                                                    lex,
                                                    new AlternationNode(
                                                            new Node(bla),
                                                            new Node(
                                                                    blub,
                                                                    new PrimitiveNode(INT)
                                                            )
                                                    )
                                            ),
                                            new NameNode(t20Name)
                                    )
                            ),
                            new EmptyNode()
                    )
            );

            // t21 = foo[bar[baz]] lex[bla | blub[str]] t21 | e
            Name t21Name = new Name("t21");
            BaseNode t21 = new NameNode(
                    t21Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new Node(
                                            foo,
                                            new Node(
                                                    bar,
                                                    new Node(baz)
                                            )
                                    ),
                                    new ConcatenationNode(
                                            new Node(
                                                    lex,
                                                    new AlternationNode(
                                                            new Node(bla),
                                                            new Node(
                                                                    blub,
                                                                    new PrimitiveNode(STR)
                                                            )
                                                    )
                                            ),
                                            new NameNode(t21Name)
                                    )
                            ),
                            new EmptyNode()
                    )
            );

            // t17 = none | e
            BaseNode t17 = new AlternationNode(
                    new NoneNode(),
                    new EmptyNode()
            );

            // t22 = e
            BaseNode t22 = new EmptyNode();

            // t23: ((foo | bar) t23) | e
            Name t23Name = new Name("t23");
            BaseNode t23 = new NameNode(
                    t23Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new AlternationNode(
                                            new Node(foo),
                                            new Node(bar)
                                    ),
                                    new NameNode(t23Name)
                            ),
                            new EmptyNode()
                    )
            );

            // t24: ((foo | bar) t23) | e
            BaseNode t24 = new NameNode(
                    new Name("t24"),
                    new AlternationNode(
                            new ConcatenationNode(
                                    new AlternationNode(
                                            new Node(foo),
                                            new Node(bar)
                                    ),
                                    t23
                            ),
                            new EmptyNode()
                    )
            );

            // t25: foo bar{5-8}
            BaseNode t25 = new ConcatenationNode(
                    new Node(foo),
                    new IterationNode(new Node(bar), 7, 9)
            );

            // t26: foo bar{5-10}
            BaseNode t26 = new ConcatenationNode(
                    new Node(foo),
                    new IterationNode(new Node(bar), 6, 10)
            );

            // t27: ((foo | bar) t27) | e
            Name t27Name = new Name("t27");
            BaseNode t27 = new NameNode(
                    t27Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new AlternationNode(
                                            new Node(foo),
                                            new Node(bar)
                                    ),
                                    new NameNode(t27Name)
                            ),
                            new EmptyNode()
                    )
            );

            // t28: a{2-5}
            BaseNode t28 = new IterationNode(
                    new Node(foo), 2, 5
            );

            // t29: a a{0-3} a
            BaseNode t29 = new ConcatenationNode(
                    new ConcatenationNode(
                            new Node(foo),
                            new IterationNode(new Node(foo), 0, 3)
                    ),
                    new Node(foo)
            );

            // t30: none | epsilon
            BaseNode t30 = new AlternationNode(
                    new NoneNode(),
                    new EmptyNode()
            );

            // t31: foo*
            BaseNode t31 = new IterationNode(
                    new Node(foo), 0, Name.INFINITY
            );

            // t32: epsilon
            BaseNode t32 = new EmptyNode();

            // t33: foo t33 | epsilon
            Name t33Name = new Name("t33");
            BaseNode t33 = new NameNode(
                    t33Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new Node(foo),
                                    new NameNode(t33Name)
                            ),
                            new EmptyNode()
                    )
            );

            // t34: foo t34 | epsilon
            Name t34Name = new Name("t34");
            BaseNode t34 = new NameNode(
                    t34Name,
                    new AlternationNode(
                            new ConcatenationNode(
                                    new Node(foo, null),
                                    new NameNode(t34Name)
                            ),
                            new EmptyNode()
                    )
            );

            int size = 40;
            BaseNode[] type = new BaseNode[size];

            type[0] = t00;     // 00 <: 01 |- true  (00)
            type[1] = t01;
            type[2] = t03;     // 03 <: 04 |- true  (01)
            type[3] = t04;
            type[4] = t04;     // 04 <: 05 |- true  (02)
            type[5] = t05;
            type[6] = t00;     // 00 <: 02 |- true  (03)
            type[7] = t02;
            type[8] = t08;     // 08 <: 09 |- false (04)
            type[9] = t09;
            type[10] = t06;    // 06 <: 07 |- false (05)
            type[11] = t07;
            type[12] = t11;    // 11 <: 10 |- true  (06)
            type[13] = t10;
            type[14] = t12;    // 12 <: 14 |- 0     (07)
            type[15] = t14;
            type[16] = t14;    // 14 <: 13 |- true  (08)
            type[17] = t13;
            type[18] = t15;    // 15 <: 16 |- 0     (09)
            type[19] = t16;
            type[20] = t18;    // 18 <: 19 |- false (10)
            type[21] = t19;
            type[22] = t20;    // 20 <: 21 |- false (11)
            type[23] = t21;
            type[24] = t17;    // 17 <: 22 |- true  (12)
            type[25] = t22;
            type[26] = t23;    // 23 <: 24 |- true  (13)
            type[27] = t24;
            type[28] = t25;    // 25 <: 26 |- true  (14)
            type[29] = t26;
            type[30] = t24;    // 24 <: 27 |- true  (15)
            type[31] = t27;
            type[32] = t28;    // 28 <: 29 |- true  (16)
            type[33] = t29;
            type[34] = t30;    // 30 <: 25 |- false (17)
            type[35] = t25;
            type[36] = t31;    // 31 <: 32 |- false (18)
            type[37] = t32;
            type[38] = t33;    // 33 <: 34 |- true  (19)
            type[39] = t34;

            return type;

        } catch (IncompleteTypeException ite) {

            System.err.println(
                    "Some type was not constructred properly."
            );

            return null;
        }//try catch
    }//getProveTestTypes


    /**
     * Return expected results for test cases.
     *
     * @return Expected results for test cases.
     */
    public static int[] getResults() {

        int size = 20;
        int[] result = new int[size];

        result[0] = 1;
        result[1] = 1;
        result[2] = 1;
        result[3] = 1;
        result[4] = -1;
        result[5] = -1;
        result[6] = 1;
        result[7] = 0;
        result[8] = 1;
        result[9] = 0;
        result[10] = -1;
        result[11] = -1;
        result[12] = 1;
        result[13] = 1;
        result[14] = 1;
        result[15] = 1;
        result[16] = 1;
        result[17] = -1;
        result[18] = -1;
        result[19] = 1;

        return result;
    }//getResults


}//class
