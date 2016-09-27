package qube.qai.parsers.antimirov;

import java.util.Hashtable;
import java.util.Iterator;


/**
 * Represents a named type.
 * Named types consist of a name and a definition and can be part of
 * other types.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Inequality
 * @see RType
 */
public final class RNameType
        extends RType {


    /**
     * Constructor for recursive occurrences of named types.
     *
     * @param name Name of the type.
     */
    public RNameType(RName name) {

        super(null, null);
        this.name = name;
    }//constructor


    /**
     * Constructor for named types. A new type with given name and
     * definition is constructed.
     *
     * @param name   Name of the type.
     * @param child1 Content of the type.
     */
    public RNameType(RName name, RType child1) {

        super(child1, null);
        this.name = name;
    }//constructor


    /**
     * Does nothing, because <code>RNameType</code> does not use its
     * second member.
     *
     * @param t No meaning.
     */
    public void setSecondChild(RType t) {
    }


    /**
     * Returns TRUE if type is nullable, otherwise FALSE (rule NA5).
     *
     * @return TRUE if type is nullable, otherwise FALSE.
     */
    public boolean isNullable()
            throws NoWellformedTypeException {

        // rule NA5
        // If child1 == null, type is a recursive occurrence.

        if (this.child1 != null)
            return child1.isNullable();
        else
            throw new NoWellformedTypeException("Node: \""
                    + this.toString() + "\" is not wellformed.");
    }//isNullable


    /**
     * Returns the leading names of the type (rule LN5).
     *
     * @return <code>Set</code> containing the leading names.
     */
    public Set leadingNames() {

        //rule LN5
        Set result = (this.child1 != null) ?
                this.child1.leadingNames() :
                new Set(this.getName());

        return result;
    }//leadingNames


    /**
     * Returns the partial derivatives of the type for all names in
     * <code>names</code> (rule LF4).
     *
     * @param names The set of leading names to compute partial
     *              derivatives for.
     * @return The partial derivatives of the type for all names in
     *         <code>names</code>.
     */
    public Set getPartialDerivatives(Set names)
            throws IllegalConcatenationException,
            NoWellformedTypeException {

        //rule LF4
        if (this.child1 == null) {

            //Following constraint 2, we never apply rules 4 and 7 to a
            //recursive occurrence of a named type to avoid computing its
            //linear form. A recursive occurrence is noticed by child1 ==
            //null. So if child1 == null, we applied either rule 4 or 7
            //which means, the type hurts constraint 2.

            throw new NoWellformedTypeException("In type: \""
                    + this.toString()
                    + "\" recursive occurrence(s) without non-nullable ancestor.");

        }

        return this.child1.getPartialDerivatives(names);
    }//getPartialDerivatives


    /**
     * Returns TRUE if wellformedness constraint 1 is fulfilled,
     * otherwise FALSE. That means, the type does not have recursive
     * occurrences in non-tail positions. Call must have <code>flag ==
     * </code>TRUE (rule TP5).
     *
     * @param rootName The name of the top-level type to check.
     * @param flag     Says if recursive occurrence is allowed (TRUE)
     *                 or not.
     * @return TRUE, if constraint 1 is fulfilled and there are
     *         no recursive occurrences in non-tail positions,
     *         otherwise FALSE.
     */
    public boolean checkTailPosition(RName rootName, boolean flag) {

        //rule TP5
        if (this.getName().equals(rootName))

            if (this.child1 == null)
                // if recursive occurrence, return legality flag
                return flag;
            else
                // if no recursive occurrence, descend
                // (otherwise every initial call on a RNameType would
                // yield TRUE)
                return this.child1.checkTailPosition(rootName, flag);

        else
            // if new named type within the named type, descend with new
            // rootname
            return this.child1.checkTailPosition(this.getName(), true);
    }//checkTailPosition


    /**
     * Returns TRUE if wellformedness constraint 2 is fulfilled, otherwise
     * FALSE (rule NH5). That means, the type does not have recursive
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

        //rule NH5
        if (this.child1 != null)
            return this.child1.checkNonNullableHead(false);

        return flag;
    }//checkNonNullableHead


    /**
     * Returns the content of the type or a
     * <code>IrregularContentRequestException</code> if the method is
     * applied on recursive occurrences of named types.
     *
     * @param nameTable The table containing name and definition of the
     *                  top level named type and all yet unfolded types.
     * @return The unfolded type.
     */
    public RType unfold(Hashtable nameTable) {

        RType unfoldedChild = null;

        // rule UF5
        // if instance is a recursive occurrence, add its definition
        if (this.child1 == null) {

            RName name = null;
            Iterator it = nameTable.keySet().iterator();

            while (it.hasNext()) {
                name = (RName) it.next();
                if (this.getName().equals(name))
                    break;
            }

            unfoldedChild = (RType) nameTable.get(name);

            // rule UF6
            // if type is not a recursive occurrence, unfold
        } else {

            if (nameTable.containsKey(this.getName()) == false) {

                nameTable.put(this.getName(), this.getFirstChild());
            }

            unfoldedChild = this.child1.unfold(nameTable);
        }//else


        return new RNameType(
                new RName(this.name.toString()),
                unfoldedChild
        );
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
            return (    // equal type ?
                    t instanceof RNameType

                            // equal definition ?
                            && (this.child1 != null ? // recursive occurrence?
                            this.child1.equals(t.getFirstChild()) : //no
                            true             // yes: structural equality
                    )
            )
                    // or same type ?
                    || (this == t);
    }//equals


    /**
     * Returns a <code>String</code> representation of the expression.
     *
     * @return A String representation of the
     *         <code>RNameType</code>.
     */
    public String toString() {

        if (this.child1 != null) {
            StringBuffer buf = new StringBuffer(
                    (this.name == null) ?
                            RName.NULL :
                            this.name.toString()
            );
            buf.append(": [");
            buf.append(this.child1 == null ?
                    RName.NULL :
                    this.child1.toString());
            buf.append("]");
            return buf.toString();
        }
        return this.name.toString();
    }//toString


}//class       
