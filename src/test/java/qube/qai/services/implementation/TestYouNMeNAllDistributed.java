package qube.qai.services.implementation;

import qube.qai.main.QaiTestBase;
import qube.qai.procedure.wikiripper.TestWikiRipperProcedure;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

/**
 * Created by rainbird on 1/12/16.
 */
public class TestYouNMeNAllDistributed extends QaiTestBase {

    private static String dummyWikiFileName = "/home/rainbird/projects/work/qube/qai/test/testWiki.xml";
    private static String dummyWikiArchiveName = "/home/rainbird/projects/work/qube/qai/test/testWiki.zip";

    public void testYouNMeAndEveryoneWeKnow() throws Exception {

        WikiRipperProcedure wikiRipper = TestWikiRipperProcedure.createTestWikiRipper();
        long start = System.currentTimeMillis();
        wikiRipper.ripWikiFile();
        long duration = System.currentTimeMillis() - start;
        logger.info("ripping wiki-archive took: " + duration);

        // now we run our indexer on the wiki-archive file
    }
}
