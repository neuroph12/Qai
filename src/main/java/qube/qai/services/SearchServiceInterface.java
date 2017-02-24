package qube.qai.services;

import qube.qai.persistence.WikiArticle;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 11/9/15.
 */
public interface SearchServiceInterface {

    Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage);

    @Deprecated
        // @TODO remove this method, it is useful only for tests at this point
    WikiArticle retrieveDocumentContentFromZipFile(String fileName);
}
