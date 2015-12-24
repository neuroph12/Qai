package qube.qai.persistence.mapstores;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by rainbird on 12/24/15.
 */
public class TestTarballMapStore extends QaiTestBase {

    private static Logger logger = LoggerFactory.getLogger("TestTarballMapStore");

    private static String wikiArticleName = "";
    private static String tarballsBaseDirectory = "/media/rainbird/ALEPH/wiki-data/";
    private static String wiktionaryTarballFile = "enwiktionary-20121104-local-media-1.tar";
    private static String wikipediaTarballFile = "enwiki-20121104-local-media-1.tar";
    // [[File:...|thumb|...]]
    private static String wikiFileFormatStart = "[[File:";
    private static String wikiFileFormatEnd = "|";
    private String darwinPage = "Charles Darwin.xml";

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    public void testTarballMapStore() throws Exception {

        // ok- this is simple really- we pick a wiki-article
        // and query the resources required- use of course a wiki-model
        // for the purpose, and check that the tarball map-store
        // can actually locate and retrieve them

        TarballMapStore mapStore = new TarballMapStore(wikipediaTarballFile);
        WikiArticle article = searchService.retrieveDocumentContentFromZipFile(darwinPage);
        assertNotNull(article);

        log(article.getContent());

        if (article.getContent().contains(wikiFileFormatStart)) {
            logger.info("we are good- found some resources to look for");
        } else {
            logger.info("no file reference in this article- nothing to search for");
        }

        String[] filenames = StringUtils.substringsBetween(article.getContent(), wikiFileFormatStart, wikiFileFormatEnd);
        for (String key : filenames) {
            logger.info("currently checking file: " + key);
            Object result = mapStore.load(key);
            assertNotNull(result);
        }
    }

    public void restTarballReading() throws Exception {
        // well, not much here really...
        BufferedOutputStream outputStream = null;
        TarArchiveInputStream tarInputStream = null;

        String filename = tarballsBaseDirectory + wiktionaryTarballFile;
        File file = new File(filename);
        tarInputStream = new TarArchiveInputStream(new FileInputStream(file));

        long fileCount = 0;
        long directoryCount = 0;
        TarArchiveEntry entry;
        while ((entry = tarInputStream.getNextTarEntry()) != null) {
            if (!entry.isDirectory()) {
                File compressedFile = entry.getFile();
                String name = entry.getName();
                logger.info("found file: " + name);
                fileCount++;
            } else {
                directoryCount++;
            }
        }

        logger.info("there are: " + fileCount + "files and " + directoryCount +" directories in the archive");

        //outputStream.flush();
        //outputStream.close();
    }

    private void log(String message) {
        System.out.println(message);
    }
}
