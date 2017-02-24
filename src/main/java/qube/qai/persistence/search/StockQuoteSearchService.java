package qube.qai.persistence.search;

import qube.qai.persistence.StockQuote;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 5/31/16.
 */
public class StockQuoteSearchService implements SearchServiceInterface {

    @Inject
    private EntityManager manager;

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        String queryString = "SELECT q FROM StockQuote q";

        if ("TICKERSYMBOL".equalsIgnoreCase(fieldName)) {
            queryString += " WHERE q.id.tickerSymbol = '" + searchString + "'";
        } else if ("QUOTEDATE".equals(fieldName)) {
            queryString += " WHERE q.id.quoteDate = " + searchString;
        } else {
            String[] parts = org.apache.commons.lang3.StringUtils.split(searchString, '|');
            queryString += " WHERE q.id.tickerSymbol = '" + parts[0] + "' AND q.id.quoteDate = " + parts[1];
        }

        //queryString += " ORDER BY q.id.quoteDate ASC";

        Query query = manager.createQuery(queryString);
        List<StockQuote> quotes = query.getResultList();
        Collection<SearchResult> results = new ArrayList<>();
        int count = 0;
        for (StockQuote quote : quotes) {
            String idString = quote.getTickerSymbol() + "|" + quote.getQuoteDate();
            SearchResult result = new SearchResult(searchString, idString, 1.0);
            results.add(result);
            count++;
            if (hitsPerPage > 0 && count >= hitsPerPage) {
                break;
            }
        }

        return results;
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }
}
