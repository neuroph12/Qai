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


import qube.qai.parsers.antimirov.IncompleteTypeException;
import qube.qai.parsers.antimirov.Inequality;

import java.util.Hashtable;


/**
 * Represents an XML BaseNode type. The inner type of a
 * <code>RNodeType</code> represents the content of the type. Special
 * subclasses representing single node types can be derived from this
 * class.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see BaseNode
 */
public class Node
        extends BaseNode {

    public Node() {
    }

    /**
     * Constructor for class node types.
     *
     * @param name The name of the node.
     * @throws IncompleteTypeException Occurrs if type is not
     *                                 constructed as valid node type.
     */
    public Node(Name name)
    //throws IncompleteTypeException {
    {

        super(null, null);
        this.name = name;
        this.check();
    }//constructor


    /**
     * Constructor for node types.
     *
     * @param name  The name of the node.
     * @param child The content of the node.
     * @throws IncompleteTypeException Occurrs if type is not
     *                                 constructed as valid node type.
     */
    public Node(Name name, BaseNode child)
            throws IncompleteTypeException {

        super(child, null);
        this.name = name;
        this.check();
    }//constructor

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Does nothing, because <code>RNodeType</code> does not use its
     * second member.
     *
     * @param t No meaning.
     */
    public void setSecondChild(BaseNode t) {
    }


    /**
     * Returns FALSE because node types are never nullable (rule NA3).
     *
     * @return FALSE.
     */
    public boolean isNullable() {

        //rule NA3
        return false;
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN3).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public NodeSet leadingNames() {

        //rule LN3
        return new NodeSet(this.name);
    }//leadingNames


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP3). That means, the type does not have
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

        //rule TP3
        return true;
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled,
     * otherwise FALSE (rule NH3). That means, the type does not have
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

        //rule NH3
        return true;
    }//checkNonNullableHead


    /**
     * Returns the content of the type (rules CN1 and CN2).
     *
     * @return Content of the type instance.
     */
    public BaseNode content() {

        //rules CN1, CN2
        return (this.child1 != null) ? this.child1 : new EmptyNode();
    }//content


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition (rule UF3).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public BaseNode unfold(Hashtable nameTable) {

        //rule UF3
        BaseNode result = null;

        try {

            result = new Node(
                    new Name(this.name.toString()),
                    (this.child1 != null) ? //recursive occurrence ?
                            this.child1.unfold(nameTable) : //no
                            null //yes
            );

        } catch (IncompleteTypeException ite) {
            //cannot occurr here
        }

        return result;
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
            return (   // same type ?
                    t instanceof Node

                            // equal tag ?
                            && this.name.equals(t.getName())

                            // equal content ?
                            && (this.child1 != null ?
                            this.child1.equals(t.getFirstChild()) :
                            t.getFirstChild() == null
                    )
            );
    }//equals


    /**
     * Returns TRUE if type is a valid node expression,
     * otherwise FALSE.
     *
     * @return TRUE if type is a valid node expression
     * otherwise FALSE.
     */
    public boolean check()
    //throws IncompleteTypeException {
    {

        return (this.name != null);
    }//check


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     * <code>RNodeType</code>.
     */
    public String toString() {

        if (this.child1 == null)
            return (this.name != null) ?
                    this.name.getNameString() :
                    Name.NULL;
        else {
            StringBuffer buf = new StringBuffer((this.name != null) ?
                    this.name.getNameString() :
                    Name.NULL);
            buf.append("[");
            buf.append(this.child1.toString());
            buf.append("]");
            return buf.toString();
        }
    }//toString

}//class
