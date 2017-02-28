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

/**
 * Represents a boolean array. This is needed for computing power sets
 * in the course of the subtyping algorithm.  For a set <code>S</code>
 * of types <code>t</code>[1]...<code>t</code>[n] its power set is
 * computed by defining a <code>BitArray</code> <code>A</code> with
 * length <code>n</code>, starting with all bits from
 * <code>A</code>[0]...<code>A</code>[n-1] being FALSE. Then each
 * addition of value 1 to <code>A</code> represents an element of the
 * power set <code>P</code>(<code>S</code>) till all bits in
 * <code>A</code> are TRUE.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see AlternationNode
 */
public class BitArray {


    /**
     * Represents boolean field with each bit as an element.
     */
    private int data[];


    /**
     * Number of legal index positions.
     */
    private int size;


    /**
     * Number of bits per data cell. (Default is type <code>int</code>
     * and number of bits of <code>int</code> is 32.)
     */
    private int typeSize = 32;


    /**
     * Initializes array of size <code>size</code>.
     * First element has index 0, last element has index
     * <code>size</code>-1.
     *
     * @param size Number of boolean flags available.
     */
    public BitArray(int size) {

        // initialize array data
        int cells = ((size - 1) / this.typeSize) + 1;
        this.data = new int[cells];
        for (int i = 0; i < data.length; i++)
            data[i] = 0;

        // store number of  bits available
        this.size = size;

    }//constructor


    /**
     * Returns value of element with index <code>i</code>.
     *
     * @param i Index of Element whose value is the return value.
     * @return Value of Element <code>i</code>.
     * @throws BitArrayIndexException Occurrs if an index position out
     *                                of bounds of the <code>BitArray</code> is tried to be
     *                                accessed.
     */
    public boolean getElement(int i)
            throws BitArrayIndexException {

        if ((i > this.size - 1) || (i < 0))

            throw new BitArrayIndexException(i, this.size);

        else

            return (data[i / this.typeSize] & (1 << i % this.typeSize)) != 0;
    }//getElement


    /**
     * Assigns value <code>v</code> to the element with index
     * <code>i</code>.
     *
     * @param i Index of Element to set value.
     * @param v New value for element <code>i</code>.
     * @throws BitArrayIndexException Occurrs if an index position out
     *                                of bounds of the <code>BitArray</code> is tried to be
     *                                accessed.
     */
    public void setElement(int i, boolean v)
            throws BitArrayIndexException {

        if ((i > this.size - 1) || (i < 0))

            throw new BitArrayIndexException(i, this.size);

        else {

            int index = i / this.typeSize;
            int mask = (1 << i % this.typeSize);
            data[index] = v ? data[index] | mask : data[index] & ~mask;
        }//if else

    }//setElement


    /**
     * Sets all elements in the <code>BitArray</code> to the value of
     * <code>val</code>.
     *
     * @param val The new value for all elements.
     */
    public void setAll(boolean val) {

        int index, mask;

        // set all elements to TRUE
        if (val) {

            for (int i = 0; i < this.size; i++) {

                index = i / this.typeSize;
                mask = (1 << i % this.typeSize);
                data[index] = data[index] | mask;
            }//for

            // set all elements to FALSE
        } else {

            for (int i = 0; i < data.length; i++) {

                data[i] = 0;
            }//for

            /*
              Alternative solution: conjunction of each bit with 0

              for (int i=0; i<this.size; i++) {

                  index = i/this.typeSize;
              mask = (1 << i%this.typeSize);
              data[index] = data[index] & ~mask;
              }
            */

        }// if else
    }//setAll


    /**
     * Returns TRUE, if all elements have the value <code>val</code>,
     * otherwise FALSE.
     *
     * @param val Value to compare all flags with.
     * @return TRUE, if all flags have the value <code>val</code>,
     * otherwise FALSE.
     */
    public boolean all(boolean val) {

        boolean result = true;

        // all true?
        if (val) {

            for (int i = 0; (i < this.size && result); i++)
                result =
                        result
                                && ((data[i / this.typeSize] & (1 << i % this.typeSize)) != 0);

            // all false?
        } else {

            for (int i = 0; (i < data.length && result); i++)
                result = result && ((data[i] == 0) ? true : false);
        }

        return result;
    }//all


    /**
     * Returns the number of elements (legal index positons) defined in
     * the array.
     *
     * @return The number of elements in the array.
     */
    public int size() {

        return this.size;
    }//size


    /**
     * Interprets the <code>BitArray</code> as a binary number and adds
     * 1 to it if this is possible without exceeding
     * <code>size</code>.
     */
    public void add() {

        if (this.all(true)) return;

        for (int i = 0; i < this.size; i++) {

            int arrayIndex = i / this.typeSize;
            int mask = (1 << i % this.typeSize);

            // if element at position n is 1...
            if ((data[arrayIndex] & mask) != 0)

                //...set it to 0 and move on
                data[arrayIndex] = data[arrayIndex] & ~mask;

            else {

                //...set it to 1 and ready
                data[arrayIndex] = data[arrayIndex] | mask;
                return;
            }
        }
    }//add


