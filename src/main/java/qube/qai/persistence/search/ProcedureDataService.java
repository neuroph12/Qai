package qube.qai.persistence.search;

import qube.qai.persistence.WikiArticle;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 1/25/17.
 */
public class ProcedureDataService implements DataServiceInterface {

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {
        return null;
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }
}
