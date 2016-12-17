package qube.qai.services.implementation;

import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.DataSelectionOperator;
import qube.qai.main.QaiTestBase;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.wikiripper.TestWikiRipperProcedure;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

import java.io.File;

/**
 * Created by rainbird on 1/12/16.
 */
public class TestYouNMeNAllDistributed extends QaiTestBase {

    private static String dummyWikiFileName = "/home/rainbird/projects/work/qube/qai/test/testWiki.xml";
    private static String dummyWikiArchiveName = "/home/rainbird/projects/work/qube/qai/test/testWiki.zip";
    private String dummyIndexDirectory = "/home/rainbird/projects/work/qai/test/testWiki.index";

    public void testYouNMeAndEveryoneWeKnow() throws Exception {

        WikiRipperProcedure ripperProcedure = TestWikiRipperProcedure.createTestWikiRipper();
        injector.injectMembers(ripperProcedure);

        WikiArchiveIndexer wikiIndexer = new WikiArchiveIndexer(ripperProcedure);
        // these are the only additional arguments which we need in this case
        wikiIndexer.setAnalyseDate(true);
        wikiIndexer.setAnalyseLocation(true);
        wikiIndexer.setAnalysePerson(true);
        wikiIndexer.setAnalyseOrganization(true);
        SelectionOperator<String> selectionOperator = new DataSelectionOperator<String>(dummyIndexDirectory);
        wikiIndexer.getArguments().setArgument(WikiArchiveIndexer.INPUT_INDEX_DIRECTORY, selectionOperator);
        injector.injectMembers(wikiIndexer);

        long start = System.currentTimeMillis();
        wikiIndexer.execute();
        long duration = System.currentTimeMillis() - start;
        log("procedure completed in: " + duration + " ms");

        File indexDirectory = new File(dummyIndexDirectory);
        assertTrue("index directory not found", indexDirectory.exists());
    }

}
