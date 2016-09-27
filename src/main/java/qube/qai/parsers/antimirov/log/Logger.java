package qube.qai.parsers.antimirov.log;


import qube.qai.parsers.antimirov.nodes.BitArray;
import qube.qai.parsers.antimirov.nodes.BitArrayIndexException;


/**
 * Abstract superclass for line oriented logging classes.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see LogManager
 */
public abstract class Logger {


    /**
     * Maximal indent.
     */
    protected int maxIndent;


    /**
     * Horizontal separator for output.
     */
    protected char separator = '|';


    /**
     * Indentation value. Number of characters between left edge and
     * start of line.
     */
    protected int indent = 0;


    /**
     * Flag for activation status.
     */
    protected boolean isActive = true;


    /**
     * Flag for separation status.
     */
    protected boolean separation = true;


    /**
     * Separator positions.
     */
    protected BitArray sepPos;


    /**
     * Constructor for class <code>Logger</code>.
     */
    public Logger() {

        this.maxIndent = 80;
        this.sepPos = new BitArray(this.maxIndent);
    }


    /**
     * Constructor for class <code>Logger</code>, defining maximal
     * indent.
     *
     * @param max Maximum legal indent position. Default is 80.
     */
    public Logger(int max) {

        this.maxIndent = max;
        this.sepPos = new BitArray(this.maxIndent);
    }


    /**
     * Activates or deactivates logging. Logging is activated by TRUE
     * and deactivated by FALSE. Default is activated (TRUE).
     *
     * @param flag TRUE activates logging, FALSE deactivates
     *             logging.
     */
    public void setActive(boolean flag) {

        this.isActive = flag;
    }//setActive


    /**
     * Returns TRUE if logger is active, otherwise FALSE.
     *
     * @return TRUE if logger is active, otherwise FALSE.
     */
    public boolean isActive() {

        return this.isActive;
    }//isActive


    /**
     * Sets the internal indent to 0.
     */
    public void resetIndent() {

        this.indent = 0;
    }//resetIndent


    /**
     * Returns the current indent.
     *
     * @return The current indent.
     */
    public int getIndent() {

        return this.indent;
    }//getIndent


    /**
     * Returns the maximal indent.
     *
     * @return The maximal indent.
     */
    public int getMaxIndent() {

        return this.maxIndent;
    }//getMaxIndent


    /**
     * Turns separation on or off.
     *
     * @param flag TRUE is separation turned on, FALSE is off.
     */
    public void setSeparation(boolean flag) {

        this.separation = flag;
    }//setSeparation


    /**
     * Marks an indent position as separator position.
     *
     * @param indentPos The indent position to mark.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void markPos(int indentPos)
            throws LoggingException {

        if (this.isActive()) {
            try {

                this.sepPos.setElement(indentPos, true);
            } catch (BitArrayIndexException baie) {

                throw new LoggingException(baie.getIndex(), baie.getSize());
            }
        }//if
    }//markPos


    /**
     * Unmarks an indent position as separator position and returns it.
     *
     * @return The last marked position.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void unmarkPos(int indentPos)
            throws LoggingException {

        if (this.isActive()) {
            try {

                this.sepPos.setElement(indentPos, false);
            } catch (BitArrayIndexException baie) {

                throw new LoggingException(baie.getIndex(), baie.getSize());
            }
        }//if
    }//unmarkPos


    /**
     * Logs a single line.
     *
     * @param line Text to log.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void log(String line)
            throws LoggingException {

        if (this.isActive) {

            this.indent();
            this.wlog(line);
            this.newLn();
        }
    }//log


    /**
     * Worker for <code>log()</code>.
     *
     * @param line Text to log.
     */
    protected abstract void wlog(String line);


    /**
     * Prints indent.
     *
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void indent()
            throws LoggingException {

        if (this.isActive()) {

            if (indent < 1) {
                indent = 0;
            }

            char[] pos = new char[indent];

            if (this.separation) {
                try {

                    // insert separator on marked positions
                    for (int cpos = 0; cpos < pos.length; cpos++)
                        pos[cpos] = (sepPos.getElement(cpos)) ?
                                this.separator :
                                ' ';

                } catch (BitArrayIndexException baie) {

                    throw new LoggingException(baie.getIndex(), baie.getSize());
                }
            }//if

            this.wlog(new String(pos));
        } //if
    }//indent


    /**
     * Increases indent by one step.
     */
    public void incIndent() {

        if (this.isActive() && indent < maxIndent - 1) {
            indent++;
            indent++;
        }
    }//incIndent


    /**
     * Decreases indent by one step if possible.
     */
    public void decIndent() {

        if (this.isActive() && indent > 1) {
            indent--;
            indent--;
        }
    }//decIndent


    /**
     * Writes a line separator to the currently logged line.
     */
    public abstract void newLn();


    /**
     * Produces an empty line with indent.
     *
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void emptyLn()
            throws LoggingException {

        this.indent();
        this.newLn();
    }//emptyLn


    /**
     * Closes Logger and deactivates logging.
     */
    public abstract void close();


    /**
     * Returns a String representation of the instance.
     *
     * @return a String representation of the instance.
     */
    public abstract String toString();


} //class
