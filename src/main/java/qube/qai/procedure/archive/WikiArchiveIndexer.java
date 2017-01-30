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
import qube.qai.data.Arguments;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureDecorator;

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
public class WikiArchiveIndexer extends ProcedureDecorator {

    private Logger logger = LoggerFactory.getLogger("WikiArchiveIndexer");


    public static String NAME = "WikiArchiveIndexer";
    public static String DESCRIPTION = "Indexes the wiki articles which are in the given archive file to the target index directory";
    public static String FIELD_FILE = "file";
    public static String FIELD_TITLE = "title";
    public static String FIELD_CONTENT = "content";
    public static String FIELD_PERSON = "person";
    public static String FIELD_LOCATION = "location";
    public static String FIELD_DATE = "date";
    public static String FIELD_ORGANIZATION = "organization";

    public static String INPUT_TARGET_FILENAME = "TARGET_FILENAME";
    public static String INPUT_INDEX_DIRECTORY = "INDEX_DIRECTORY";

    //public String indexDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    //public String indexDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";
    private String indexDirectory;
    //public String targetFilename = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //public String targetFilename = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    public String targetFilename;

    public boolean analysePerson = false;

    public boolean analyseLocation = false;

    public boolean analyseDate = false;

    public boolean analyseOrganization = false;

    private long indexedFileCount = 0;

    public WikiArchiveIndexer(Procedure toDecorate) {
        super(NAME, toDecorate);
    }

    @Override
    public void execute() {

        toDecorate.execute();

        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(toDecorate.getArguments());
        }

        indexDirectory = (String) arguments.getSelector(INPUT_INDEX_DIRECTORY).getData();
        targetFilename = (String) arguments.getSelector(INPUT_TARGET_FILENAME).getData();
        indexZipFileEntries();
    }

    public void indexZipFileEntries() {

        try {
            ZipFile zipFile = new ZipFile(targetFilename);

            // feed the output directory name
            Path path = FileSystems.getDefault().getPath(indexDirectory);
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
                doc.add(new StringField(FIELD_TITLE, wikiPage.getTitle(), Field.Store.YES));
                doc.add(new TextField(FIELD_CONTENT, wikiPage.getContent(), Field.Store.NO));

                if (analysePerson) {
                    logger.debug("Analysing person: " + fileName);
                    analysePerson(wikiPage, doc);
                }

                if (analyseDate) {
                    logger.debug("Analysing date: " + fileName);
                    analyseDate(wikiPage, doc);
                }

                if (analyseLocation) {
                    logger.debug("Analysing location: " + fileName);
                    analyseLocation(wikiPage, doc);
                }

                if (analyseOrganization) {
                    logger.debug("Analysing organization: " + fileName);
                    analyseOrganization(wikiPage, doc);
                }

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

    private void analysePerson(WikiArticle article, Document document) {

    }

    private void analyseDate(WikiArticle article, Document document) {

    }

    private void analyseLocation(WikiArticle article, Document document) {

    }

    private void analyseOrganization(WikiArticle article, Document document) {

    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_TARGET_FILENAME, INPUT_INDEX_DIRECTORY);
        // arguments.putResultNames(); // no need to return a name for the indexed directory?
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(String indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    public String getTargetFilename() {
        return targetFilename;
    }

    public void setTargetFilename(String targetFilename) {
        this.targetFilename = targetFilename;
    }

    public long getIndexedFileCount() {
        return indexedFileCount;
    }

    public boolean isAnalysePerson() {
        return analysePerson;
    }

    public void setAnalysePerson(boolean analysePerson) {
        this.analysePerson = analysePerson;
    }

    public boolean isAnalyseLocation() {
        return analyseLocation;
    }

    public void setAnalyseLocation(boolean analyseLocation) {
        this.analyseLocation = analyseLocation;
    }

    public boolean isAnalyseDate() {
        return analyseDate;
    }

    public void setAnalyseDate(boolean analyseDate) {
        this.analyseDate = analyseDate;
    }

    public boolean isAnalyseOrganization() {
        return analyseOrganization;
    }

    public void setAnalyseOrganization(boolean analyseOrganization) {
        this.analyseOrganization = analyseOrganization;
    }
}
