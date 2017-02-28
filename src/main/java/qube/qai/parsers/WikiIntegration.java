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

package qube.qai.parsers;

import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.model.WikiModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.WikiArticle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 12/24/15.
 */
public class WikiIntegration {

    private static Logger logger = LoggerFactory.getLogger("WikiIntegration");

    /**
     * this at least at this point, a class to integrate
     * text from wiki-articles in vaadin surrounding.
     * for starters, we will be converting wiki-tables
     * to vaadin tables, so that they can be used in
     * the gui and allow better user-interaction
     */
    public WikiIntegration() {
    }

    /**
     * strips the header out of a table
     *
     * @param html
     * @return
     */
    public static String[] stripHeader(String html) {

        //<div style="page-break-inside: avoid;">
        //<table class="wikitable sortable">

        Document doc = Jsoup.parse(html);
        Element table = doc.select("table.wikitable").first();
        if (table == null) {
            logger.error("table not found- is this really a wiki-table?");
            return null;
        }
        Elements header = table.select("th");
        StringBuffer headerBuffer = new StringBuffer();
        String[] titles = new String[header.size()];
        for (int i = 0; i < header.size(); i++) {
            Element element = header.get(i);
            Elements children = element.children();
            if (children != null && children.size() > 0) {
                titles[i] = children.get(0).text().trim();
            } else {
                titles[i] = element.text().trim();
            }
        }

        return titles;
    }

    /**
     * converts contents of wikiModel to html-string
     *
     * @param wikiArticle
     * @return
     */
    public static String wikiToHtml(WikiArticle wikiArticle) {
        return wikiToHtml(wikiArticle.getContent());
    }

    /**
     * converts contents of wikiModel to html-string
     *
     * @param wiki
     * @return
     */
    public static String wikiToHtml(String wiki) {
        StringBuilder builder = new StringBuilder();
        WikiModel model = createModel(wiki, builder);
        return builder.toString();
    }

    /**
     * creates a model out of the given html
     *
     * @param wiki
     * @param builder
     * @return
     */
    public static WikiModel createModel(String wiki, StringBuilder builder) {
        WikiModel wikiModel = new WikiModel("${image}", "${title}");
        try {
            WikiModel.toText(wikiModel, new HTMLConverter(), wiki, builder, false, false);
        } catch (IOException e) {
            logger.error("error while converting wiki-data to html: " + e.getMessage());
        }

        return wikiModel;
    }

    /**
     * creates a model out of the given html
     *
     * @param article
     * @param builder
     * @return
     */
    public static WikiModel createModel(WikiArticle article, StringBuilder builder) {
        return createModel(article.getContent(), builder);
    }

    /**
     * creates a model of the wiki-article
     * and returns the links found in that model
     *
     * @param article
     * @return
     */
    public static Collection<String> getLinksOf(WikiArticle article) {
        StringBuilder builder = new StringBuilder();
        return createModel(article, builder).getLinks();
    }

    /**
     * strips the data found in a given wiki-html table
     * assumes that the html is from wiki nad as the style "wikitable"
     *
     * @param html
     * @return
     */
    public static String[][] stripTableData(String html) {
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table.wikitable").first();
        Elements rows = table.select("tr");

        String[] header = stripHeader(html);
        ArrayList<RowData> tableData = new ArrayList<RowData>();
        // start from the 1st row to skip header
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.children();
            RowData rowDat = new RowData(header);
            for (int j = 0; j < cols.size(); j++) {
                Element td = cols.get(j);
                rowDat.addValue(j, td.text());
            }
            tableData.add(rowDat);
        }
        // copy the whole thing to return array
        String[][] data = new String[tableData.size()][header.length];
        for (int i = 0; i < data.length; i++) {
            RowData row = tableData.get(i);
            String[] values = row.rowData();
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = values[j];
            }
        }
        return data;
    }

    static class RowData {
        String[] header;
        Map<String, String> values = new HashMap<String, String>();

        RowData(String[] header) {
            this.header = header;
        }

        void addValue(int i, String value) {
            values.put(header[i], value);
        }

        String[] rowData() {
            String[] row = new String[header.length];
            for (int i = 0; i < header.length; i++) {
                row[i] = values.get(header[i]);
            }
            return row;
        }
    }
}
