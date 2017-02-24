package qube.qai.parsers.antimirov.log;


import java.io.FileWriter;
import java.io.IOException;


/**
 * Represents a logger for logging
 * protocol messages into a file. The filename is given as a
 * <code>String</code> to the constructor of the logger. The instance
 * of the <code>TextLogger</code> can be assigned to a
 * <code>LogManager</code> for more convenient use.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see LogManager
 */
public class TextLogger
        extends Logger {


    /**
     * Name of the file for logging.
     */
    private String logfilename;


    /**
     * <code>FileWriter</code> for writing into the file.
     */
    private FileWriter fwriter;


    /**
     * The line separator string. (Usually one single character.)
     */
    private String lineSep;


    /**
     * Sole constructor for TextLogger.
     *
     * @param filename Name of the logfile.
     */
    public TextLogger(String filename) {

        this.logfilename = filename;

        try {

            this.fwriter = new FileWriter(this.logfilename);
        } catch (IOException ioe) {

            System.err.println("Error opening logfile.");
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
        }
        this.lineSep = System.getProperty("line.separator", "\n");
    }//constructor


    /**
     * Worker for log().
     *
     * @param msg Message to log.
     */
    protected void wlog(String msg) {

        if (this.isActive()) {
            StringBuffer buf = new StringBuffer(msg);

            try {

                fwriter.write(buf.toString());
            } catch (IOException ioe) {

                System.err.println("IOException while Writing!");
                System.err.println(ioe.getMessage());
                ioe.printStackTrace();
            }
        }//if
    }//wlog


    /**
     * Starts a new line.
     */
    public void newLn() {

        if (this.isActive()) {
            try {

                fwriter.write(lineSep);
            } catch (IOException ioe) {

                System.err.println("IOException while line separation.");
                System.err.println(ioe.getMessage());
                ioe.printStackTrace();
            }
        }//if
    }//newLn


    /**
     * Closes the logger.
     */
    public void close() {

        try {

            if (this.fwriter != null)
                this.fwriter.close();

        } catch (IOException ioe) {

            System.err.println("Error closing logfile!");
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
        }
    }//close


    /**
     * Sets logfile to file <code>name</code>.
     *
     * @param filename Name of the new logfile.
     */
    public void setLogFile(String filename) {

        this.logfilename = filename;

        try {

            if (this.fwriter != null)
                this.fwriter.close();
            this.fwriter = new FileWriter(this.logfilename);
            this.indent = 0;

        } catch (IOException ioe) {

            System.err.println("Error opening new logfile.");
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
        }
    }//setLogFile


    /**
     * Returns a <code>String</code> representation of the
     * <code>TextLogger</code>.
     *
     * @return A <code>String</code> representation of the
     * <code>TextLogger</code>.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer("TextLogger");
        buf.append((this.isActive) ? " (active) " : " (inactive) ");
        return buf.toString();
    }//toString


}//class
