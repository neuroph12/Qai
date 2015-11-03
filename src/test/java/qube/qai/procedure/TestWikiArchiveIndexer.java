package qube.qai.procedure;

import junit.framework.TestCase;

/**
 * Created by rainbird on 11/3/15.
 */
public class TestWikiArchiveIndexer extends TestCase {

    public void testWikiIndexer() throws Exception {
        WikiArchiveIndexer wikiIndexer = new WikiArchiveIndexer();

        wikiIndexer.indexZipFileEntries("");
    }
}
