/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.procedure.wikiripper;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.milyn.delivery.sax.SAXVisitBefore;
import org.milyn.delivery.sax.annotation.TextConsumer;
import qube.qai.persistence.WikiArticle;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by rainbird on 11/4/15.
 */
@TextConsumer
public class WikipediaPageTextVisitor implements SAXVisitBefore, SAXVisitAfter {

    private boolean debug = false;

    private XStream xStream;

    public WikipediaPageTextVisitor() {
        this.xStream = new XStream();
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        //log(element.getName().toString() + ":" +element.getText());
    }


    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {

        WikiArticle wikiArticle = (WikiArticle) executionContext.getContext().getAttribute("wikiArticle");

        String textContent = element.getTextContent();
        // many of the pages are simply redirect pages without content
        if (StringUtils.containsIgnoreCase(textContent, "#REDIRECT")) {
            log("Apparently a redirect page without content");
            return;
        }

        // {{disambig}} disambiguation pages are also not necessary, remove them as well
        if (StringUtils.containsIgnoreCase(textContent, "{{disambig}}")
                || StringUtils.containsIgnoreCase(textContent, "{{disambiguation}}")) {
            log("Apparently a disambiguation page without content");
            return;
        }

        wikiArticle.setContent(textContent);

        // after this we will be persisting the whole in the zip file
        String title = wikiArticle.getTitle();

        // category: pages are also confusing
        if (StringUtils.containsIgnoreCase(title, "Category:")) {
            log("'Category:' page, we are skipping those");
            return;
        }

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

        addPageToStream(executionContext, wikiArticle, title);
    }

    private void addPageToStream(ExecutionContext executionContext, WikiArticle wikiPage, String title) {
        try {
            String zipEntryName = title + ".xml";
            log("Adding page: " + zipEntryName);

            ZipOutputStream zipStream = (ZipOutputStream) executionContext.getContext().getAttribute("ZipOutputStream");
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zipStream.putNextEntry(zipEntry);

            xStream.toXML(wikiPage, zipStream);
            zipStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
