package qube.qai.persistence.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 6/1/16.
 */
public class RDFTriplesSearchService implements DataServiceInterface {

    @Inject
    private EntityManager manager;

    private String tableName;

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        String queryString = "SELECT t.uuid FROM RDFTriple t";

        if ("OBJECT".equalsIgnoreCase(fieldName)) {
            queryString += " WHERE t.object LIKE '" + searchString + "'";
        } else if ("SUBJECT".equalsIgnoreCase(fieldName)) {
            queryString += " WHERE t.subject LIKE '" + searchString + "'";
        } else if ("PREDICATE".equalsIgnoreCase(fieldName)) {
            queryString += " WHERE t.predicate LIKE '" + searchString + "'";
        } else {
            // if you don't put anything, we'll assume this
            String[] parts = StringUtils.split(searchString, '|');
            queryString += " WHERE t.subject LIKE '" + parts[0] + "' AND t.predicate LIKE '" + parts[1] + "'";
        }

        Query query = manager.createQuery(queryString, String.class);
        List<String> uuids = query.getResultList();
        List<SearchResult> results = new ArrayList<>();
        int count = 0;
        for (String uuid : uuids) {
            SearchResult result = new SearchResult(searchString, uuid, 1.0);
            results.add(result);
            count++;
            // make sure we leep to the maximum hits
            if (hitsPerPage > 0 && count >= hitsPerPage) {
                break;
            }
        }

        return results;
    }

    @Override
    public void save(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void update(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }
}
