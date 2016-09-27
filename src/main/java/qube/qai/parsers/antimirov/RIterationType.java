package qube.qai.parsers.antimirov;


import java.util.Hashtable;


/**
 * Represents an iterated regular expression. An iteration has two
 * iteration bounds. The lower bound n defines the minimal number of
 * occurrences of the iterated type, the upper bound m defines the
 * maximal number of occurrences .
 * <p>
 * The general form of an iterated expression is a{n, m}. For some
 * values of n and m the resulting expressions have alternative
 * notations.
 * a{0, INIFINITY} = a*  (Kleene Star expression)
 * a{1, INFINITY}  = a+  (non-empty iteration)
 * a{0, 1}         = a?  (choice)
 * </p>
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see RType
 */
public final class RIterationType
        extends RType {


    /**
     * Lower bound for iteration.
     */
    private int minOccurrs;


    /**
     * Upper bound for iteration.
     */
    private int maxOccurrs;


    /**
     * Constructor for iteration type representation.
     *
     * @param child The type to be iterated.
     * @throws IncompleteTypeException Occurrs if type is not
     *                                 constructed as valid concatenation expression.
     */
    public RIterationType(RType child)
            throws IncompleteTypeException {

        super(child, null);
        this.minOccurrs = 0;
        this.maxOccurrs = RName.INFINITY;
        this.name = new RName(RName.ITERATION);
        this.check();
    }//constructor


    /**
     * Constructor for class <code>RIterationType</code>. If
     * <code>maxOccurrs</code> < <code>minOccurrs</code>, the lower bound
     * is taken as upper bound.
     *
     * @param child      The type to be iterated.
     * @param minOccurrs The lower bound for iteration.
     * @param maxOccurrs The upper bound for iteration.
     * @throws IncompleteTypeException Occurrs if type is not
     *                                 constructed as valid concatenation expression.
     */
    public RIterationType(RType child, int minOccurrs, int maxOccurrs) {

        super(child, null);
        this.minOccurrs = (minOccurrs < 0) ? 0 : minOccurrs;

        if (maxOccurrs == RName.INFINITY)
            this.maxOccurrs = RName.INFINITY;
        else
            this.maxOccurrs = (maxOccurrs < minOccurrs) ?
                    minOccurrs :
                    maxOccurrs;

        this.name = new RName(RName.ITERATION);
    }//constructor


    /**
     * Does nothing, because <code>RIterationType</code> does not use
     * its second member.
     *
     * @param t No meaning.
     */
    public void setSecondChild(RType t) {
    }


    /**
     * Returns lower bound for iteration.
     *
     * @return Lower bound for iteration.
     */
    public int getMinOccurrs() {

        return this.minOccurrs;
    }


    /**
     * Returns upper bound for iteration.
     *
     * @return Upper bound for iteration.
     */
    public int getMaxOccurrs() {

        return this.maxOccurrs;
    }


    /**
     * Returns TRUE if type is nullable, otherwise FALSE (rule NA8).
     *
     * @return TRUE if type is nullable, otherwise FALSE.
     */
    public boolean isNullable()
            throws NoWellformedTypeException {

        //rule NA8
        return (this.minOccurrs == 0) ? true : this.child1.isNullable();
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN8).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public Set leadingNames() {

        //rule LN8
        Set result = (this.child1 != null) ?
                this.child1.leadingNames() :
                null;

        return result;
    }//leadingNames


    /**
     * Returns the partial derivatives of the type for all names in
     * <code>names</code> (rule LF8).
     *
     * @param names The set of leading names to compute partial
     *              derivatives for.
     * @return The partial derivatives of the type for all names in
     *         <code>names</code>.
     */
    public Set getPartialDerivatives(Set names)
            throws IllegalConcatenationException,
            NoWellformedTypeException {

        //rule LF8
        Set result = this.child1.getPartialDerivatives(names);

        result = result.concatenate(this);

        return result;
    }//getPartialDerivatives


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP8). That means, the type does not have
     * recursive occurrences in non-tail positions. Call must have
     * <code>flag == </code>TRUE.
     *
     * @param rootName The name of the top-level type to check.
     * @param flag     Says if recursive occurrence is allowed (TRUE)
     *                 or not.
     * @return TRUE, if constraint 1 is fulfilled and there are
     *         no recursive occurrences in non-tail positions,
     *         otherwise FALSE.
     */
    public boolean checkTailPosition(RName rootName, boolean flag) {

        //rule TP8
        if (this.maxOccurrs == 1)
            return this.child1.checkTailPosition(rootName, flag);
        else
            return this.child1.checkTailPosition(rootName, false);
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled,
     * otherwise FALSE (rule NH8). That means, the type does not have
     * recursive occurrences with only nullable predecessors. Call must
     * have <code>flag == </code>FALSE.
     *
     * @param flag Says if nullable instances are allowed (TRUE) or
     *             not (FALSE).
     * @return TRUE, if constraint 2 is fulfilled and there is no
     *         recursive occurrences with pure nullable predecessors,
     *         otherwise FALSE.
     */
    public boolean checkNonNullableHead(boolean flag) {

        //rule NH8
        return this.child1.checkNonNullableHead(flag);
    }//checkNonNullableHead


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition (rule UF9).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public RType unfold(Hashtable nameTable) {

        //rule UF9
        RType t1 = this.child1.unfold(nameTable);
        return new RIterationType(t1, this.minOccurrs, this.maxOccurrs);
    }//unfold


    /**
     * Returns TRUE, if type is equal to type <code>t</code>, otherwise
     * FALSE.
     *
     * @param t The <code>RType</code> to compare the instance with.
     * @return TRUE if types are equal, otherwise false.
     */
    public boolean equals(RType t) {

        if (t == null)

            return false;

            // t != null
        else {
            boolean result = true;

            // same type?
            if (t instanceof RIterationType) {

                // child equal ?
                if (this.child1 != null)

                    result = result && this.child1.equals(t.getFirstChild());

                else
                    result = result && (t.getFirstChild() == null);

                // bounds equal ?
                result =
                        result
                                && this.minOccurrs == ((RIterationType) t).getMinOccurrs()
                                && this.maxOccurrs == ((RIterationType) t).getMaxOccurrs();

                return result;

            } else
                return false;
        }
    }//equals


    /**
     * Returns TRUE if type is a valid iteration expression,
     * otherwise FALSE.
     *
     * @return TRUE if type is a valid iteration expression
     *         otherwise FALSE.
     */
    private boolean check()
            throws IncompleteTypeException {

        if (this.child1 != null)
            return true;
        else
            throw new IncompleteTypeException("Iteration not complete.");
    }//check


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     *         <code>RIterationType</code>.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer("(");
        buf.append((this.child1 != null) ?
                this.child1.toString() :
                RName.NULL);
        // choose alternative notation
        buf.append((this.minOccurrs == 0) ?
                (this.maxOccurrs == RName.INFINITY) ?
                        ")*"
                        : (this.maxOccurrs == 1) ?
                        ")?"
                        : "{" + this.minOccurrs + "-" + this.maxOccurrs + "})"
                : (this.minOccurrs == 1) ?
                (this.maxOccurrs == RName.INFINITY) ?
                        ")+"
                        : "{" + this.minOccurrs + "-" + this.maxOccurrs + "})"
                : "{" + this.minOccurrs + "-" + this.maxOccurrs + "})"
        );
        return buf.toString();
    }//toString


}//class
