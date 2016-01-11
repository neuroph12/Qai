package qube.qai.procedure;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

import java.io.File;

/**
 * Created by rainbird on 11/3/15.
 */
public class TestWikiRipperProcedure extends TestCase {

    private Logger logger = LoggerFactory.getLogger("TestWikiRipperProcedure");

    private boolean debug = true;

    private static String dummyWikiFileName = "/home/rainbird/projects/work/qube.qai/test/testWiki.xml";
    private static String dummyWikiArchiveName = "/home/rainbird/projects/work/qube.qai/test/testWiki.zip";

    /**
     * @throws Exception
     */
    public void testWikiRipper() throws Exception {

        WikiRipperProcedure ripperProcedure = createTestWikiRipper();

        long start = System.currentTimeMillis();
        ripperProcedure.ripWikiFile();
        long end = System.currentTimeMillis();

        // now check that the file out can be found on the filesystem
        File file = new File(dummyWikiArchiveName);
        assertTrue("output file could not be found", file.exists());

        long duration = end - start;
        log("procedure completed in: " + duration + " ms");
        // result 1756343 ms which is around 30 mins-
        // that is with logging, without would be much faster
        File archiveZip = new File(dummyWikiArchiveName);
        assertTrue("output file must be there", archiveZip.exists());

        // we are done- just delete the file
        archiveZip.deleteOnExit();
    }

    public static WikiRipperProcedure createTestWikiRipper() {
        WikiRipperProcedure ripperProcedure = new WikiRipperProcedure();
        Selector<String> fileanmeSelector = new DataSelector<String>(dummyWikiFileName);
        Selector<String> archiveNameSelector = new DataSelector<String>(dummyWikiArchiveName);
        Selector<Boolean> isWiktionarySelector = new DataSelector<Boolean>(Boolean.FALSE);
        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_FILENAME, fileanmeSelector);
        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_TARGET_FILENAME, archiveNameSelector);
        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_IS_WIKTIONARY, isWiktionarySelector);
        return ripperProcedure;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
