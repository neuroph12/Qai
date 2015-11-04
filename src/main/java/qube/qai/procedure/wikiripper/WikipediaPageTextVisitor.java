package qube.qai.procedure.wikiripper;

import org.apache.commons.lang3.StringUtils;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.delivery.sax.annotation.TextConsumer;
import qube.qai.persistence.WikiArticle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by rainbird on 11/4/15.
 */
@TextConsumer
public class WikipediaPageTextVisitor implements SAXVisitBefore, SAXVisitAfter {

    private boolean debug = true;


    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        //log(element.getName().toString() + ":" +element.getText());
    }


    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {

        WikiArticle wikiArticle = (WikiArticle) executionContext.getContext().getAttribute("wikiArticle");

        String textContent = element.getTextContent();

        //log(element.getName().toString() + ":" + textContent);

        wikiArticle.setContent(textContent);

        // after this we will be persisting the whole in the zip file
        //log("adding page with title: " + wikiPage.getText());
        String title = wikiArticle.getTitle();

        // in order to make sure we have a filename
        if (StringUtils.isEmpty(title)) {
            log("No title found for the page- skipping");
            return;
        }

        // in order to avoid directories and Wikisaurus entries
        if (title.contains("/") || title.contains("Wikisaurus:")) {
            log("Apparently a directory of sorts: '" + title + "'");
            return;
        }

        // in order to make sure we have an english word
        if (!textContent.contains("==English==")) {
            log("Apparently not an English word- skipping: '" + title + "'");
            return;
        }

        addPageToStream(executionContext, wikiArticle, title);
    }

    private void addPageToStream(ExecutionContext executionContext, WikiArticle wikiPage, String title) {
        try {
            String zipEntryName = title + ".xml";
            log("Adding page: " + zipEntryName);

            ZipOutputStream zipStream = (ZipOutputStream) executionContext.getContext().getAttribute("ZipOutputStream");
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zipStream.putNextEntry(zipEntry);

            JAXBContext jaxbContext = JAXBContext.newInstance(WikiArticle.class);
            jaxbContext.createMarshaller().marshal(wikiPage, zipStream);
            zipStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
