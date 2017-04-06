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

package qube.qai.procedure.archive;

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
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.utils.SimpleProcedure;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by rainbird on 12/25/15.
 */
public class DirectoryIndexer extends Procedure implements ProcedureConstants {


    //private Logger logger = LoggerFactory.getLogger("DirectoryIndexer");

    public static String NAME = "DirectoryIndexer";

    public static String DESCRIPTION = "indexes file names in the given directory and its child directories";

    public static String DIRECTORY_TO_INDEX = "directory to index";

    public static String INDEX_DIRECTORY = "index directory";

    public static String FIELD_FILE = "file";

    public static String FIELD_NAME = "name";


    private long fileCount = 0;
    private long directoryCount = 0;
    private String directoryToIndex;
    public String indexDirectory;

    public DirectoryIndexer() {
        super(NAME);
    }

    public DirectoryIndexer(Procedure procedure) {
        super(NAME, procedure);
    }

    public DirectoryIndexer(String directoryToIndex, String indexDirectory) {
        super(NAME, new SimpleProcedure());
        this.directoryToIndex = directoryToIndex;
        this.indexDirectory = indexDirectory;
    }

    private boolean hasBeenInitialized() {
        File baseDirectory = new File(indexDirectory);
        if (baseDirectory.exists()) {
            return true;
        }
        return false;
    }

    @Override
    public void execute() {

        try {
            Path path = FileSystems.getDefault().getPath(indexDirectory);
            Directory directory = FSDirectory.open(path);

            Analyzer analyzer = new StandardAnalyzer();
            analyzer.setVersion(Version.LUCENE_5_3_1);

            IndexWriterConfig conf = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, conf);
            writer.deleteAll();

            File baseDirectory = new File(directoryToIndex);
            indexFilesIn(baseDirectory, writer);

            writer.commit();
            writer.deleteUnusedFiles();

            info(writer.maxDoc() + " documents written " + fileCount + " files and " + directoryCount + " directories indexed");
//            arguments.addResult(INDEX_DIRECTORY, indexDirectory);

        } catch (IOException e) {
            error("error while indexing directory: " + directoryToIndex);

        }
    }

    private void indexFilesIn(File directory, IndexWriter writer) throws IOException {

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {
                directoryCount++;
                indexFilesIn(file, writer);
            } else {
                // i really don't want to see all the names of 880k or something files
                //logger.debug("indexing file: " + file.getName());
                Document doc = new Document();
                doc.add(new TextField(FIELD_NAME, file.getName(), Field.Store.YES));
                doc.add(new StringField(FIELD_FILE, file.getAbsolutePath(), Field.Store.YES));
                writer.addDocument(doc);
                fileCount++;
            }
        }

    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
//        arguments = new Arguments(DIRECTORY_TO_INDEX);
//        arguments.putResultNames(INDEX_DIRECTORY);
    }

    public String getDirectoryToIndex() {
        return directoryToIndex;
    }

    public void setDirectoryToIndex(String directoryToIndex) {
        this.directoryToIndex = directoryToIndex;
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(String indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

//    @thewebsemantic.Id
//    public String getUuid() {
//        return this.uuid;
//    }
}
