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


import qube.qai.parsers.antimirov.IllegalConcatenationException;
import qube.qai.parsers.antimirov.Inequality;

import java.util.Hashtable;


/**
 * Represents the empty type, also called epsilon.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see BaseNode
 */
public final class EmptyNode
        extends BaseNode {


    /**
     * Sole constructor for empty type.
     */
    public EmptyNode() {

        super(null, null);
        this.name = new Name(Name.EMPTY);
    }//constructor

    @Override
    public Object accept(NodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Does nothing, because <code>REmptyType</code> does not use members.
     *
     * @param t No meaning.
     */
    public void setFirstChild(BaseNode t) {
    }


    /**
     * Does nothing, because <code>REmptyType</code> does not use members.
     *
     * @param t No meaning.
     */
    public void setSecondChild(BaseNode t) {
    }


    /**
     * Returns TRUE, because the empty type is nullable (rule NA2).
     *
     * @return TRUE.
     */
    public boolean isNullable() {

        //rule NA2
        return true;
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN2).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public NodeSet leadingNames() {

        //rule LN2
        return new NodeSet();
    }//leadingNames


    /**
     * Returns the partial derivatives of the type for all names in
     * <code>names</code> (rule LF2).
     *
     * @param names The set of leading names to compute partial
     *              derivatives for.
     * @return The partial derivatives of the type for all names in
     * <code>names</code>.
     */
    public NodeSet getPartialDerivatives(NodeSet names) {

        //rule LF2
        return new NodeSet();
    }//getPartialDerivatives


    /**
     * Returns a concatenation of the instance with type <code>r</code>
     * (rule CL5).
     *
     * @param r The type to concatenate the instance to.
     * @return Concatenation of the instance with type <code>r</code>.
     */
    public BaseNode concatenate(BaseNode r)
            throws IllegalConcatenationException {

        // rule CL5
        if (r != null)
            return r;
        else
            throw
                    new IllegalConcatenationException("Concatenation with null.");
    }//concatenate


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP2). That means, the type does not have
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

        //rule TP2
        return true;
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled, otherwise
     * FALSE (rule NH2). That means, the type does not have recursive
     * occurrences with only nullable predecessors. Call must have
     * <code>flag == </code>FALSE.
     *
     * @param flag Says if nullable instances are allowed (TRUE) or
     *             not (FALSE).
     * @return TRUE, if constraint 2 is fulfilled and there is no
     * recursive occurrences with pure nullable predecessors,
     * otherwise FALSE.
     */
    public boolean checkNonNullableHead(boolean flag) {

        //rule NH2
        return true;
    }//checkNonNullableHead


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition (UF2).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public BaseNode unfold(Hashtable nameTable) {

        //rule UF2
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

        return (t instanceof EmptyNode);
    }//equals


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     * <code>RIterationType</code>.
     */
    public String toString() {

        return Name.EMPTY;
    }//toString

//    @thewebsemantic.Id
//    public String getUuid() {
//        return this.uuid;
//    }

}//class
