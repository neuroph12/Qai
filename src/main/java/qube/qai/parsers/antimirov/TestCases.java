package qube.qai.parsers.antimirov;

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
    public static RType[] getProveTestTypes() {

        RName foo = new RName("foo");
        RName bar = new RName("bar");
        RName baz = new RName("baz");
        RName blub = new RName("blub");
        RName lex = new RName("lex");
        RName bla = new RName("bla");
        RName STR = new RName(RName.STRING);
        RName INT = new RName(RName.INTEGER);

        try {

            // t00 = foo[str] (bar[int] foo[str])*
            RType t00 = new RConcatenationType(
                    new RNodeType(foo,
                            new RPrimitiveType(STR)),
                    new RIterationType(
                            new RConcatenationType(
                                    new RNodeType(bar,
                                            new RPrimitiveType(INT)),
                                    new RNodeType(foo,
                                            new RPrimitiveType(STR))
                            )
                    )
            );

            // t01 = (foo[str] bar[int])* foo[str]
            RType t01 = new RConcatenationType(
                    new RIterationType(
                            new RConcatenationType(
                                    new RNodeType(foo,
                                            new RPrimitiveType(STR)
                                    ),
                                    new RNodeType(bar,
                                            new RPrimitiveType(INT)
                                    )
                            )
                    ),
                    new RNodeType(foo,
                            new RPrimitiveType(STR)
                    )
            );

            // t02 = (foo[str] bar[int])* foo[str] | baz
            RType t02 =
                    new RNameType(
                            new RName("t"),
                            new RAlternationType(
                                    new RConcatenationType(
                                            new RIterationType(
                                                    new RConcatenationType(
                                                            new RNodeType(foo,
                                                                    new RPrimitiveType(STR)
                                                            ),
                                                            new RNodeType(bar,
                                                                    new RPrimitiveType(INT)
                                                            )
                                                    )
                                            ),
                                            new RNodeType(foo,
                                                    new RPrimitiveType(STR)
                                            )
                                    ),
                                    new RNodeType(baz)
                            )
                    );

            // t03: int int
            RType t03 = new RConcatenationType(
                    new RPrimitiveType(INT),
                    new RPrimitiveType(INT)
            );

            // t04: int*
            RType t04 = new RIterationType(new RPrimitiveType(INT));

            // t05 :  (int t05) | e
            RName t05Name = new RName("t05");
            RType t05 = new RNameType(
                    t05Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RPrimitiveType(INT),
                                    new RNameType(t05Name)
                            ),
                            new REmptyType()
                    )
            );

            // t06: foo bar baz t06 | foo t06 | e
            RName t06Name = new RName("t06");
            RType t06 = new RNameType(
                    t06Name,
                    new RAlternationType(
                            new RAlternationType(
                                    new RConcatenationType(
                                            new RConcatenationType(
                                                    new RConcatenationType(
                                                            new RNodeType(foo),
                                                            new RNodeType(bar)
                                                    ),
                                                    new RNodeType(baz)
                                            ),
                                            new RNameType(t06Name)
                                    ),
                                    new RConcatenationType(
                                            new RNodeType(foo),
                                            new RNameType(t06Name)
                                    )
                            ),
                            new REmptyType()
                    )
            );

            // t07: foo bar baz t07 | foo bar t07 | e
            RName t07Name = new RName("t07");
            RType t07 = new RNameType(
                    t07Name,
                    new RAlternationType(
                            new RAlternationType(
                                    new RConcatenationType(
                                            new RConcatenationType(
                                                    new RConcatenationType(
                                                            new RNodeType(foo),
                                                            new RNodeType(bar)
                                                    ),
                                                    new RNodeType(baz)
                                            ),
                                            new RNameType(t07Name)
                                    ),
                                    new RConcatenationType(
                                            new RConcatenationType(
                                                    new RNodeType(foo),
                                                    new RNodeType(bar)
                                            ),
                                            new RNameType(t07Name)
                                    )
                            ),
                            new REmptyType()
                    )
            );

            // t08: foo baz t08 | e
            RName t08Name = new RName("t08");
            RType t08 = new RNameType(
                    t08Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RConcatenationType(
                                            new RNodeType(foo),
                                            new RNodeType(baz)
                                    ),
                                    new RNameType(t08Name)
                            ),
                            new REmptyType()
                    )
            );

            // t09: foo bar t09 | e
            RName t09Name = new RName("t09");
            RType t09 = new RNameType(
                    t09Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RConcatenationType(
                                            new RNodeType(foo),
                                            new RNodeType(bar)
                                    ),
                                    new RNameType(t09Name)
                            ),
                            new REmptyType()
                    )
            );

            // t10: e | int t10 | str int t10
            RName t10Name = new RName("t10");
            RType t10 = new RNameType(
                    t10Name,
                    new RAlternationType(
                            new RAlternationType(
                                    new REmptyType(),
                                    new RConcatenationType(
                                            new RPrimitiveType(INT),
                                            new RNameType(t10Name)
                                    )
                            ),
                            new RConcatenationType(
                                    new RConcatenationType(
                                            new RPrimitiveType(STR),
                                            new RPrimitiveType(INT)
                                    ),
                                    new RNameType(t10Name)
                            )
                    )
            );

            // t11: e | int t11
            RName t11Name = new RName("t11");
            RType t11 = new RNameType(
                    t11Name,
                    new RAlternationType(
                            new REmptyType(),
                            new RConcatenationType(
                                    new RPrimitiveType(INT),
                                    new RNameType(t11Name)
                            )
                    )
            );

            // t12 = (int)* ((str)* t12) | e
            RName t12Name = new RName("t12");
            RType t12 = new RNameType(
                    t12Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RIterationType(
                                            new RPrimitiveType(INT)
                                    ),
                                    new RConcatenationType(
                                            new RIterationType(
                                                    new RPrimitiveType(STR)
                                            ),
                                            new RNameType(t12Name)
                                    )
                            ),
                            new REmptyType()
                    )
            );

            // t13 = int ((str)* t13) | e
            RName t13Name = new RName("t13");
            RType t13 = new RNameType(
                    t13Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RPrimitiveType(INT),
                                    new RConcatenationType(
                                            new RIterationType(
                                                    new RPrimitiveType(STR)
                                            ),
                                            new RNameType(t13Name)
                                    )
                            ),
                            new REmptyType()
                    )
            );

            // t14 = int (str t14) | e
            RName t14Name = new RName("t14");
            RType t14 = new RNameType(
                    t14Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RPrimitiveType(INT),
                                    new RConcatenationType(
                                            new RPrimitiveType(STR),
                                            new RNameType(t14Name)
                                    )
                            ),
                            new REmptyType()
                    )
            );

            // t15 = (int | str (t15)*) foo | int t15
            RName t15Name = new RName("t15");
            RType t15 = new RNameType(
                    t15Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RAlternationType(
                                            new RPrimitiveType(INT),
                                            new RConcatenationType(
                                                    new RPrimitiveType(STR),
                                                    new RIterationType(
                                                            new RNameType(t15Name)
                                                    )
                                            )
                                    ),
                                    new RNodeType(foo)
                            ),
                            new RConcatenationType(
                                    new RPrimitiveType(INT),
                                    new RNameType(t15Name)
                            )
                    )
            );

            // t16 = (int | str t16) foo
            RName t16Name = new RName("t16");
            RType t16 = new RNameType(
                    t16Name,
                    new RConcatenationType(
                            new RAlternationType(
                                    new RPrimitiveType(INT),
                                    new RConcatenationType(
                                            new RPrimitiveType(STR),
                                            new RNameType(t16Name)
                                    )
                            ),
                            new RNodeType(foo)
                    )
            );

            // t18 = [foo[bar[baz]]] int | str int
            RType t18 = new RAlternationType(
                    new RConcatenationType(
                            new RNodeType(
                                    foo,
                                    new RNodeType(
                                            bar,
                                            new RNodeType(baz)
                                    )
                            ),
                            new RPrimitiveType(INT)
                    ),
                    new RConcatenationType(
                            new RPrimitiveType(STR),
                            new RPrimitiveType(INT)
                    )
            );

            // t19 = [foo[bar[bla]]] int | str int
            RType t19 = new RAlternationType(
                    new RConcatenationType(
                            new RNodeType(
                                    foo,
                                    new RNodeType(
                                            bar,
                                            new RNodeType(bla)
                                    )
                            ),
                            new RPrimitiveType(INT)
                    ),
                    new RConcatenationType(
                            new RPrimitiveType(STR),
                            new RPrimitiveType(INT)
                    )
            );

            // t20 = foo[bar[baz]] lex[bla | blub[int]] t20 | e
            RName t20Name = new RName("t20");
            RType t20 = new RNameType(
                    t20Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RNodeType(
                                            foo,
                                            new RNodeType(
                                                    bar,
                                                    new RNodeType(baz)
                                            )
                                    ),
                                    new RConcatenationType(
                                            new RNodeType(
                                                    lex,
                                                    new RAlternationType(
                                                            new RNodeType(bla),
                                                            new RNodeType(
                                                                    blub,
                                                                    new RPrimitiveType(INT)
                                                            )
                                                    )
                                            ),
                                            new RNameType(t20Name)
                                    )
                            ),
                            new REmptyType()
                    )
            );

            // t21 = foo[bar[baz]] lex[bla | blub[str]] t21 | e
            RName t21Name = new RName("t21");
            RType t21 = new RNameType(
                    t21Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RNodeType(
                                            foo,
                                            new RNodeType(
                                                    bar,
                                                    new RNodeType(baz)
                                            )
                                    ),
                                    new RConcatenationType(
                                            new RNodeType(
                                                    lex,
                                                    new RAlternationType(
                                                            new RNodeType(bla),
                                                            new RNodeType(
                                                                    blub,
                                                                    new RPrimitiveType(STR)
                                                            )
                                                    )
                                            ),
                                            new RNameType(t21Name)
                                    )
                            ),
                            new REmptyType()
                    )
            );

            // t17 = none | e
            RType t17 = new RAlternationType(
                    new RNoneType(),
                    new REmptyType()
            );

            // t22 = e
            RType t22 = new REmptyType();

            // t23: ((foo | bar) t23) | e
            RName t23Name = new RName("t23");
            RType t23 = new RNameType(
                    t23Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RAlternationType(
                                            new RNodeType(foo),
                                            new RNodeType(bar)
                                    ),
                                    new RNameType(t23Name)
                            ),
                            new REmptyType()
                    )
            );

            // t24: ((foo | bar) t23) | e
            RType t24 = new RNameType(
                    new RName("t24"),
                    new RAlternationType(
                            new RConcatenationType(
                                    new RAlternationType(
                                            new RNodeType(foo),
                                            new RNodeType(bar)
                                    ),
                                    t23
                            ),
                            new REmptyType()
                    )
            );

            // t25: foo bar{5-8}
            RType t25 = new RConcatenationType(
                    new RNodeType(foo),
                    new RIterationType(new RNodeType(bar), 7, 9)
            );

            // t26: foo bar{5-10}
            RType t26 = new RConcatenationType(
                    new RNodeType(foo),
                    new RIterationType(new RNodeType(bar), 6, 10)
            );

            // t27: ((foo | bar) t27) | e
            RName t27Name = new RName("t27");
            RType t27 = new RNameType(
                    t27Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RAlternationType(
                                            new RNodeType(foo),
                                            new RNodeType(bar)
                                    ),
                                    new RNameType(t27Name)
                            ),
                            new REmptyType()
                    )
            );

            // t28: a{2-5}
            RType t28 = new RIterationType(
                    new RNodeType(foo), 2, 5
            );

            // t29: a a{0-3} a
            RType t29 = new RConcatenationType(
                    new RConcatenationType(
                            new RNodeType(foo),
                            new RIterationType(new RNodeType(foo), 0, 3)
                    ),
                    new RNodeType(foo)
            );

            // t30: none | epsilon
            RType t30 = new RAlternationType(
                    new RNoneType(),
                    new REmptyType()
            );

            // t31: foo*
            RType t31 = new RIterationType(
                    new RNodeType(foo), 0, RName.INFINITY
            );

            // t32: epsilon
            RType t32 = new REmptyType();

            // t33: foo t33 | epsilon
            RName t33Name = new RName("t33");
            RType t33 = new RNameType(
                    t33Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RNodeType(foo),
                                    new RNameType(t33Name)
                            ),
                            new REmptyType()
                    )
            );

            // t34: foo t34 | epsilon
            RName t34Name = new RName("t34");
            RType t34 = new RNameType(
                    t34Name,
                    new RAlternationType(
                            new RConcatenationType(
                                    new RNodeType(foo, null),
                                    new RNameType(t34Name)
                            ),
                            new REmptyType()
                    )
            );

            int size = 40;
            RType[] type = new RType[size];

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
