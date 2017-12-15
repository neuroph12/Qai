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

/**
 * Represents a pair of types <code>t1, t2</code>.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see NodeSetElement
 * @see Concatenable
 */
public final class NodePair
        implements NodeSetElement, Concatenable {


    /**
     * The first type of the pair.
     */
    private BaseNode t1;


    /**
     * The second type of the pair.
     */
    private BaseNode t2;


    /**
     * Constructor for class <code>TypePair</code> using
     * <code>RType</code> instances.
     *
     * @param t1 The first element of the type pair
     * @param t2 The second element of the type pair
     */
    public NodePair(BaseNode t1, BaseNode t2) {

        this.t1 = t1;
        this.t2 = t2;
    }//constructor


    /**
     * Returns the first type of the pair.
     *
     * @return The first type of the pair.
     */
    public BaseNode getFirstElement() {

        return this.t1;
    }//getFirstElement


    /**
     * Sets first element of the type pair to <code>e</code>.
     *
     * @param e The new first element of the type pair.
     */
    public void setFirstElement(BaseNode e) {

        this.t1 = e;
    }//setFirstElement


    /**
     * Returns the second type of the pair.
     *
     * @return The second element of the pair.
     */
    public BaseNode getSecondElement() {

        return this.t2;
    }//getSecondElement


    /**
     * Sets second type of the type pair to <code>e</code>.
     *
     * @param e The new second element of the type pair.
     */
    public void setSecondElement(BaseNode e) {

        this.t2 = e;
    }//setSecondElement


    /**
     * Returns TRUE if <code>e</code> is of equal content to the
     * instance.
     *
     * @param e The SetElement to compare the instance to.
     * @return TRUE if e is of equal content to the instance.
     */
    public boolean equals(NodeSetElement e) {

        NodePair p = null;

        try {

            p = (NodePair) e;
        } catch (ClassCastException cce) {

            return false;
        }

        return this.equals(p);
    }//equals


    /**
     * Returns TRUE, if the content of <code>e</code> is equal to the
     * content of the instance.
     *
     * @return TRUE, if compared elements are of equal content, otherwise
     * FALSE.
     */
    public boolean equals(NodePair p) {

        boolean r1 = true;
        boolean r2 = true;

        // if p is of correct type and not null, start comparison
        if (p != null) {

            BaseNode p1 = p.getFirstElement();
            BaseNode p2 = p.getSecondElement();

            // compare elements
            r1 = (this.t1 != null) ? (t1.equals(p1)) : (p1 == null);
            r2 = (this.t2 != null) ? (t2.equals(p2)) : (p2 == null);

            // if both elements are equal, return TRUE, otherwise FALSE
            return r1 && r2;

            // if p is null, it cannot be equal
        } else
            return false;
    }//equals


    /**
     * Returns a concatenation of the instance with type <code>r</code>.
     *
     * @param r The type to concatenate the instance with.
     * @return A Concatenation of the instance with type <code>r</code>.
     */
    public NodeSet concatenate(BaseNode r)
            throws IllegalConcatenationException {

        //rule CL1
        if (r instanceof NoneNode)
            return new NodeSet();

        //rule CL2
        if (r instanceof EmptyNode)
            return new NodeSet((NodeSetElement) this);

        //pass application of rules CL4, CL5, CL6
        return new NodeSet((NodeSetElement)
                new NodePair(
                        this.getFirstElement(),
                        this.getSecondElement().concatenate(r)
                )
        );

    }//concatenate


    /**
     * Returns a <code>String</code> representation of the
     * <code>TypePair</code>.
     *
     * @return A <code>String</code> representation of the
     * <code>TypePair</code>.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer("<");
        buf.append((t1 == null) ? Name.NULL : this.t1.toString());
        buf.append(", ");
        buf.append((t2 == null) ? Name.NULL : this.t2.toString());
        buf.append(">");

        return buf.toString();
    }//toString


}//class
