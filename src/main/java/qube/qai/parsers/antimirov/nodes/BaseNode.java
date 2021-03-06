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


import org.openrdf.annotations.Iri;
import qube.qai.parsers.antimirov.IllegalConcatenationException;
import qube.qai.parsers.antimirov.IncompleteTypeException;
import qube.qai.parsers.antimirov.IrregularContentRequestException;
import qube.qai.parsers.antimirov.NoWellformedTypeException;
import qube.qai.services.implementation.UUIDService;

import java.io.Serializable;
import java.util.Hashtable;

import static qube.qai.main.QaiConstants.BASE_URL;


/**
 * Abstract superclass for all intern type representation classes. Types
 * are represented by regular expressions and the whole subtyping
 * process works on regular expressions. <code>RType</code> defines the
 * common interface for using regular expressions and their attributes.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 */

@Iri(BASE_URL + "BaseNode")
public abstract class BaseNode implements VisitableNode, Serializable {


    @Iri(BASE_URL + "uuid")
    protected String uuid;

    @Iri(BASE_URL + "parent")
    protected BaseNode parent;

    /**
     * The name of the type.
     */
    protected Name name;

    /**
     * The representation of the first inner type that is modified by
     * this type.
     */
    @Iri(BASE_URL + "child1")
    protected BaseNode child1;

    /**
     * The representation of the second inner type that is modified by
     * this type.
     */
    @Iri(BASE_URL + "child2")
    protected BaseNode child2;

    public BaseNode() {
        this.uuid = UUIDService.uuidString();
    }

    /**
     * Empty Constructor.
     */
    public BaseNode(String name) {
        this();
        this.name = new Name(name);
    }


    /**
     * Constructor for type representations with two children.
     *
     * @param child1 The type representation of the first child of the
     *               current type.
     * @param child2 The type representation of the second child of the
     *               current type.
     */
    public BaseNode(BaseNode child1, BaseNode child2) {
        this("BaseNode");
        this.child1 = child1;
        this.child2 = child2;
        if (child1 != null) {
            child1.setParent(this);
        }
        if (child2 != null) {
            child2.setParent(this);
        }
    }//constructor


    public abstract Object accept(NodeVisitor visitor, Object data);

    public Object childrenAccept(NodeVisitor visitor, Object data) {
        data = this.accept(visitor, data);
        if (child1 != null) {
            data = child1.childrenAccept(visitor, data);
        }
        if (child2 != null) {
            data = child2.childrenAccept(visitor, data);
        }
        return data;
    }

    public void addChild(BaseNode child) {
        child.setParent(this);
        if (getFirstChild() == null) {
            setFirstChild(child);
        } else if (getSecondChild() == null) {
            setSecondChild(child);
        } else {
            ConcatenationNode concat = new ConcatenationNode(getSecondChild(), child);
            setSecondChild(concat);
        }
    }

    /**
     * Returns the name of the type. If type has no name,
     * <code>null</code> is returned.
     *
     * @return The name of the type or <code>null</code> if type has no
     * name.
     */
    public Name getName() {
        return this.name;
    }//getName

    public void setName(Name name) {
        this.name = name;
    }

    /**
     * Sets first child of the type to <code>t</code>.
     *
     * @param t new first child
     */
    public void setFirstChild(BaseNode t) {
        this.child1 = t;
        if (child1 != null) {
            child1.setParent(this);
        }
    }//setFirstChild


    /**
     * Returns first child of the type.
     *
     * @return First child of the type.
     */
    public BaseNode getFirstChild() {

        return this.child1;
    }//getFirstChild


    /**
     * Sets second child of the type to <code>t</code>.
     *
     * @param t new second child
     */
    public void setSecondChild(BaseNode t) {
        this.child2 = t;
        if (child2 != null) {
            child2.setParent(this);
        }
    }//setSecondChild


    /**
     * Returns second child of the type.
     *
     * @return Second child of the type.
     */
    public BaseNode getSecondChild() {
        return this.child2;
    }//getSecondChild


    /**
     * Returns TRUE if type is nullable, otherwise FALSE.
     *
     * @return TRUE if type is nullable, otherwise FALSE.
     * @throws NoWellformedTypeException Occurrs if
     *                                   <code>isNullable()</code> is
     *                                   applied on recursive
     *                                   occurrences.
     */
    public abstract boolean isNullable()
            throws NoWellformedTypeException;


    /**
     * Returns the leading names of the type.
     *
     * @return <code>Set</code> containing the leading names.
     */
    public abstract NodeSet leadingNames();


