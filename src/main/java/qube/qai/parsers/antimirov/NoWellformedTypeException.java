package qube.qai.parsers.antimirov;


import qube.qai.parsers.antimirov.nodes.BaseNode;

/**
 * Occurs if a type instance is analyzed that does not fulfill the
 * two wellformedness constraints of types.
 * <p> <b>
 * 1) every recursive occurrence must be in a tail position within a
 * concatenation,
 * </p>
 * <p> <b>
 * 2) every recursive occurrence must be preceded by at least one
 * non-nullable term.
 * </b></p>
 * See also thesis, Section 3.3, p.27.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see BaseNode
 */
public class NoWellformedTypeException
        extends TypeException {


    /**
     * Sole Constructor for class NoWellformedTypeException.
     *
     * @param msg Error message to be printed.
     */
    public NoWellformedTypeException(String msg) {

        super(msg);
    }


}//class