    /**
     * Interprets the <code>BitArray</code> as a binary number and
     * subtracts 1 from it if this is possible. Result must be
     * greater or equal 0.
     */
    public void subtract() {

        if (this.all(false)) return;

        for (int i = 0; i < this.size; i++) {

            int arrayIndex = i / this.typeSize;
            int mask = (1 << i % this.typeSize);

            // if element at position n is 1...
            if ((data[arrayIndex] & mask) != 0) {

                //...set it to 0 and ready
                data[arrayIndex] = data[arrayIndex] & ~mask;
                return;

            } else

                //...set it to 1 and move on
                data[arrayIndex] = data[arrayIndex] | mask;

        }//for
    }//subtract


    /**
     * Constructs a left-deep <code>RAlternationType</code> connecting all
     * types contained in <code>pairs</code> whose index is marked as
     * TRUE. If <code>firstElem</code> is TRUE, type is constructed using
     * the first element of each pair, otherwise the second.
     * (Implementation of the Sigma-operator, cf. thesis p.18.)
     * <p/>
     * param v   A bit pattern which marks the indices of the type pairs
     * in <code>pairs</code> to insert.
     *
     * @param pairs     The type pairs to construct the new type from.
     *                  If first or second element is used for construction
     *                  depends on the value of <code>firstElem</code>.
     * @param firstElem If TRUE, construct type with first element of
     *                  the marked pairs in <code>pairs</code>, otherwise with
     *                  second.
     * @return A left-deep <code>RAlternationType</code>-tree
     * containing the marked elements in <code>pairs</code>.
     * @throws BitArrayIndexException Occurrs if an index position out
     *                                of bounds of the <code>BitArray</code> is tried to be
     *                                accessed.
     */
    public BaseNode construct(NodeSetElement[] pairs, boolean firstElem)
            throws BitArrayIndexException {
        // result type
        BaseNode ty = null;

        // arrays of same length?
        if (this.size() != pairs.length) {
            //throw new IncompleteTypeException("");
            System.err.println("Arrays not of equal length!");
        }

        // if pattern contains no marks for insertion, return Nothing
        if (this.all(false)) return new NoneNode();

        // step to first non-null type pair to insert
        int i = 0;
        while ((i < this.size())
                && (this.getElement(i) == false
                || ((pairs[i]) == null))) {

            i++;
        }//while

        // if there is a non-null type pair to insert, start insertion
        if (i < this.size()) {

            try {

                ty = (firstElem) ? ((NodePair) pairs[i]).getFirstElement() :
                        //second Element
                        ((NodePair) pairs[i]).getSecondElement();

            } catch (NullPointerException npe) {

                System.err.println("Clause caused exception on position: " + i + ".");
                for (int k = 0; k < pairs.length; k++) {
                    System.err.println("Pos. " + k + ": " + ((pairs[k] != null) ? pairs[k].toString() : "null"));
                }
                System.err.println("---");
            }//catch

            // insert each marked type pair into the return type
            for (int j = i + 1; j < this.size(); j++) {

                if (this.getElement(j) == true) {

                    try {
                        ty = (firstElem) ?
                                // construct s1 from first element
                                new AlternationNode(
                                        ty,
                                        (BaseNode) ((NodePair) pairs[j]).getFirstElement()) :

                                // construct s2 from second element
                                new AlternationNode(
                                        ty,
                                        (BaseNode) ((NodePair) pairs[j]).getSecondElement());

                    } catch (IncompleteTypeException ite) {
                        // cannot occurr here
                    }//catch

                }//if
            }//for

        }//if i

        return ty;
    }//construct


    /**
     * Returns a <code>String</code> representation of the
     * <code>BitArray</code>.  Rightmost bit is index 0.
     *
     * @return <code>BitArray</code> data as a <code>String</code> with
     * rightmost bit as index 0.
     */
    public String toString() {

        int numBits = data.length * this.typeSize;
        int maxArrayIndex = data.length - 1;
        int mask = 1;

        //char[] chars = new char[numBits];
        char[] chars = new char[this.size];

        //int pos = this.typeSize - 1;
        int pos = chars.length - 1;

        /*
        // version commented out prints allocated range
        // current version prints only defined data range

        for (int j = maxArrayIndex; j >= 0; j--) {
          for (int i=0; i<this.typeSize; ++i) {
            chars[pos-i] = (data[j] & (mask << i)) != 0 ? '1' : '0';
          }
          pos += this.typeSize;
        }
        */

        for (int j = 0; j <= maxArrayIndex; j++) {
            for (int i = 0; i < this.typeSize; i++) {
                if (j == maxArrayIndex && (i == (this.size % this.typeSize)))
                    return new String(chars);
                chars[pos - i] = (data[j] & (mask << i)) != 0 ? '1' : '0';
            }//for i
            pos -= this.typeSize;
        }//for j

        return new String(chars);
    }//toString


}//class
