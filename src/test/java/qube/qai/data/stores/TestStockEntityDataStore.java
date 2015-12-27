package qube.qai.data.stores;

import qube.qai.main.QaiTestBase;
import qube.qai.persistence.StockEntity;
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

    private String SnP500Page = "List of S&P 500 companies.xml";

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
        logger.info("we have altogether " + marketListings.size() + " listings available");
        logger.info("of which " + missing.size() + " items are missing, out of some reason");
        for (String name : missing) {
            logger.info(name + " is missing");
        }
    }

    public void testFetchEntitesOf() {

        StockEntityDataStore dataStore = new StockEntityDataStore();
        injector.injectMembers(dataStore);

        Collection<StockEntity> entities = dataStore.fetchEntitesOf(SnP500Page);
        assertNotNull("well, of course", entities);
        assertTrue("there has to be something", !entities.isEmpty());
        int entityCount = 0;
        for (StockEntity entity : entities) {
            logger.info("found: " + entitiy2String(entity));
            entityCount++;
        }

        logger.info("altogether " + entityCount + " entities in list... should be 500 exactly really");
    }


    private String entitiy2String(StockEntity entity) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Ticker symbol: ").append(entity.getTickerSymbol());
        buffer.append(" Security: ").append(entity.getSecurity());
        buffer.append(" GICS: ").append(entity.getGicsSector());
        buffer.append(" GICS Sub Industry: ").append(entity.getGicsSubIndustry());
        buffer.append(" Address of Headquarters: ").append(entity.getAddress());
        buffer.append(" Date first added: ").append(entity.getDateFirstAdded());
        buffer.append(" CIK: ").append(entity.getCIK());
        return buffer.toString();
    }

}
