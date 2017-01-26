package qube.qai.parsers.antimirov.nodes;


import qube.qai.parsers.antimirov.Inequality;

import java.util.Hashtable;


/**
 * Represents the regular expression matching nothing.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see BaseNode
 */
public final class NoneNode
        extends BaseNode {


    /**
     * Sole constructor for type none.
     */
    public NoneNode() {

        super(null, null);
        this.name = new Name(Name.NONE);
    }//constructor

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Does nothing, because <code>RNoneType</code> does not use members.
     *
     * @param t No meaning.
     */
    public void setFirstChild(BaseNode t) {
    }


    /**
     * Does nothing, because <code>RNoneType</code> does not use members.
     *
     * @param t No meaning.
     */
    public void setSecondChild(BaseNode t) {
    }


    /**
     * Returns FALSE because the none type is not nullable (rule NA1).
     *
     * @return FALSE.
     */
    public boolean isNullable() {

        //rule NA1
        return false;
    }//isNullable


    /**
     * Returns an empty set because the none type does not have any
     * names (rule LN1).
     *
     * @return Empty set.
     */
    public NodeSet leadingNames() {

        //rule LN1
        return new NodeSet();
    }//leadingNames


    /**
     * Returns the partial derivatives of the type for all names in
     * <code>names</code> (rule LF1).
     *
     * @param names The set of leading names to compute partial
     *              derivatives for.
     * @return The partial derivatives of the type for all names in
     *         <code>names</code>.
     */
    public NodeSet getPartialDerivatives(NodeSet names) {

        //rule LF1
        return new NodeSet();
    }//getPartialDerivatives


    /**
     * Returns a concatenation of the instance with type <code>r</code>
     * (rule CL4).
     *
     * @param r The type to concatenate the instance to.
     * @return Concatenation of the instance with type <code>r</code>.
     */
    public BaseNode concatenate(BaseNode r) {

        //rule CL4
        return this;
    }// concatenate


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP1). That means, the type does not have
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
    public boolean checkTailPosition(Name rootName, boolean flag) {

        //rule TP1
        return true;
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled, otherwise
     * FALSE (rule NH1). That means, the type does not have recursive
     * occurrences with only nullable predecessors. Call must have
     * <code>flag == </code>FALSE.
     *
     * @param flag Says if nullable instances are allowed (TRUE) or
     *             not (FALSE).
     * @return TRUE, if constraint 2 is fulfilled and there is no
     *         recursive occurrences with pure nullable predecessors,
     *         otherwise FALSE.
     */
    public boolean checkNonNullableHead(boolean flag) {

        //rule NH1
        return true;
    }//checkNonNullableHead


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition (rule UF1).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public BaseNode unfold(Hashtable nameTable) {

        //rule UF1
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

        return (t instanceof NoneNode);
    }//equals


    /**
     * Returns a <code>String</code> representation of the type.
     *
     * @return A <code>String</code> representation of the type.
     */
    public String toString() {

        return Name.NONE;
    }//toString


}//class
