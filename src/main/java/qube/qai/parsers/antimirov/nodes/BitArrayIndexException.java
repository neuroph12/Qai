package qube.qai.parsers.antimirov.nodes;


/**
 * Occurrs if an invalid index position in the
 * <code>BitArray</code> is adressed. This happens if
 * <code>setElement()</code> or <code>getElement()</code> is called on
 * an element index which is either smaller than 0 or bigger than the
 * <code>size</code> of the <code>BitArray</code>.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see BitArray
 */
public final class BitArrayIndexException
        extends Exception {


    /**
     * Index position access which caused the exception.
     */
    private int errorIndex;


    /**
     * <code>BitArray</code> size.
     */
    private int size;


    /**
     * Sole constructor for class <code>BitArrayIndexException</code>.
     *
     * @param i    Index position that caused the exception.
     * @param size <code>BitArray</code> size.
     */
    public BitArrayIndexException(int errorIndex, int size) {

        this.errorIndex = errorIndex;
        this.size = size;
    }//constructor


    /**
     * Returns the index which caused the exception.
     *
     * @return The index which caused the exception.
     */
    public int getIndex() {

        return this.errorIndex;
    }//getIndex


    /**
     * Returns the size (maximal legal index position) of the
     * <code>BitArray</code>.
     *
     * @return Size of the <code>BitArray</code>.
     */
    public int getSize() {

        return this.size;
    }//getSize


    /**
     * Returns a <code>String</code> representation of the exception.
     *
     * @return <code>String</code> representation of the exception.
     */
    public String toString() {

        // construct error message
        StringBuffer buf = new StringBuffer("Index: ");
        buf.append(this.errorIndex);

        // if position bigger than maximum range is adressed
        if (this.errorIndex > this.size) {

            buf.append(" exceeds BitArray length of ");
            buf.append(this.size);
            buf.append(" positions.");

        } else {

            // if position smaller than 0 is adressed
            if (this.errorIndex < 0) {

                buf.append(" is no valid index position.");

            } else {

                buf.append(" is no valid index position in domain 0-"
                        + this.size);
            }
        }

        return buf.toString();
    }//toString


}//class
