package qube.qai.data.stores;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import java.util.Date;

/**
 * Created by rainbird on 12/26/15.
 */
public class StockEntityDataStore implements DataStore {

    private Logger logger = LoggerFactory.getLogger("StockEntityDataStore");

    private String stockListingPage = "Lists of companies by stock exchange listing.xml";


    String[] headerTitles = {"Ticker symbol", "Security",
            "SEC filings", "GICS", "GICS Sub Industry",
            "Address of Headquarters", "Date first added", "CIK"};


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
//        WikiArticle marketListins = searchService.retrieveDocumentContentFromZipFile(stockListingPage);
//        String html = WikiIntegration.wikiToHtml(marketListins.getContent());
//        StringBuilder builder = new StringBuilder();
//        WikiModel model = WikiIntegration.createModel(marketListins, builder);
//
//        Set<String> links =  model.getLinks();
//        for (String link : links) {
//            logger.info(link);
//            entityListings.add(link);
//        }

        return entityListings;
    }

    /**
     * this is for retrieving the contents of the given listing
     *
     */
    public Collection<StockEntity> fetchEntitesOf(String marketListingName) {

        Collection<StockEntity> entities = new ArrayList<StockEntity>();
        WikiArticle article = searchService.retrieveDocumentContentFromZipFile(marketListingName);
        if (article == null) {
            throw new RuntimeException("Listing: " + marketListingName + " could not be found, can't go on");
        }
        String html = WikiIntegration.wikiToHtml(article);
        String[] header = WikiIntegration.stripHeader(html);
        String[][] data = WikiIntegration.stripTableData(html);

        // start at the first index in order to skip the header line
        for (int i = 1; i < data.length; i++) {
            // this might seem strange but if have no informaiton whatte field is
            // we can also not associate with fields
            StockEntity entity = new StockEntity();
            for (int j = 0; j < data[i].length; j++) {
                String fieldName = header[j];
                String fieldValue = data[i][j];
                assignValueToEntity(entity, fieldName, fieldValue);
            }
            entities.add(entity);
        }

        return entities;
    }
    // "Ticker symbol", "Security","SEC filings", "GICS", "GICS Sub Industry","Address of Headquarters", "Date first added", "CIK"
    private void assignValueToEntity(StockEntity entity, String fieldName, String fieldValue) {

        if (headerTitles.length > 0 &&
                headerTitles[0].equalsIgnoreCase(fieldName)) {
            // the symbols come in {{exchange|ticker}} form
            String exchange = StringUtils.substringBetween(fieldValue, "{{", "|");
            String ticker = StringUtils.substringBetween(fieldValue, "|", "}}");
            entity.setUuid(exchange + "|" + ticker); // in fact just we broke before
            entity.setTickerSymbol(ticker);
            entity.setTradedIn(exchange);
        } else if (headerTitles.length > 1 &&
                headerTitles[1].equalsIgnoreCase(fieldName)) {
            entity.setSecurity(fieldValue);
        } else if (headerTitles.length > 2 &&
                headerTitles[2].equalsIgnoreCase(fieldName)) {
            entity.setSecFilings(fieldValue);
        } else if (headerTitles.length > 3 &&
                headerTitles[3].equalsIgnoreCase(fieldName)) {
            entity.setGicsSector(fieldValue);
        } else if (headerTitles.length > 4 &&
                headerTitles[4].equalsIgnoreCase(fieldName)) {
            entity.setGicsSubIndustry(fieldValue);
        } else if (headerTitles.length > 5
                && headerTitles[5].equalsIgnoreCase(fieldName)) {
            entity.setAddress(fieldValue);
        } else if (headerTitles.length > 6 &&
                headerTitles[6].equalsIgnoreCase(fieldName)) {
            // this field is not necessarily filled
            if (StringUtils.isNotBlank(fieldValue)) {
                try {
                    Date date = DateTime.parse(fieldValue).toDate();
                    entity.setDateFirstAdded(date);
                } catch (Exception e) {
                    logger.error("failed parsing date entry: " + fieldValue);
                }
            }
        } else if (headerTitles.length > 7
                && headerTitles[7].equalsIgnoreCase(fieldName)) {
            entity.setCIK(fieldValue);
        }
    }
}
