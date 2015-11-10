package qube.qai.procedure;

import org.milyn.Smooks;
import qube.qai.procedure.wikiripper.WiktionaryPageTextVisitor;
import qube.qai.procedure.wikiripper.WikipediaPageTextVisitor;
import qube.qai.procedure.wikiripper.WikiPageVisitor;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.zip.ZipOutputStream;

/**
 * Created by rainbird on 11/3/15.
 */
public class WikiRipperProcedure {

    private boolean wiktionary = true;

    // file to rio the wiki-data from
    private String fileToRipName = "/media/rainbird/ALEPH/wiki-data/enwiktionary-20150413-pages-articles-multistream.xml";

    // file to archive the ripped articles at
    private String fileToArchiveName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";

    // mainly for testing reasons
    public WikiRipperProcedure() {
    }

    public WikiRipperProcedure(String fileToRipName, String fileToArchiveName) {
        this.fileToRipName = fileToRipName;
        this.fileToArchiveName = fileToArchiveName;
    }

    public void ripWikiFile() {
        File file = new File(fileToRipName);

        if (!file.exists()) {
            throw new IllegalArgumentException("file: '" + file.getName() + "' could not be found on filesystem");
        }

        try {
            InputStream stream = new FileInputStream(file);

            Smooks smooks = new Smooks();
            smooks.addVisitor(new WikiPageVisitor(), "page/title");

            // for the time being there are only two variants of wiki-parsing
            // one is for Wiktionary and the other is for Wikipedia
            if (wiktionary) {
                smooks.addVisitor(new WiktionaryPageTextVisitor(), "page/revision/text");
            } else {
                smooks.addVisitor(new WikipediaPageTextVisitor(), "page/revision/text");
            }

            // now create the output stream
            FileOutputStream outStream = new FileOutputStream(fileToArchiveName);
            ZipOutputStream zipStream = new ZipOutputStream(outStream);

            smooks.getApplicationContext().setAttribute("ZipOutputStream", zipStream);

            smooks.filterSource(new StreamSource(stream));

            zipStream.close();
            outStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileToRipName() {
        return fileToRipName;
    }

    public void setFileToRipName(String fileToRipName) {
        this.fileToRipName = fileToRipName;
    }

    public String getFileToArchiveName() {
        return fileToArchiveName;
    }

    public void setFileToArchiveName(String fileToArchiveName) {
        this.fileToArchiveName = fileToArchiveName;
    }

    public boolean isWiktionary() {
        return wiktionary;
    }

    public void setWiktionary(boolean wiktionary) {
        this.wiktionary = wiktionary;
    }
}
