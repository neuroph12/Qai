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


import qube.qai.parsers.antimirov.Inequality;

import java.util.Hashtable;


/**
 * Representation of a XML Schema primitive type. It can be the
 * superclass for type classes representing single primitive types like
 * <code>xs:integer</code> or <code>xs:boolean</code>.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see BaseNode
 */
public class PrimitiveNode
        extends BaseNode {


    /**
     * Constructor for <code>RPrimitiveType</code>.
     *
     * @param name Name of the type.
     */
    public PrimitiveNode(Name name) {

        super(null, null);
        this.name = name;
    }//constructor

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Does nothing, because <code>RPrimitiveType</code> does not use
     * members.
     *
     * @param t No meaning.
     */
    public void setFirstChild(BaseNode t) {
    }


    /**
     * Does nothing, because <code>RPrimitiveType</code> does not use
     * members.
     *
     * @param t No meaning.
     */
    public void setSecondChild(BaseNode t) {
    }


    /**
     * Returns FALSE because primitive types are never nullable
     * (rule NA4).
     *
     * @return FALSE.
     */
    public boolean isNullable() {

        //rule NA4
        return false;
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN4).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public NodeSet leadingNames() {

        //rule LN4
        return new NodeSet(this.name);
    }//leadingNames


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP4). That means, the type does not have
     * recursive occurrences in non-tail positions. Call must have
     * <code>flag == </code>TRUE.
     *
     * @param rootName The name of the top-level type to check.
     * @param flag     Says if recursive occurrence is allowed (TRUE)
     *                 or not.
     * @return TRUE, if constraint 1 is fulfilled and there are
     * no recursive occurrences in non-tail positions,
     * otherwise FALSE.
     */
    public boolean checkTailPosition(Name rootName, boolean flag) {

        //rule TP4
        return true;
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled,
     * otherwise FALSE (rule NH4). That means, the type does not have
     * recursive occurrences with only nullable predecessors. Call must
     * have <code>flag == </code>FALSE.
     *
     * @param flag Says if nullable instances are allowed (TRUE) or
     *             not (FALSE).
     * @return TRUE, if constraint 2 is fulfilled and there is no
     * recursive occurrences with pure nullable predecessors,
     * otherwise FALSE.
     */
    public boolean checkNonNullableHead(boolean flag) {

        //rule NH4
        return true;
    }//checkNonNullableHead


    /**
     * Returns an empty type which is the content of primitive types by
     * definition (rule CN3).
     *
     * @return Empty type.
     */
    public BaseNode content() {

        //rule CN3
        return new EmptyNode();
    }//content


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition (rule UF4).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public BaseNode unfold(Hashtable nameTable) {

        //rule UF4
        return this;
    }//unfold


    /**
     * Returns TRUE, if type is equal to type <code>t</code>, otherwise
     * FALSE.
     *
     * @param t The <code>RType</code> to compare the instance with.
     * @return TRUE if types are equal, otherwise false.
     */
    public boolean equals(BaseNode t) {

        if (t == null)

            return false;

            //t != null
        else

            return (t instanceof PrimitiveNode      //same type ?
                    && this.name.equals(t.getName()));  //same name ?
    }//equals


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     * <code>RIterationType</code>.
     */
    public String toString() {

        return this.name.getNameString();
    }//toString


}//class
