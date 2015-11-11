package qube.qai.services.implementation;

import com.thoughtworks.xstream.XStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by rainbird on 11/9/15.
 */
public class WikiSearchService implements SearchServiceInterface {

        private boolean debug = true;

        public String INDEX_DIRECTORY;

        public String ZIP_FILE_NAME;

        public WikiSearchService(String indexDirectory, String zipFileName) {
            this.INDEX_DIRECTORY = indexDirectory;
            this.ZIP_FILE_NAME = zipFileName;
        }

        /* Sample application for searching an index
            * adapting the code for doing search and returning the contents of
            * the documents which are picked for reading
            */
        public Collection<String> searchInputString(String searchString, String fieldName, int hitsPerPage) {

            Collection<String> fileNames = new ArrayList<String>();
            try {
                Path path = FileSystems.getDefault().getPath(INDEX_DIRECTORY);
                Directory directory = FSDirectory.open(path);
                // Build a Query object
                Query query = new QueryParser(fieldName, new StandardAnalyzer()).parse(searchString);

                IndexReader reader = DirectoryReader.open(directory);
                IndexSearcher searcher = new IndexSearcher(reader);
                TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
                searcher.search(query, collector);
                log("total hits: " + collector.getTotalHits());
                ScoreDoc[] hits = collector.topDocs().scoreDocs;
                for (ScoreDoc hit : hits) {
                    Document doc = reader.document(hit.doc);
                    fileNames.add(doc.get("file"));
                    log(doc.get("file") + ": title: " + doc.get("title") + " (" + hit.score + ")");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return fileNames;
        }

        public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {

            WikiArticle wikiArticle = null;

            try {
                ZipFile zipFile = new ZipFile(ZIP_FILE_NAME);
                ZipEntry zipEntry = zipFile.getEntry(fileName);
                InputStream stream = zipFile.getInputStream(zipEntry);

                XStream xStream = new XStream();
                wikiArticle = (WikiArticle) xStream.fromXML(stream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return wikiArticle;
        }

        private void log(String message) {
            if (debug) {
                System.out.println(message);
            }
        }
}
