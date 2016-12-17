package qube.qai.procedure;

import qube.qai.main.QaiTestBase;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

/**
 * Created by rainbird on 11/11/15.
 */
public class TestWikiSearch extends QaiTestBase {

    @Inject @Named("Wiktionary_en")
    private SearchServiceInterface searchService;

    /**
     * @TODO this test needs improvement- start with injecting the constants
     * @throws Exception
     */
    public void testSearch() throws Exception {

        // do some searching and display results...
        String[] searchList = {"test", "mouse", "silly"};
        for (String search : searchList) {
            Collection<SearchResult> results = searchService.searchInputString(search, "title", 100);
            assertTrue("no results", results != null && !results.isEmpty());
            for (SearchResult result : results) {
                logger.info("searching: '" + search + "' resulted: '" + result.getTitle() + "' with " + result.getRelevance() + "% relevance");
            }
        }

    }
}
