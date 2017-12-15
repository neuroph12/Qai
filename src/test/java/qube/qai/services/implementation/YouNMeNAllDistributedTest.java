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

import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.DataSelectionOperator;
import qube.qai.main.QaiTestBase;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;
import qube.qai.procedure.wikiripper.WikiRipperProcedureTest;

import java.io.File;

/**
 * Created by rainbird on 1/12/16.
 */
public class YouNMeNAllDistributedTest extends QaiTestBase {

    private static String dummyWikiFileName = "/home/rainbird/projects/work/qube/qai/test/testWiki.xml";
    private static String dummyWikiArchiveName = "/home/rainbird/projects/work/qube/qai/test/testWiki.zip";
    private String dummyIndexDirectory = "/home/rainbird/projects/work/qai/test/testWiki.index";

    public void testYouNMeAndEveryoneWeKnow() throws Exception {

        WikiRipperProcedure ripperProcedure = WikiRipperProcedureTest.createTestWikiRipper();
        injector.injectMembers(ripperProcedure);

        WikiArchiveIndexer wikiIndexer = new WikiArchiveIndexer();
        wikiIndexer.getProcedureDescription().getProcedureInputs().addInput(new ValueNode(WikiArchiveIndexer.INPUT_INDEX_DIRECTORY, ripperProcedure));
        // these are the only additional arguments which we need in this case
        wikiIndexer.setAnalyseDate(true);
        wikiIndexer.setAnalyseLocation(true);
        wikiIndexer.setAnalysePerson(true);
        wikiIndexer.setAnalyseOrganization(true);
        SelectionOperator<String> selectionOperator = new DataSelectionOperator<String>(dummyIndexDirectory);
//        wikiIndexer.getArguments().setArgument(WikiArchiveIndexer.INPUT_INDEX_DIRECTORY, selectionOperator);
        injector.injectMembers(wikiIndexer);

        long start = System.currentTimeMillis();
        wikiIndexer.execute();
        long duration = System.currentTimeMillis() - start;
        log("procedure completed in: " + duration + " ms");

        File indexDirectory = new File(dummyIndexDirectory);
        assertTrue("index directory not found", indexDirectory.exists());
    }

}