    /**
     * Returns the partial derivatives of the type for all names in
     * <code>names</code> (rule LF3 with modification is default). The
     * default implementation of this method implements rule 2.4 and is
     * to be overridden by those type classes which use other rules.
     *
     * @param names The set of leading names to compute partial
     *              derivatives for.
     * @return The partial derivatives of the type for all names in
     * <code>names</code>.
     * @throws IllegalConcatenationException Occurrs if during the
     *                                       computation of partial
     *                                       derivatives an illegal
     *                                       concatenation is tried to be
     *                                       performed.
     * @throws NoWellformedTypeException     Occurrs if method is
     *                                       applied on recursive
     *                                       occurrences.
     */
    public NodeSet getPartialDerivatives(NodeSet names)
            throws IllegalConcatenationException,
            NoWellformedTypeException {

        //rule LF3 is default (to be overriden)
        NodeSet result = new NodeSet();
        BaseNode content = null;

        // Modification of LF3: compute not lf but partial derivatives
        if (names != null && names.contains(this.getName())) {

            try {

                result = new NodeSet(
                        (NodeSetElement)
                                new NodePair(this.content(), new EmptyNode())
                );

            } catch (IrregularContentRequestException icre) {

                System.err.println(
                        "Content of type " + this.toString() + " not defined."
                );

                return result;
            }//try catch

        }//if

        return result;
    }//getPartialDerivatives


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled. That
     * means, the type does not have recursive occurrences in
     * non-tail positions. Call must have <code>flag == </code>TRUE.
     *
     * @param rootName The name of the top-level type to check.
     * @param flag     Says if recursive occurrence is allowed (TRUE)
     *                 or not.
     * @return TRUE, if constraint 1 is fulfilled and there are
     * no recursive occurrences in non-tail positions,
     * otherwise FALSE.
     */
    public abstract boolean checkTailPosition(Name rootName,
                                              boolean flag);


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled. That
     * means, the type does not have recursive occurrences with
     * only nullable predecessors. Call must have <code>flag ==
     * </code>FALSE.
     *
     * @param flag Says if nullable instances are allowed (TRUE) or
     *             not (FALSE).
     * @return TRUE, if constraint 2 is fulfilled and there is no
     * recursive occurrences with pure nullable predecessors,
     * otherwise FALSE.
     */
    public abstract boolean checkNonNullableHead(boolean flag);


    /**
     * Returns TRUE if type fulfills wellformedness constraints 1 and 2,
     * otherwise FALSE.
     *
     * @return TRUE if type is wellformed, otherwise FALSE.
     */
    public boolean isWellformed()
            throws NoWellformedTypeException {

        if (this.checkTailPosition(this.getName(), true) == false)
            throw new NoWellformedTypeException(
                    "Node: " + this.toString()
                            + " has recursive occurrences in non-tail positions.");

        if (this.checkNonNullableHead(false) == false)
            throw new NoWellformedTypeException(
                    "Node: " + this.toString()
                            + " has a nullable head.");

        return true;

    }//isWellformed


    /**
     * Returns the content of the type if content is defined
     * for this type, otherwise an
     * <code>IrregularContentRequestException</code> is thrown. The
     * default implementation of this method does only throw such an
     * exception. It is to be overridden by methods in type classes
     * which represent types on which content() is defined.
     *
     * @return Content of the type.
     * @throws IrregularContentRequestException Occurrs if
     *                                          <code>content()</code> is called on a type for whom no
     *                                          content is defined.
     */
    public BaseNode content()
            throws IrregularContentRequestException {

        // standard is: no rule applicable
        StringBuffer buf = new StringBuffer("Node ");
        buf.append((this.name != null) ? this.name + " " : "");
        buf.append("has no defined content.");

        throw new IrregularContentRequestException(buf.toString());
    }//content


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition.
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public abstract BaseNode unfold(Hashtable nameTable);


    /**
     * Returns a concatenation of the instance with type <code>r</code>
     * (rule CL6 is default).
     * The default implementation of this method implements rule 2.13
     * and is to be overridden by methods in type classes which use
     * other rules.
     *
     * @param r The type to concatenate the instance to.
     * @return A concatenation of the instance with type <code>r</code>.
     */
    public BaseNode concatenate(BaseNode r)
            throws IllegalConcatenationException {

        //rule CL6 (to be overriden)
        try {

            return new ConcatenationNode(this, r);
        } catch (IncompleteTypeException ite) {

            throw new IllegalConcatenationException(
                    "Concatenation with null.");
        }
    }//concatenate


    /**
     * Returns TRUE, if type is equal to type <code>t</code>.
     *
     * @param t The <code>RType</code> to compare the instance with.
     * @return TRUE if types are equal, otherwise false.
     */
    public abstract boolean equals(BaseNode t);

    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the type.
     */
    public abstract String toString();

    /*public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }*/

    public BaseNode getParent() {
        return parent;
    }

    public void setParent(BaseNode parent) {
        this.parent = parent;
    }

}//class
