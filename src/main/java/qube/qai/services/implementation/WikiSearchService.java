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

package qube.qai.services.implementation;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.services.SearchServiceInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 11/9/15.
 */
public class WikiSearchService implements SearchServiceInterface {

    private Logger logger = LoggerFactory.getLogger("WikiSearchService");

    public String INDEX_DIRECTORY;

    public String ZIP_FILE_NAME;

    private String context;

    private Directory directory;

    private boolean isInitialized = false;

    public WikiSearchService() {
    }

    public WikiSearchService(String context, String indexDirectory, String zipFileName) {
        this.context = context;
        this.INDEX_DIRECTORY = indexDirectory;
        this.ZIP_FILE_NAME = zipFileName;
    }

    public void initialize() {

        try {

            if (StringUtils.isEmpty(INDEX_DIRECTORY)) {
                throw new RuntimeException("Initialization problem: INDEX_DIRECTORY has not been defined");
            }

            File file = new File(INDEX_DIRECTORY);
            if (!file.exists() || !file.isDirectory()) {
                throw new RuntimeException("Directory: " + INDEX_DIRECTORY + " either does not exist, or is not a directory!");
            }
            directory = FSDirectory.open(file.toPath(), NoLockFactory.INSTANCE);

        } catch (IOException e) {
            e.printStackTrace();
        }

        isInitialized = true;
    }

    /* Sample application for searching an index
     * adapting the code for doing search and returning the contents of
     * the documents which are picked for reading
    */
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        if (!isInitialized) {
            initialize();
        }

        Collection<SearchResult> results = new ArrayList<SearchResult>();

        if (StringUtils.isEmpty(searchString)) {
            return results;
        }

        try {

            // Build a Query object
            Query query = new QueryParser(fieldName, new StandardAnalyzer()).parse(searchString);

            DirectoryReader reader = DirectoryReader.open(directory);

            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(query, collector);
            logger.debug("total hits: " + collector.getTotalHits());
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for (ScoreDoc hit : hits) {
                Document doc = reader.document(hit.doc);
                String desc = "Search term: '" + searchString + "'";
                SearchResult result = new SearchResult(context, doc.get("title"), doc.get("file"), desc, hit.score);
                results.add(result);
                logger.debug(doc.get("file") + ": title: " + doc.get("title") + " (" + hit.score + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return results;
    }

//    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
//
//        WikiArticle wikiArticle = null;
//
//        try {
//            ZipFile zipFile = new ZipFile(ZIP_FILE_NAME);
//            ZipEntry zipEntry = zipFile.getEntry(fileName);
//            if (zipEntry == null) {
//                return null;
//            }
//            InputStream stream = zipFile.getInputStream(zipEntry);
//
//            XStream xStream = new XStream();
//            wikiArticle = (WikiArticle) xStream.fromXML(stream);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return wikiArticle;
//    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    class AbsoluteDirectory extends Directory {

        private File file;
        private LockFactory factory;

        public AbsoluteDirectory(File file, LockFactory factory) {
            this.file = file;
            this.factory = factory;
        }

        @Override
        public String[] listAll() throws IOException {
            return file.list();
        }

        @Override
        public void deleteFile(String name) throws IOException {
            File[] allFiles = file.listFiles();
            for (int i = 0; i < allFiles.length; i++) {
                if (allFiles[i].getName().equals(name)) {
                    allFiles[i].delete();
                    break;
                }
            }
        }

        @Override
        public long fileLength(String name) throws IOException {
            return 0;
        }

        @Override
        public IndexOutput createOutput(String name, IOContext context) throws IOException {
            return null;
        }

        @Override
        public void sync(Collection<String> names) throws IOException {

        }

        @Override
        public void renameFile(String source, String dest) throws IOException {

        }

        @Override
        public IndexInput openInput(String name, IOContext context) throws IOException {
            return null;
        }

        @Override
        public Lock obtainLock(String name) throws IOException {
            return factory.obtainLock(null, name);
        }

        @Override
        public void close() throws IOException {

        }

//        @Override
//        public IndexOutput createTempOutput(String s, String s1, IOContext ioContext) throws IOException {
//            return null;
//        }
    }

}
