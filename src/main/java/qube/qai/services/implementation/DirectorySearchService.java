/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

package qube.qai.services.implementation;

import org.apache.commons.lang3.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 12/25/15.
 */
public class DirectorySearchService implements SearchServiceInterface {

    private Logger logger = LoggerFactory.getLogger("DirectorySearchService");

    public static String FIELD_FILE = "file";

    public static String FIELD_NAME = "name";

    private String context;

    private String indexDirectory;

    public DirectorySearchService() {
    }

    public DirectorySearchService(String context, String indexDirectory) {
        this.context = context;
        this.indexDirectory = indexDirectory;
    }

    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        Collection<SearchResult> results = new ArrayList<SearchResult>();

        if (StringUtils.isEmpty(searchString)) {
            return results;
        }

        try {
            Path path = FileSystems.getDefault().getPath(indexDirectory);
            Directory directory = FSDirectory.open(path);
            // Build a Query object
            Query query = new QueryParser(FIELD_NAME, new StandardAnalyzer()).parse(searchString);

            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(query, collector);
            logger.info("total hits: " + collector.getTotalHits());
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for (ScoreDoc hit : hits) {
                Document doc = reader.document(hit.doc);
                String desc = "Search for: '" + searchString + "'";
                SearchResult result = new SearchResult(context, doc.get(FIELD_NAME), doc.get(FIELD_FILE), desc, hit.score);
                results.add(result);
                logger.info(doc.get(FIELD_FILE) + " (" + hit.score + ")");
            }
        } catch (IOException e) {
            logger.error("IOException during wiki: ", e);
        } catch (ParseException e) {
            logger.error("ParseException during wiki: ", e);
        }

        logger.info("DirectorySearchService: " + context + " brokered " + results.size() + " for wiki: '" + searchString + "'");

        return results;
    }

    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(String indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
