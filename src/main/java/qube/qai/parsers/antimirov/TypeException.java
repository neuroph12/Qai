package qube.qai.parsers.antimirov;


/**
 * Abstract superclass for all exceptions concerning inherent parts or
 * behaviour of type classes.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 */
public abstract class TypeException
        extends Exception {


    /**
     * Constructor for class <code>TypeException</code>.
     *
     * @param msg The error message to be printed.
     */
    public TypeException(String msg) {

        super(msg);
    }//constructor


}//class
