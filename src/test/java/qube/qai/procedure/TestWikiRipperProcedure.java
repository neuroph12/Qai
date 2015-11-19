package qube.qai.procedure;

import junit.framework.TestCase;

import java.io.File;

/**
 * Created by rainbird on 11/3/15.
 */
public class TestWikiRipperProcedure extends TestCase {

    private boolean debug = true;

    public void testSomething() throws Exception {
        fail("test not yet implemented");
    }

    /**
     * @TODO add real tests for the class
     * @throws Exception
     */
    public void restWikiRipper() throws Exception {

        WikiRipperProcedure ripperProcedure = new WikiRipperProcedure();

        String fileInName = ripperProcedure.getFileToRipName();
        File file = new File(fileInName);
        assertTrue("file could not be found will exit", file.exists());

        long start = System.currentTimeMillis();
        ripperProcedure.ripWikiFile();

        long end = System.currentTimeMillis();

        // now check that the file out can be found on the filesystem
        String fileOutName = ripperProcedure.getFileToArchiveName();
        file = new File(fileOutName);
        assertTrue("output file could not be found", file.exists());

        long duration = end - start;
        log("procedure completed in: " + duration + " ms");
        // result 1756343 ms which is around 30 mins-
        // that is with logging, without would be much faster

    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
