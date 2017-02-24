package qube.qai.parsers.antimirov.nodes;


import qube.qai.parsers.antimirov.*;

import java.util.Hashtable;


/**
 * Represents a regular expression consisting of a sequence of two
 * regular expressions a and b. Thereby, a and b are concatenated as
 * follows:
 * <p>
 * a b
 * </p>
 * <code>RConcatenationType</code>s can be folded into each other to
 * represent concatenations consisting of more than two expressions.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see BaseNode
 */
public final class ConcatenationNode
        extends BaseNode {


    /**
     * Constructor for Concatenation type representation.
     *
     * @param child1 The first element of the concatenation.
     * @param child2 The second element of the concatenation.
     * @throws IncompleteTypeException Occurrs if type is not
     *                                 constructed as valid concatenation expression.
     */
    public ConcatenationNode(BaseNode child1, BaseNode child2)
            throws IncompleteTypeException {

        super(child1, child2);
        this.name = new Name(Name.CONCATENATION);
        this.check();
    }//constructor

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns TRUE if type is nullable, otherwise FALSE (rule NA6).
     *
     * @return TRUE if type is nullable, otherwise FALSE.
     * @throws NoWellformedTypeException Occurrs if
     *                                   <code>isNullable()</code> is applied on
     *                                   recursive occurrences.
     */
    public boolean isNullable()
            throws NoWellformedTypeException {

        // rule NA6
        return (this.child1.isNullable() && this.child2.isNullable());
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN6).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public NodeSet leadingNames() {

        // rule LN6
        NodeSet result = this.child1.leadingNames();

        try {

            if (this.child1.isNullable()) {
                result = result.union(this.child2.leadingNames());
            }
        } catch (TypeException te) {

            System.err.println(te.getMessage());
            te.printStackTrace();
            return new NodeSet();
        }

        return result;
    }//leadingNames


    /**
     * Returns the partial derivatives of the type for all names in
     * <code>names</code> (rule LF5 and LF6).
     *
     * @param names The set of leading names to compute partial
     *              derivatives for.
     * @return The partial derivatives of the type for all names in
     * <code>names</code>.
     */
    public NodeSet getPartialDerivatives(NodeSet names)
            throws IllegalConcatenationException,
            NoWellformedTypeException {

        // (part of rules LF5 and LF6)
        NodeSet result = this.child1.getPartialDerivatives(names);

        result = result.concatenate(this.child2);

        // rule LF6
        if (this.child1.isNullable()) {

            // add linear form of second child
            result = result.union(this.child2.getPartialDerivatives(names));
        }

        return result;
    }//getPartialDerivatives


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP6). That means, the type does not have
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

        // rule TP6
        return this.child1.checkTailPosition(rootName, false)
                && this.child2.checkTailPosition(rootName, flag);
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled,
     * otherwise FALSE (rule NH6). That means, the type does not have
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

        // rule NH6
        boolean result = true;

        try {

            result = (this.child1.isNullable() == false);

        } catch (TypeException te) {

            System.err.println(te.getMessage());
            te.printStackTrace();
        }

        return this.child1.checkNonNullableHead(flag)
                && this.child2.checkNonNullableHead(flag || result);

    }//checkNonNullableHead


    /**
     * Returns an unfolded copy of the instance with every recursive
     * occurrence once replaced by its definition (rule UF7).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public BaseNode unfold(Hashtable nameTable) {

        // rule UF7
        ConcatenationNode result = null;

        BaseNode t1 = this.child1.unfold(nameTable);
        BaseNode t2 = this.child2.unfold(nameTable);

        try {
            result = new ConcatenationNode(t1, t2);
        } catch (IncompleteTypeException ite) {
            //cannot occurr here
        }

        return result;
    }//unfold


    /**
     * Returns TRUE, if type is of equal content to type
     * <code>t</code>, otherwise FALSE.
     *
     * @param t The <code>RType</code> to compare the instance with.
     * @return TRUE if types are equal, otherwise false.
     */
    public boolean equals(BaseNode t) {

        return (t == null) ?

                false :

                //t != null

                // same type ?
                (t instanceof ConcatenationNode

                        // child 1 equal ?
                        && ((this.child1 != null) ?
                        this.child1.equals(t.getFirstChild()) :
                        t.getFirstChild() == null)

                        // child 2 equal ?
                        && ((this.child2 != null) ?
                        this.child2.equals(t.getSecondChild()) :
                        t.getSecondChild() == null)
                );
    }//equals


    /**
     * Returns TRUE if type is a valid concatenation
     * expression, otherwise FALSE.
     *
     * @return TRUE if type is a valid concatenation expression
     * otherwise FALSE.
     */
    private boolean check()
            throws IncompleteTypeException {

        if (this.child1 != null && this.child2 != null)
            return true;
        else
            throw new IncompleteTypeException(
                    "Concatenation not complete."
            );
    }//check


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     * <code>RConcatenationType</code>.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer();
        buf.append((this.child1 != null) ?
                this.child1.toString() :
                Name.NULL);
        buf.append(" ");
        buf.append((this.child2 != null) ?
                this.child2.toString() :
                Name.NULL);
        return buf.toString();
    }//toString


}//class
