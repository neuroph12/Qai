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

package qube.qai.procedure.archive;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

import java.io.File;

/**
 * Created by rainbird on 11/3/15.
 */
public class WikiArchiveIndexerTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger("TestWikiArchiever");

    private String dummyWikiFileName = "/home/rainbird/projects/work/qai/test/testWiki.xml";
    private String dummyWikiArchiveName = "/home/rainbird/projects/work/qai/test/testWiki.zip";
    private String dummyIndexDirectory = "/home/rainbird/projects/work/qai/test/testWiki.index";

    private boolean debug = true;

    public void testWikiIndexer() throws Exception {

        WikiArchiveIndexer wikiIndexer = new WikiArchiveIndexer();
        wikiIndexer.setIndexDirectory(dummyIndexDirectory);
        wikiIndexer.setTargetFilename(dummyWikiArchiveName);

        long start = System.currentTimeMillis();
        wikiIndexer.indexZipFileEntries();
        long duration = System.currentTimeMillis() - start;
        log("procedure completed in: " + duration + " ms");

        File indexDirectory = new File(dummyIndexDirectory);
        assertTrue("index directory not found", indexDirectory.exists());

    }


    public void restWikiRipAndIndex() throws Exception {

//        String wikiToRip = "/media/rainbird/GIMEL/wiki-data/dewiki-20151226-pages-articles.xml";
//        String archiveToCreate = "/media/rainbird/GIMEL/wiki-archives/wikipedia_de.zip";
//        String indexDirectory = "/media/rainbird/GIMEL/wiki-archives/wikipedia_de.index";

        String wikiToRip = "/media/rainbird/GIMEL/wiki-data/dewiktionary-20151226-pages-articles.xml";
        String archiveToCreate = "/media/rainbird/GIMEL/wiki-archives/wiktionary_de.zip";
        String indexDirectory = "/media/rainbird/GIMEL/wiki-archives/wiktionary_de.index";

        WikiRipperProcedure ripperProcedure = new WikiRipperProcedure();
        QaiDataProvider<String> fileanmeSelectionOperator = new DataProvider<>(wikiToRip);
        QaiDataProvider<String> archiveNameSelectionOperator = new DataProvider<>(archiveToCreate);
        QaiDataProvider<Boolean> isWiktionarySelectionOperator = new DataProvider<>(Boolean.TRUE);
//        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_FILENAME, fileanmeSelectionOperator);
//        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_TARGET_FILENAME, archiveNameSelectionOperator);
//        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_IS_WIKTIONARY, isWiktionarySelectionOperator);

        WikiArchiveIndexer indexerProcedure = new WikiArchiveIndexer();
        indexerProcedure.getProcedureDescription().getProcedureInputs().addInput(new ValueNode(WikiArchiveIndexer.INPUT_INDEX_DIRECTORY, ripperProcedure));
        QaiDataProvider<String> selectionOperator = new DataProvider<>(indexDirectory);
//        indexerProcedure.getArguments().setArgument(WikiArchiveIndexer.INPUT_INDEX_DIRECTORY, selectionOperator);

        long start = System.currentTimeMillis();
        log("ripping: " + wikiToRip);
        ripperProcedure.execute();
        long duration = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        log("ripping finished, took: " + duration + "ms. now indexing...");
        indexerProcedure.execute();
        duration = System.currentTimeMillis() - start;
        log("indexing finished, took " + duration + "ms");
    }

    private void log(String message) {
        System.out.println(message);
    }

}
