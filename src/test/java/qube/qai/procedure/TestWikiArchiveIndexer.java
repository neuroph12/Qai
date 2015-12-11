package qube.qai.procedure;

import junit.framework.TestCase;
import qube.qai.procedure.archive.WikiArchiveIndexer;

import java.io.File;

/**
 * Created by rainbird on 11/3/15.
 */
public class TestWikiArchiveIndexer extends TestCase {

    private boolean debug = true;

    public void testSomething() throws Exception {
        // @TODO a real test should instead be implemented
        fail("a real test should instead be implemented");
    }

    /**
     * @TODO implement real tests for this
     * @throws Exception
     */
    public void restWikiIndexer() throws Exception {
        WikiArchiveIndexer wikiIndexer = new WikiArchiveIndexer();

        File indexDirectory = new File(wikiIndexer.getINDEX_DIRECTORY());
        assertTrue("index directory not found", indexDirectory.exists());

        File archiveFile = new File(wikiIndexer.getZIP_FILE());
        assertTrue("zip file not found", archiveFile.exists());

        long start = System.currentTimeMillis();
        wikiIndexer.indexZipFileEntries();

        long end = System.currentTimeMillis();
        long duration = end - start;
        log("procedure completed in: " + duration + " ms");
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
