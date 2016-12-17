package qube.qai.parsers.antimirov.log;


/**
 * Occurrs if a <code>Logger</code> exceeds the upper bound for
 * indentation. A <code>Logger</code> has a default maximum indentation
 * of 80 which means it can log with maximal indent of 80 characters.
 * This should be really sufficient for normal cases of subtyping
 * processes where the depth of recursion does not exceed 3-4 recursive
 * calls on the stack. This corresponds to an indent of about 20
 * characters.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 */
public class LoggingException
        extends Exception {


    /**
     * Indent position access which caused the exception.
     */
    protected int errorIndex;


    /**
     * Maximum legal indent position.
     */
    protected int size;


    /**
     * Sole constructor for class <code>LoggingException</code>.
     *
     * @param errorIndex Index position that caused the exception.
     * @param size       <code>BitArray</code> size.
     */
    public LoggingException(int errorIndex, int size) {

        this.errorIndex = errorIndex;
        this.size = size;
    }//constructor


    /**
     * Returns a <code>String</code> representation of the exception.
     *
     * @return <code>String</code> representation of the exception.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer("Indent: ");
        buf.append(this.errorIndex);

        // if adressed position bigger than maximum range
        if (this.errorIndex > this.size) {

            buf.append(" exceeds maximum indent position of ");
            buf.append(this.size);
            buf.append(".");

        } else {

            // if adressed position smaller than 0
            if (this.errorIndex < 0) {

                buf.append(" is no valid indent.");

            } else {

                buf.append(" is no valid indent in domain 0-" + this.size);
            }
        }

        return buf.toString();
    }//toString


}//class
