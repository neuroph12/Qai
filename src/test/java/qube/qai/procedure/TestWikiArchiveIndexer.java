package qube.qai.procedure;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.main.QaiTestBase;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

import java.io.File;

/**
 * Created by rainbird on 11/3/15.
 */
public class TestWikiArchiveIndexer extends QaiTestBase {

    private Logger logger = LoggerFactory.getLogger("TestWikiArchiever");

    private String dummyWikiFileName = "/home/rainbird/projects/work/qai/test/testWiki.xml";
    private String dummyWikiArchiveName = "/home/rainbird/projects/work/qai/test/testWiki.zip";
    private String dummyIndexDirectory = "/home/rainbird/projects/work/qai/test/testWiki.index";

    private boolean debug = true;

    public void testWikiIndexer() throws Exception {

        WikiRipperProcedure ripperProcedure = TestWikiRipperProcedure.createTestWikiRipper();
        injector.injectMembers(ripperProcedure);

        WikiArchiveIndexer wikiIndexer = new WikiArchiveIndexer(ripperProcedure);
        Selector<String> selector = new DataSelector<String>(dummyIndexDirectory);
        wikiIndexer.getArguments().setArgument(WikiArchiveIndexer.INPUT_INDEX_DIRECTORY, selector);
        injector.injectMembers(wikiIndexer);

        long start = System.currentTimeMillis();
        wikiIndexer.execute();
        long duration = System.currentTimeMillis() - start;
        log("procedure completed in: " + duration + " ms");

        File indexDirectory = new File(dummyIndexDirectory);
        assertTrue("index directory not found", indexDirectory.exists());

    }

    private void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.debug(message);
        }
    }
}
