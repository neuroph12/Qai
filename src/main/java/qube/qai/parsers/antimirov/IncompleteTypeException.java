package qube.qai.parsers.antimirov;


import qube.qai.parsers.antimirov.nodes.BaseNode;

/**
 * Occurs if a type instance is not constructed as a regular
 * expression, for instance if a <code>RAlternationType</code> does not
 * have two children, or a <code>RNameType</code> has more than one
 * child.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see BaseNode
 */
public class IncompleteTypeException extends TypeException {


    /**
     * Sole Constructor for class <code>IncompleteTypeException</code>.
     *
     * @param msg Error message to be printed.
     */
    public IncompleteTypeException(String msg) {

        super(msg);
    }


}//class
