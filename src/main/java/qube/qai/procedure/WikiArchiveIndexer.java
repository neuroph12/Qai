package qube.qai.procedure;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by rainbird on 11/3/15.
 */
public class WikiArchiveIndexer {

    private boolean debug = true;

    public String INDEX_DIRECTORY = "./data/wiktionary/lucene/";

    public void indexZipFileEntries(String zipFileName) throws Exception {

        //File docs = new File(inputDirectory);
        ZipFile zipFile = new ZipFile(zipFileName);

        Path path = FileSystems.getDefault().getPath(INDEX_DIRECTORY);
        Directory directory = FSDirectory.open(path);
        // with version is deprecated Version.LUCENE_4_10_0
        Analyzer analyzer = new StandardAnalyzer();
        analyzer.setVersion(Version.LUCENE_5_3_1);
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, conf);
        writer.deleteAll();

        JAXBContext jaxbContext = JAXBContext.newInstance(WikiArticle.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();

            InputStream stream = zipFile.getInputStream(zipEntry);
            WikiArticle wikiPage = (WikiArticle) unmarshaller.unmarshal(stream);
            String fileName = zipEntry.getName();
            Document doc = new Document();
            doc.add(new StringField("file", fileName, Field.Store.YES));

            log("Indexing zip-entry: " + fileName);

            doc.add(new TextField("title", wikiPage.getTitle(), Field.Store.YES));
            doc.add(new TextField("content", wikiPage.getContent(), Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.commit();
        writer.deleteUnusedFiles();
        log(writer.maxDoc() + " documents written");
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
