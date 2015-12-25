package qube.qai.procedure.archive;

import com.thoughtworks.xstream.XStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by rainbird on 11/3/15.
 */
public class WikiArchiveIndexer extends Procedure {

    private Logger logger = LoggerFactory.getLogger("WikiArchiveIndexer");

    public static String FIELD_FILE = "file";
    public static String FIELD_TITLE = "title";
    public static String FIELD_CONTENT = "content";

    //public String INDEX_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    public String INDEX_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";

    //public String ZIP_FILE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    public String ZIP_FILE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    private long indexedFileCount = 0;

    @Override
    public void execute() {
        this.INDEX_DIRECTORY = (String) arguments.getSelector("INDEX_DIRECTORY").getData();
        this.ZIP_FILE = (String) arguments.getSelector("ZIP_FILE").getData();
        indexZipFileEntries();
    }

    public void indexZipFileEntries() {

        //File docs = new File(inputDirectory);
        try {
            ZipFile zipFile = new ZipFile(ZIP_FILE);

            // feed the output directory name
            Path path = FileSystems.getDefault().getPath(INDEX_DIRECTORY);
            Directory directory = FSDirectory.open(path);

            // create the analyzer
            Analyzer analyzer = new StandardAnalyzer();
            analyzer.setVersion(Version.LUCENE_5_3_1);
            IndexWriterConfig conf = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, conf);
            writer.deleteAll();

            XStream xStream = new XStream();
            progressPercentage = 0;
            indexedFileCount = 0;
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {

                ZipEntry zipEntry = zipEntries.nextElement();
                InputStream stream = zipFile.getInputStream(zipEntry);
                WikiArticle wikiPage = (WikiArticle) xStream.fromXML(stream);
                String fileName = zipEntry.getName();
                logger.debug("Indexing zip-entry: " + fileName);

                Document doc = new Document();
                doc.add(new StringField(FIELD_FILE, fileName, Field.Store.YES));
                doc.add(new TextField(FIELD_TITLE, wikiPage.getTitle(), Field.Store.YES));
                doc.add(new TextField(FIELD_CONTENT, wikiPage.getContent(), Field.Store.NO));
                writer.addDocument(doc);
                progressPercentage++;
                indexedFileCount++;
            }

            writer.commit();
            writer.deleteUnusedFiles();
            logger.debug(writer.maxDoc() + " documents written");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildArguments() {

    }

    public String getINDEX_DIRECTORY() {
        return INDEX_DIRECTORY;
    }

    public void setINDEX_DIRECTORY(String INDEX_DIRECTORY) {
        this.INDEX_DIRECTORY = INDEX_DIRECTORY;
    }

    public String getZIP_FILE() {
        return ZIP_FILE;
    }

    public void setZIP_FILE(String ZIP_FILE) {
        this.ZIP_FILE = ZIP_FILE;
    }

    public long getIndexedFileCount() {
        return indexedFileCount;
    }
}
