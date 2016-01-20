package qube.qai.procedure.archive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.main.QaiTestBase;
import qube.qai.procedure.wikiripper.TestWikiRipperProcedure;
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

    public void restWikiRipAndIndex() throws Exception {

//        String wikiToRip = "/media/rainbird/ALEPH/wiki-data/dewiki-20151226-pages-articles.xml";
//        String archiveToCreate = "/media/rainbird/ALEPH/wiki-archives/wikipedia_de.zip";
//        String indexDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_de.index";

        String wikiToRip = "/media/rainbird/ALEPH/wiki-data/dewiktionary-20151226-pages-articles.xml";
        String archiveToCreate = "/media/rainbird/ALEPH/wiki-archives/wiktionary_de.zip";
        String indexDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_de.index";

        WikiRipperProcedure ripperProcedure = new WikiRipperProcedure();
        Selector<String> fileanmeSelector = new DataSelector<String>(wikiToRip);
        Selector<String> archiveNameSelector = new DataSelector<String>(archiveToCreate);
        Selector<Boolean> isWiktionarySelector = new DataSelector<Boolean>(Boolean.TRUE);
        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_FILENAME, fileanmeSelector);
        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_TARGET_FILENAME, archiveNameSelector);
        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_IS_WIKTIONARY, isWiktionarySelector);

        WikiArchiveIndexer indexerProcedure = new WikiArchiveIndexer(ripperProcedure);
        Selector<String> selector = new DataSelector<String>(indexDirectory);
        indexerProcedure.getArguments().setArgument(WikiArchiveIndexer.INPUT_INDEX_DIRECTORY, selector);

        long start = System.currentTimeMillis();
        log("ripping: " + wikiToRip);
        ripperProcedure.execute();
        long duration = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        log("ripping finished, took: " + duration + "ms. now indexing...");
        indexerProcedure.execute();
        duration = System.currentTimeMillis() - start;
        log("indexing finished, took " + duration + "ms");
    }

    private void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.debug(message);
        }
    }
}
