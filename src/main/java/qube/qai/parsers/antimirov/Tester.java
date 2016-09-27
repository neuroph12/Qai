package qube.qai.parsers.antimirov;


import qube.qai.parsers.antimirov.log.LogManager;
import qube.qai.parsers.antimirov.log.ScreenLogger;
import qube.qai.parsers.antimirov.log.TextLogger;

/**
 * Performs a test of the subtyping
 * implementation with an array of test types taken out of class
 * <code>TestCases</code>.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see TestCases
 */
public final class Tester {


    /**
     * Logfilename without suffix.
     */
    private String logfilename = "report";


    /**
     * Logfilename suffix.
     */
    private String logfilenamesuffix = ".log";


    /**
     * Protocol generator.
     */
    private LogManager myLogManager;


    /**
     * Flag for screen logging status.
     */
    private boolean logToScreen = false;


    /**
     * Flag for short circuit evaluation.
     */
    private boolean shortCirc = false;


    /**
     * Invokes <code>prove()</code> for every two adjacent types in the
     * type array that comes from class <code>TestCases</code>.
     */
    public boolean subtypeTest() {

        // array of test types, tested with type[i] <: type[i+1]
        RType[] type = TestCases.getProveTestTypes();

        // array of expected results to each test
        int[] expResult = TestCases.getResults();

        StringBuffer buf = null;
        Inequality iq;

        int number = (type.length % 2 == 0) ?
                type.length :
                type.length - 1;

        int index = 0;
        int j = 0;
        int resultCode = 0;
        boolean result = true;
        boolean resultFlag = true;

        this.myLogManager = new LogManager();

        // test type[i] <: type[i+1] forall i < number
        for (int i = 0; i < number; i += 2) {

            System.out.print(".");

            // manage ascending enumeration of report filenames
            if (this.logToScreen == false) {

                // name = "report"
                buf = new StringBuffer(this.logfilename);
                // "report" + "0" ?
                buf.append((index < 10) ? "0" : "");
                // "report" + x
                buf.append((new Integer(index)).toString());
                // "reportX" + ".log"
                buf.append(this.logfilenamesuffix);

                // configure logging
                if (this.myLogManager.getLogger() != null)
                    this.myLogManager.getLogger().close();

                //this.myLogManager.setLogger(new TextLogger(buf.toString()));
                this.myLogManager.setLogger(new ScreenLogger());

            }

            try {

                // get result from subtyping algorithm
                iq = new Inequality(type[i], type[i + 1]);
                iq.setLogManager(this.myLogManager);
                iq.setShortCircuit(this.shortCirc);
                result = iq.prove();

                // transform result into Tester format
                resultCode = (result) ? 1 : -1;

            } catch (TypeException te) {

                resultCode = 0;

            } catch (Exception e) {

                resultCode = 0;
                this.myLogManager.logException(e, 0);
            }

            // resultFlag stays true if every result is equal to expected
            // result
            resultFlag = resultCode == expResult[j];

            if (resultFlag == false && logToScreen == false) {
                System.out.println("\nUnexpected result in file: "
                        + buf.toString()
                );
            }

            index++;
            j++;

        }//for
        System.out.println("Done.");

        // true iff every single result met expectation
        return resultFlag;
    }//subtypeTest


    /**
     * Analyzes command line arguments and configures the
     * <code>Tester</code> in accordance to them.
     *
     * @param args Command line arguments
     */
    private void configure(String[] args) {

        boolean flag = true;
        boolean correct = true;

        for (int i = 0; i < args.length; i++) {

            if ((correct = correct && flag) == false) {

                this.usage();
                System.exit(1);

            }

            if (flag = args[i].equals("-c")) {
                this.shortCirc = true;
                System.out.println("Short circuit evaluation activated.");
                continue;
            }

            this.logToScreen = true;
            //if (flag = args[i].equals("-s")) {
            if (true) {
                //this.logToScreen = true;
                this.myLogManager = new LogManager(new ScreenLogger());
                continue;
            }

            if (flag == false) {
                this.usage();
                System.exit(1);
            }

        }//for

    }//configure


    /**
     * Prints a usage message to stdout.
     */
    private void usage() {
        System.out.println("Usage: java Tester [options]");
        System.out.println(
                "   options:  -c   use short circuit evaluation   (default: yes)");
        System.out.println(
                "             -s   log to screen   (default: to file)");
    }//usage


    /**
     * Main method of class Tester. Runs subtyping test.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) throws TypeException {

        // create and configure Tester
        Tester myTester = new Tester();
        myTester.configure(args);

        // perform test
        if (myTester.subtypeTest())
            System.out.println("Test was allright.");
        else
            System.out.println("Test failed.");

        // close
        myTester.myLogManager.getLogger().close();

    }//main


}//class
