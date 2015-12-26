package qube.qai.data.stores;

import info.bliki.wiki.model.WikiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.parsers.WikiIntegration;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by rainbird on 12/26/15.
 */
public class StockEntityDataStore implements DataStore {

    private Logger logger = LoggerFactory.getLogger("StockEntityDataStore");

    private String stockListingPage = "Lists of companies by stock exchange listing.xml";
    private String SnP500Page = "List of S&P 500 companies.xml";

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    /**
     * practically the Lists of companies by stock exchange listing
     * page from Wikipedia, out of some reason
     * Companies listed on the Singapore Exchange and
     * List of companies listed on the Australian Securities Exchange are missing
     * but i guess, when i switch to using hazelcast-maps rather than
     * search service, which i will be removing soon sometime anyways
     * @return
     */
    public Collection<String> getMarketListings() {

        Collection<String> entityListings = new ArrayList<String>();
        WikiArticle marketListins = searchService.retrieveDocumentContentFromZipFile(stockListingPage);
        String html = WikiIntegration.wikiToHtml(marketListins.getContent());
        StringBuilder builder = new StringBuilder();
        WikiModel model = WikiIntegration.createModel(marketListins, builder);

        Set<String> links =  model.getLinks();
        for (String link : links) {
            logger.info(link);
            entityListings.add(link);
        }

        return entityListings;
    }

    /**
     * this is for retrieving the contents of the given listing
     *
     */
    public Collection<StockEntity> fetchEntitesOf(String marketListingName) {

        Collection<StockEntity> entities = new ArrayList<StockEntity>();


        return entities;
    }
}
