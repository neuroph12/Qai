package qube.qai.services;

import qube.qai.persistence.WikiArticle;

import java.util.Collection;

/**
 * Created by rainbird on 11/9/15.
 */
public interface SearchServiceInterface {

    public Collection<String> searchInputString(String searchString, String fieldName, int hitsPerPage);

    public WikiArticle retrieveDocumentContentFromZipFile(String fileName);
}
