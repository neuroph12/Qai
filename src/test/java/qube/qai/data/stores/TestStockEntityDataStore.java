package qube.qai.data.stores;

import qube.qai.main.QaiTestBase;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 12/26/15.
 */
public class TestStockEntityDataStore extends QaiTestBase {

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    /**
     * this is about the stock entity data store
     * the data store fetches its data from
     * wiki listings, and we will begin with
     * S&P 500, obviously. other lists then will
     * be worked later... perhaps we can actually
     * work all those hundreds of listings some day...
     */
    public void testStockEntityDataStore() throws Exception {

        StockEntityDataStore dataStore = new StockEntityDataStore();

        injector.injectMembers(dataStore);

        Collection<String> marketListings = dataStore.getMarketListings();
        assertNotNull("no brainer", marketListings);
        assertTrue("there has to be something", !marketListings.isEmpty());

        // i want to see whether all pages are actually available
        // i assume they will be, obviously
        Collection<String> missing = new ArrayList<String>();
        for (String name : marketListings) {
            String articleName = name + ".xml";
            logger.info("retrieving: " + name + " with assumeed filename: " + articleName);
            WikiArticle article = searchService.retrieveDocumentContentFromZipFile(articleName);
            if (article == null) {
                missing.add(name);
            }
            //assertNotNull("this is what we want to see", article);
        }
        logger.info("we have altogether " + marketListings.size() + " lsitings available");
        logger.info("of which " + missing.size() + " items are missing, out of some reason");
        for (String name : missing) {
            logger.info(name + " is missing");
        }
    }

}
