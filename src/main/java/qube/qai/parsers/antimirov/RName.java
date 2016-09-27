package qube.qai.parsers.antimirov;

/**
 * Represents a name. A name can be the name of a letter or the name
 * of a named type.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 */
public final class RName
        implements SetElement {

    //--------- Regular Expression Based Types

    /**
     * Name of the alternation type.
     */
    public static final String ALTERNATION = "alternation";

    /**
     * Name of the iteration type.
     */
    public static final String ITERATION = "iteration";

    /**
     * Name of the concatenation type.
     */
    public static final String CONCATENATION = "concatenation";

    /**
     * Name of the type matching nothing.
     */
    public static final String NONE = "none";

    /**
     * Name of the empty type.
     */
    public static final String EMPTY = "e";

    //--------- Primitive Types

    /**
     * Name of the primitive type double.
     */
    public static final String DOUBLE = "double";

    /**
     * Name of the primitive type decimal.
     */
    public static final String DECIMAL = "decimal";

    /**
     * Name of the primitive type integer.
     */
    public static final String INTEGER = "integer";

    /**
     * Name of the primitive type boolean.
     */
    public static final String BOOLEAN = "boolean";

    /**
     * Name of the primitive type string.
     */
    public static final String STRING = "string";

    //--------- XML BaseNode Types

    /**
     * Name of the node type comment.
     */
    public static final String COMMNODE = "comment";

    /**
     * Name of the node type processing instruction.
     */
    public static final String PINODE = "pi";

    /**
     * Name of the node type text.
     */
    public static final String TEXTNODE = "text";

    /**
     * Name of the node type document.
     */
    public static final String DOCNODE = "document";

    /**
     * Name of the node type attribute.
     */
    public static final String ATTRNODE = "attribute";

    /**
     * Name of the node type element.
     */
    public static final String ELEMNODE = "element";

    //--------- other

    /**
     * Name of the primitive type string.
     */
    public static final String NULL = "null";

    /**
     * Name of infinity.
     */
    public static final int INFINITY = -1; // always has to be
    // negative !!!

    /**
     * User defined name.
     */
    private String name;


    /**
     * Constructor for names.
     *
     * @param name The name to construct.
     */
    public RName(String name) {

        this.name = name;
    }//constructor


    /**
     * Returns the name.
     *
     * @return Name represented by the instance.
     */
    public String getNameString() {

        return this.name;
    }//getNameString


    /**
     * Returns TRUE, if instance represents same <code>String</code>
     * like <code>s</code>.
     *
     * @param s The <code>String</code> to compare the instance to.
     * @return TRUE, if <code>String</code>s are equal, otherwise FALSE.
     */
    public boolean equals(String s) {

        return this.name.equals(s);
    }//equals


    /**
     * Returns TRUE, if instance represents same <code>String</code>
     * like <code>s</code>.
     *
     * @param s The string to compare the instance to.
     * @return TRUE, if strings are equal, otherwise FALSE.
     */
    public boolean equals(SetElement s) {

        return (s instanceof RName) ?

                (this.getNameString().equals(((RName) s).getNameString())) :

                false;
    }//equals


    /**
     * Returns a <code>String</code> representation of the name.
     *
     * @return A <code>String</code> representation of the name.
     */
    public String toString() {

        return new String(this.name.toString());
    }//toString


}//class
