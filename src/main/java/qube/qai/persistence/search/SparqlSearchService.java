package qube.qai.persistence.search;

import org.apache.jena.rdf.model.Model;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 6/8/16.
 */
public class SparqlSearchService implements DataServiceInterface {

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {
        return null;
    }

    @Override
    public void save(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void remove(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }
}
