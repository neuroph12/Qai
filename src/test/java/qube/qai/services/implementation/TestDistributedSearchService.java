package qube.qai.services.implementation;

import qube.qai.main.QaiTestBase;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

/**
 * Created by rainbird on 1/6/16.
 */
public class TestDistributedSearchService extends QaiTestBase {

    @Inject @Named("Wikipedia_en")
    SearchServiceInterface searchService;

    public void testDistributedSearch() throws Exception {

        // make an instance of the thing
        DistributedSearchListener searchListener = new DistributedSearchListener("Wikipedia_en");
        searchListener.setSearchService(searchService);
        injector.injectMembers(searchListener);

        searchListener.initialize();


        String topicName = "Wikipedia_en";
        DistributedSearchService distributedSearch = new DistributedSearchService(topicName);
        injector.injectMembers(distributedSearch);
        distributedSearch.initialize();

        Collection<SearchResult> results = distributedSearch.searchInputString("mouse", "title", 100);
        assertNotNull("have to return something", results);
        assertTrue("has to be something in there as well", !results.isEmpty());
        for (SearchResult result : results) {
            logger.info("found result: " + result.getTitle());
        }
    }
}
