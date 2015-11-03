package qube.qai.procedure.wikiripper;

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.delivery.sax.annotation.TextConsumer;
import qube.qai.persistence.WikiArticle;

import java.io.IOException;

/**
 * Created by rainbird on 11/3/15.
 */
@TextConsumer
public class WikiPageVisitor implements SAXVisitBefore, SAXVisitAfter {

    private boolean debug = false;

    private WikiArticle wikiArticle;


    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {

        log(element.getName().toString() + ":" + element.getText());
    }


    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {

        String title = element.getTextContent();
        wikiArticle = new WikiArticle();

        wikiArticle.setTitle(title);
        executionContext.getContext().setAttribute("wikiArticle", wikiArticle);

        log(element.getName().toString() + ":" + title);
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
