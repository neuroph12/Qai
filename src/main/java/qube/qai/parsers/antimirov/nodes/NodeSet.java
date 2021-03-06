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

import java.util.HashSet;
import java.util.Iterator;


/**
 * Represents a set. <code>Set</code> aggregates elements of type
 * <code>SetElement</code>. Adding, removing and containment check on
 * <code>Set</code>s are done by equality and by reference.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Concatenable
 * @see NodeSetElement
 */
public final class NodeSet
        implements Concatenable, Iterable {


    /**
     * Inner representation of the set.
     */
    private HashSet<NodeSetElement> mySet;


    /**
     * Constructor for class Set.
     */
    public NodeSet() {

        this.mySet = new HashSet<NodeSetElement>();
    }//constructor


    /**
     * Constructor for class Set. Adds <code>e</code> to the empty set.
     *
     * @param e The element to be added to the empty set.
     */
    public NodeSet(NodeSetElement e) {

        this.mySet = new HashSet<NodeSetElement>();
        this.mySet.add(e);
    }//constructor


    public Iterator<NodeSetElement> iterator() {
        return mySet.iterator();
    }

    /**
     * Returns the number of elements in the <code>Set</code>.
     *
     * @return The number of elements in the <code>Set</code>.
     */
    public int size() {

        return this.mySet.size();
    }//size


    /**
     * Adds element <code>e</code> to the <code>Set</code>. Returns TRUE
     * if operation was successfull, otherwise it returns FALSE.
     * Duplicates are eliminated.
     *
     * @param e The element to compare the instance to.
     * @return TRUE if element was successfully added, otherwise FALSE.
     */
    public boolean add(NodeSetElement e) {

        if (this.mySet.isEmpty() == false) {

            if (this.contains(e))
                return false;
        }//if

        // Try to add s
        return (this.mySet.add(e));

    }//add


    /**
     * Removes <code>SetElement</code> <code>e</code> from the
     * <code>Set</code> instance. If <code>e</code> is not in the
     * <code>Set</code> but an element t with a content equal to
     * <code>e</code>, t is removed.
     *
     * @param e The element to remove.
     * @return TRUE, if removal was successful, otherwise FALSE.
     */
    public boolean remove(NodeSetElement e) {

        NodeSetElement t;
        boolean result = true;

        if (this.mySet.remove(e) == false) {
            result = false;

            Iterator it = this.mySet.iterator();
            while (it.hasNext()) {

                t = (NodeSetElement) it.next();

                if (t.equals(e)) {
                    result = this.mySet.remove(t);
                    break;
                }
            }//while
        }//if

        return result;
    }//remove


    /**
     * Checks if element <code>e</code> or an element with equal content
     * to <code>e</code> is in the <code>Set</code>.
     *
     * @param e The element to check if it is contained.
     * @return TRUE, if element is contained in the set, otherwise
     * FALSE.
     */
    public boolean contains(NodeSetElement e) {

        // if set is empty...
        if (this.mySet.isEmpty())

            return false;

            // if set is not empty, make comparison
            // on each element to t
        else {

            if (this.mySet.contains(e))
                return true;

            // check every element for equality by value
            Iterator it = this.mySet.iterator();
            while (it.hasNext()) {
                if (((NodeSetElement) it.next()).equals(e))
                    // if element is already in the set, break
                    return true;
            }//while

            return false;

        }//else
    }//contains


    /**
     * Unifies the instance with <code>Set</code> <code>s</code>.
     * Duplicates are eliminated.
     *
     * @param s The <code>Set</code> to unify the instance with.
     * @return A union of the <code>Set</code> and <code>s</code>.
     */
    public NodeSet union(NodeSet s) {

        NodeSet result = null;

        if (s != null) {

            result = (NodeSet) this.clone();

            Iterator it = s.mySet.iterator();
            while (it.hasNext())
                result.add((NodeSetElement) it.next());

        }

        return result;

    }//union


    /**
     * Removes all elements from the <code>Set</code>.
     */
    public void clear() {

        this.mySet.clear();
    }//clear


    /**
     * Returns TRUE, if <code>Set</code> is empty, otherwise FALSE.
     *
     * @return TRUE, if <code>Set</code> is empty, otherwise FALSE.
     */
    public boolean isEmpty() {

        return this.mySet.isEmpty();
    }//isEmpty


    /**
     * Returns the elements in the <code>Set</code> as an array. This is
     * useful if elements must have a defined order.
     *
     * @return All elements of the <code>Set</code> in an array.
     */
    public NodeSetElement[] toArray() {

        return (NodeSetElement[]) this.mySet.toArray(new NodeSetElement[1]);
    }//toArray


    /**
     * Returns a shallow copy of this instance. The elements itselves
     * are not cloned.
     *
     * @return Shallow copy of this instance.
     */
    public Object clone() {

        NodeSet copy = new NodeSet();
        copy.mySet = (HashSet) this.mySet.clone();
        return (Object) copy;
    }//clone


    /**
     * Returns concatenation of the instance with type <code>r</code>.
     *
     * @param r The type to concatenate the instance with.
     * @return Concatenation of the instance with type <code>r</code>.
     */
    public NodeSet concatenate(BaseNode r)
            throws IllegalConcatenationException {

        Iterator it;
        Concatenable c1 = null;
        Concatenable c2 = null;

        switch (this.size()) {

            //rule CL3
            case 0:

                return this;

            //pass application of rules CL1, CL2, CL4, CL5, CL6
            case 1:

                it = this.mySet.iterator();
                try {

                    c1 = (Concatenable) it.next();

                } catch (ClassCastException cce) {

                    throw new IllegalConcatenationException(
                            "No Concatenables in Set."
                    );
                }

                return c1.concatenate(r);

            //rule CL7
            case 2:

                it = this.mySet.iterator();
                try {

                    c1 = (Concatenable) it.next();
                    c2 = (Concatenable) it.next();

                } catch (ClassCastException cce) {

                    throw new IllegalConcatenationException(
                            "No Concatenables in Set."
                    );
                }
                NodeSet result = c1.concatenate(r);
                result = result.union(c2.concatenate(r));

                return result;

            default:

                throw new IllegalConcatenationException(
                        "No Concatenables in Set."
                );

        }//switch

    }//concatenate


    /**
     * Returns a <code>String</code> representation of the
     * <code>Set</code>.
     *
     * @return <code>String</code> representation of the
     * <code>Set</code>.
     */
    public String toString() {

        return this.mySet.toString();
    }//toString


}//class
