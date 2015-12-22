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

    private boolean debug = false;

    //public String INDEX_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    public String INDEX_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";

    //public String ZIP_FILE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    public String ZIP_FILE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

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
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();

                InputStream stream = zipFile.getInputStream(zipEntry);
                WikiArticle wikiPage = (WikiArticle) xStream.fromXML(stream);
                String fileName = zipEntry.getName();
                Document doc = new Document();
                doc.add(new StringField("file", fileName, Field.Store.YES));

                log("Indexing zip-entry: " + fileName);

                doc.add(new TextField("title", wikiPage.getTitle(), Field.Store.YES));
                doc.add(new TextField("content", wikiPage.getContent(), Field.Store.NO));
                writer.addDocument(doc);
                progressPercentage++;
            }
            writer.commit();
            writer.deleteUnusedFiles();
            log(writer.maxDoc() + " documents written");
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

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
