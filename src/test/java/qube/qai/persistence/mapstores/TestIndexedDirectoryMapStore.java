package qube.qai.persistence.mapstores;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;

/**
 * Created by rainbird on 12/24/15.
 */
public class TestIndexedDirectoryMapStore extends QaiTestBase {

    private static Logger logger = LoggerFactory.getLogger("TestTarballMapStore");

    private static String wikiArticleName = "";
    private static String tarballsBaseDirectory = "/media/rainbird/ALEPH/wiki-data/";
    private static String wiktionaryTarballFile = "enwiktionary-20121104-local-media-1.tar";
    private static String wikipediaTarballFile = "enwiki-20121104-local-media-1.tar";
    private static String wikipediaResourceDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";
    private static String wikipediaResourceIndexDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources.index";
    // [[File:...|thumb|...]] is how you define an image in wiki-format
    private static String wikiFileFormatStart = "[[File:";
    private static String wikiFileFormatEnd = "|";
    private String darwinPage = "Charles Darwin.xml";

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    public void testIndexedDirectoryMapStore() throws Exception {

        // ok- this is simple really- we pick a wiki-article
        // and query the resources required- use of course a wiki-model
        // for the purpose, and check that the tarball map-store
        // can actually locate and retrieve them
        DirectorySearchService directorySearchService = new DirectorySearchService(wikipediaResourceIndexDirectory);
        IndexedDirectoryMapStore mapStore = new IndexedDirectoryMapStore(wikipediaResourceDirectory, wikipediaResourceIndexDirectory);
        mapStore.setSearchService(directorySearchService);
        WikiArticle article = searchService.retrieveDocumentContentFromZipFile(darwinPage);
        assertNotNull("seriously?!?", article);

        //log(article.getContent());

        if (article.getContent().contains(wikiFileFormatStart)) {
            logger.info("we are good- found some resources to look for");
        } else {
            logger.info("no file reference in this article- nothing to search for");
        }

        int foundCount = 0;
        int notFoundCount = 0;
        String[] filenames = StringUtils.substringsBetween(article.getContent(), wikiFileFormatStart, wikiFileFormatEnd);
        for (String key : filenames) {
            logger.info("currently checking file: " + key);
            File result = mapStore.load(key);
            //assertNotNull(result);
            if (result == null) {
                notFoundCount++;
                logger.info("file: '" + key + "' could not be found");
            } else {
                foundCount++;
                logger.info("file: '" + key + "' is OK");
            }
        }

        logger.info("at the end of the search found: " + foundCount + " and not found: " + notFoundCount);
    }

    private void log(String message) {
        System.out.println(message);
    }
}
