package qube.qai.parsers.antimirov;


/**
 * Occurs if the concatenation of linear forms is applied
 * to an illegal input. A concatenation of linear forms is defined for
 * type pairs (cf. rules CL1 and CL2) and sets (cf. rules CL3-CL7).
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Concatenable
 */
public class IllegalConcatenationException
        extends Exception {


    /**
     * Sole Constructor for class IllegalConcatenationException
     *
     * @param msg Error message to be printed.
     */
    public IllegalConcatenationException(String msg) {

        super(msg);
    }


}//class
