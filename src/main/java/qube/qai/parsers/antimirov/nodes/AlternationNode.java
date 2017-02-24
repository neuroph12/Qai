package qube.qai.parsers.antimirov.nodes;


import qube.qai.parsers.antimirov.IllegalConcatenationException;
import qube.qai.parsers.antimirov.IncompleteTypeException;
import qube.qai.parsers.antimirov.Inequality;
import qube.qai.parsers.antimirov.NoWellformedTypeException;

import java.util.Hashtable;


/**
 * Represents a regular expression consisting of two regular expressions
 * connected by the boolean operator "or".
 * <p>
 * a | b
 * </p>
 * <code>RAlternationType</code>s can be folded into each other to
 * represent alternations consisting of more than two expressions.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see BaseNode
 */
public final class AlternationNode extends BaseNode {


    /**
     * Constructor for alternation type representation.
     *
     * @param child1 The first element of the alternation.
     * @param child2 The second element of the alternation.
     * @throws IncompleteTypeException Occurrs if type is not
     *                                 constructed as valid alternation expression.
     */
    public AlternationNode(BaseNode child1, BaseNode child2)
            throws IncompleteTypeException {

        super(child1, child2);
        this.name = new Name(Name.ALTERNATION);
        this.check();
    }//constructor

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns TRUE if type is nullable, otherwise FALSE (rule NA7).
     *
     * @return TRUE if type is nullable, otherwise FALSE.
     */
    public boolean isNullable()
            throws NoWellformedTypeException {

        //rule NA7
        return (this.child1.isNullable() || this.child2.isNullable());
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN7).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public NodeSet leadingNames() {

        //rule LN7
        NodeSet result = this.child1.leadingNames();

        result = result.union(this.child2.leadingNames());

        return result;
    }//leadingNames


    /**
     * Returns the partial derivatives of the type for all names in
     * <code>names</code> (rule LF7).
     *
     * @param names The set of leading names to compute partial
     *              derivatives for.
     * @return The partial derivatives of the type for all names in
     * <code>names</code>.
     */
    public NodeSet getPartialDerivatives(NodeSet names)
            throws IllegalConcatenationException,
            NoWellformedTypeException {

        //rule LF7
        NodeSet result = this.child1.getPartialDerivatives(names);

        result = result.union(this.child2.getPartialDerivatives(names));

        return result;
    }//getPartialDerivatives


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP7). That means, the type does not have
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

        //rule TP7
        return this.child1.checkTailPosition(rootName, flag)
                && this.child2.checkTailPosition(rootName, flag);
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled, otherwise
     * FALSE (rule NH7). That means, the type does not have recursive
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

        //rule NH7
        return this.child1.checkNonNullableHead(flag)
                && this.child2.checkNonNullableHead(flag);
    }//checkNonNullableHead


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition (rule UF8).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public BaseNode unfold(Hashtable nameTable) {

        //rule UF8
        AlternationNode result = null;

        BaseNode t1 = this.child1.unfold(nameTable);
        BaseNode t2 = this.child2.unfold(nameTable);

        try {
            result = new AlternationNode(t1, t2);
        } catch (IncompleteTypeException ite) {
            //cannot occur here
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

        return (t == null) ?

                false :

                //t != null
                // same type ?
                (t instanceof AlternationNode

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
     * Returns TRUE if type is a valid alternation expression,
     * otherwise FALSE.
     *
     * @return TRUE if type is a valid alternation expression.
     * @throws IncompleteTypeException Occurrs if type is not a
     *                                 valid alternation expression.
     */
    private boolean check()
            throws IncompleteTypeException {

        if (this.child1 != null && this.child2 != null)
            return true;
        else
            throw new IncompleteTypeException(
                    "Alternation not complete."
            );
    }//check


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     * <code>RAlternationType</code>.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer("(");
        buf.append((this.child1 != null) ?
                this.child1.toString() :
                Name.NULL);
        buf.append(" | ");
        buf.append((this.child2 != null) ?
                this.child2.toString() :
                Name.NULL);
        buf.append(")");
        return buf.toString();
    }//toString


}//class
