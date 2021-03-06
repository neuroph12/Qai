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

package qube.qai.procedure.wikiripper;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;

import java.io.File;

/**
 * Created by rainbird on 11/3/15.
 */
public class WikiRipperProcedureTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger("WikiRipperProcedureTest");

    private boolean debug = true;

    private static String dummyWikiFileName = "/home/rainbird/projects/work/qai/test/testWiki.xml";
    private static String dummyWikiArchiveName = "/home/rainbird/projects/work/qai/test/testWiki.zip";

    /**
     * @throws Exception
     */
    public void testWikiRipper() throws Exception {

        WikiRipperProcedure ripperProcedure = new WikiRipperProcedure();
        ripperProcedure.setFileToRipName(dummyWikiArchiveName);
        ripperProcedure.setFileToArchiveName(dummyWikiFileName);
        ripperProcedure.setWiktionary(false);

        long start = System.currentTimeMillis();
        ripperProcedure.ripWikiFile();
        long end = System.currentTimeMillis();

        // now check that the file out can be found on the filesystem
        File file = new File(dummyWikiArchiveName);
        assertTrue("output file could not be found", file.exists());

        long duration = end - start;
        log("procedure completed in: " + duration + " ms");
        // result 1756343 ms which is around 30 mins-
        // that is with logging, without would be much faster
        File archiveZip = new File(dummyWikiArchiveName);
        assertTrue("output file must be there", archiveZip.exists());

        // we are done- just delete the file
        //archiveZip.deleteOnExit();
    }

    public static WikiRipperProcedure createTestWikiRipper() {
        WikiRipperProcedure ripperProcedure = new WikiRipperProcedure();
        QaiDataProvider<String> fileanmeSelectionOperator = new DataProvider<>(dummyWikiFileName);
        QaiDataProvider<String> archiveNameSelectionOperator = new DataProvider<>(dummyWikiArchiveName);
        QaiDataProvider<Boolean> isWiktionarySelectionOperator = new DataProvider<>(Boolean.FALSE);
//        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_FILENAME, fileanmeSelectionOperator);
//        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_TARGET_FILENAME, archiveNameSelectionOperator);
//        ripperProcedure.getArguments().setArgument(WikiRipperProcedure.INPUT_IS_WIKTIONARY, isWiktionarySelectionOperator);
        return ripperProcedure;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
