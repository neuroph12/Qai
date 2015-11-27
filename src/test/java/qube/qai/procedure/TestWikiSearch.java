package qube.qai.procedure;

import junit.framework.TestCase;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;
import qube.qai.services.implementation.WikiSearchService;

import java.io.File;
import java.util.Collection;

/**
 * Created by rainbird on 11/11/15.
 */
public class TestWikiSearch extends TestCase {

    public String INDEX_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";

    public String ZIP_FILE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";

    public void testSearch() throws Exception {

        File indexDiretpory = new File(INDEX_DIRECTORY);
        assertTrue("index directory not found", indexDiretpory.exists());

        File zipFile = new File(ZIP_FILE);
        assertTrue("zip file not found", zipFile.exists());

        SearchServiceInterface searchService = new WikiSearchService(INDEX_DIRECTORY, ZIP_FILE);

        Collection<SearchResult> results = searchService.searchInputString("test", "title", 100);
        assertTrue("no results", results != null && !results.isEmpty());
    }
}
