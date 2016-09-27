package qube.qai.parsers.antimirov;


import qube.qai.parsers.antimirov.nodes.BaseNode;

/**
 * Occurrs if a content is requested on a type instance
 * for which a content is not defined.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see BaseNode
 */
public class IrregularContentRequestException
        extends TypeException {


    /**
     * Sole constructor for class
     * <code>IrregularContentRequestException</code>.
     *
     * @param msg Error message.
     */
    public IrregularContentRequestException(String msg) {

        super(msg);
    }


}//class
