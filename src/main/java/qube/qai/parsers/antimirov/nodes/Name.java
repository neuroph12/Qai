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

package qube.qai.parsers.antimirov.nodes;

import java.io.Serializable;

/**
 * Represents a name. A name can be the name of a letter or the name
 * of a named type.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 */
public final class Name implements NodeSetElement, Serializable {

    //--------- Regular Expression Based Types

    /**
     * Name of the alternation type.
     */
    public static final String ALTERNATION = "alternation";

    /**
     * Name of the iteration type.
     */
    public static final String ITERATION = "iteration";

    /**
     * Name of the concatenation type.
     */
    public static final String CONCATENATION = "concatenation";

    /**
     * Name of the type matching nothing.
     */
    public static final String NONE = "none";

    /**
     * Name of the empty type.
     */
    public static final String EMPTY = "e";

    //--------- Primitive Types

    /**
     * Name of the primitive type double.
     */
    public static final String DOUBLE = "double";

    /**
     * Name of the primitive type decimal.
     */
    public static final String DECIMAL = "decimal";

    /**
     * Name of the primitive type integer.
     */
    public static final String INTEGER = "integer";

    /**
     * Name of the primitive type boolean.
     */
    public static final String BOOLEAN = "boolean";

    /**
     * Name of the primitive type string.
     */
    public static final String STRING = "string";

    //--------- XML BaseNode Types

    /**
     * Name of the node type comment.
     */
    public static final String COMMNODE = "comment";

    /**
     * Name of the node type processing instruction.
     */
    public static final String PINODE = "pi";

    /**
     * Name of the node type text.
     */
    public static final String TEXTNODE = "text";

    /**
     * Name of the node type resource.
     */
    public static final String DOCNODE = "resource";

    /**
     * Name of the node type attribute.
     */
    public static final String ATTRNODE = "attribute";

    /**
     * Name of the node type element.
     */
    public static final String ELEMNODE = "element";

    //--------- other

    /**
     * Name of the primitive type string.
     */
    public static final String NULL = "null";

    /**
     * Name of infinity.
     */
    public static final int INFINITY = -1; // always has to be
    // negative !!!

    /**
     * User defined name.
     */
    private String name;

    public Name() {
    }

    /**
     * Constructor for names.
     *
     * @param name The name to construct.
     */
    public Name(String name) {

        this.name = name;
    }//constructor


    /**
     * Returns the name.
     *
     * @return Name represented by the instance.
     */
    public String getName() {

        return this.name;
    }//getName


    public void setNameString(String name) {
        this.name = name;
    }

    /**
     * Returns TRUE, if instance represents same <code>String</code>
     * like <code>s</code>.
     *
     * @param s The <code>String</code> to compare the instance to.
     * @return TRUE, if <code>String</code>s are equal, otherwise FALSE.
     */
    public boolean equals(String s) {

        return this.name.equals(s);
    }//equals


    /**
     * Returns TRUE, if instance represents same <code>String</code>
     * like <code>s</code>.
     *
     * @param s The string to compare the instance to.
     * @return TRUE, if strings are equal, otherwise FALSE.
     */
    public boolean equals(NodeSetElement s) {

        return (s instanceof Name) ?

                (this.getName().equals(((Name) s).getName())) :

                false;
    }//equals


    /**
     * Returns a <code>String</code> representation of the name.
     *
     * @return A <code>String</code> representation of the name.
     */
    public String toString() {

        return new String(this.name.toString());
    }//toString


}//class
