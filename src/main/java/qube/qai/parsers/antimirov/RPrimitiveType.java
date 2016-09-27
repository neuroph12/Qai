package qube.qai.parsers.antimirov;


import java.util.Hashtable;


/**
 * Representation of a XML Schema primitive type. It can be the
 * superclass for type classes representing single primitive types like
 * <code>xs:integer</code> or <code>xs:boolean</code>.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see RType
 */
public class RPrimitiveType
        extends RType {


    /**
     * Constructor for <code>RPrimitiveType</code>.
     *
     * @param name Name of the type.
     */
    public RPrimitiveType(RName name) {

        super(null, null);
        this.name = name;
    }//constructor


    /**
     * Does nothing, because <code>RPrimitiveType</code> does not use
     * members.
     *
     * @param t No meaning.
     */
    public void setFirstChild(RType t) {
    }


    /**
     * Does nothing, because <code>RPrimitiveType</code> does not use
     * members.
     *
     * @param t No meaning.
     */
    public void setSecondChild(RType t) {
    }


    /**
     * Returns FALSE because primitive types are never nullable
     * (rule NA4).
     *
     * @return FALSE.
     */
    public boolean isNullable() {

        //rule NA4
        return false;
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN4).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public Set leadingNames() {

        //rule LN4
        return new Set(this.name);
    }//leadingNames


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE (rule TP4). That means, the type does not have
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

        //rule TP4
        return true;
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled,
     * otherwise FALSE (rule NH4). That means, the type does not have
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

        //rule NH4
        return true;
    }//checkNonNullableHead


    /**
     * Returns an empty type which is the content of primitive types by
     * definition (rule CN3).
     *
     * @return Empty type.
     */
    public RType content() {

        //rule CN3
        return new REmptyType();
    }//content


    /**
     * Returns an unfolded copy of the type with every recursive
     * occurrence once replaced by its definition (rule UF4).
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public RType unfold(Hashtable nameTable) {

        //rule UF4
        return this;
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

            //t != null
        else

            return (t instanceof RPrimitiveType      //same type ?
                    && this.name.equals(t.getName()));  //same name ?
    }//equals


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     *         <code>RIterationType</code>.
     */
    public String toString() {

        return this.name.getNameString();
    }//toString


}//class
