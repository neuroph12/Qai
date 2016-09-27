package qube.qai.parsers.antimirov.log;


/**
 * Represents a logger for logging
 * protocol messages to standard output. The instance of the
 * <code>ScreenLogger</code> can be assigned to a
 * <code>LogManager</code> for more convenient use.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see LogManager
 */
public class ScreenLogger
        extends Logger {


    /**
     * Sole constructor for <code>ScreenLogger</code>.
     */
    public ScreenLogger() {

        super();
    }//constructor


    /**
     * Worker for log().
     *
     * @param msg Message to log.
     */
    protected void wlog(String line) {

        System.out.print(line);
    }//wlog


    /**
     * Starts a new line.
     */
    public void newLn() {

        System.out.println();
    }//newLn


    /**
     * Closes the logger.
     */
    public void close() {
    }//close


    /**
     * Returns a <code>String</code> representation of the
     * <code>ScreenLogger</code>.
     *
     * @return A <code>String</code> representation of the
     *         <code>ScreenLogger</code>. brief status overview.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer("ScreenLogger");
        buf.append((this.isActive) ? " (active) " : " (inactive) ");

        return buf.toString();
    }//toString


}//class
