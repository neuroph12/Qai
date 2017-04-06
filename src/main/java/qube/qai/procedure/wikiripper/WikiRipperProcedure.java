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

import org.milyn.Smooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.Procedure;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.zip.ZipOutputStream;

/**
 * Created by rainbird on 11/3/15.
 */
public class WikiRipperProcedure extends Procedure {

    private static Logger logger = LoggerFactory.getLogger("WikiRipperProcedure");

    public static String NAME = "WikiRipperProcedure";
    public static String DESCRIPTION = "Rips wiki-based archives to individual files which are easier to parse and to read";

    public static String INPUT_FILENAME = "FILENAME";
    public static String INPUT_TARGET_FILENAME = "TARGET_FILENAME";
    public static String INPUT_IS_WIKTIONARY = "IS_WIKTIONARY";

    private boolean isWiktionary = false;

    // file to rip the wiki-data from
    //private String fileToRipName = "/media/rainbird/ALEPH/wiki-data/enwiktionary-20150413-pages-articles-multistream.xml";
    //private String fileToRipName = "/media/rainbird/ALEPH/wiki-data/enwiki-20150403-pages-articles.xml";
    private String fileToRipName;
    // file to archive the ripped articles at
    //private String fileToArchiveName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //private String fileToArchiveName = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";
    private String fileToArchiveName;

    // mainly for testing reasons
    public WikiRipperProcedure() {
        super("WikiRipperProcedure");
    }

    @Override
    public void execute() {
        ripWikiFile();
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
//        arguments = new Arguments(INPUT_FILENAME, INPUT_TARGET_FILENAME, INPUT_IS_WIKTIONARY);
    }

//    public WikiRipperProcedure(String fileToRipName, String fileToArchiveName) {
//        this.fileToRipName = fileToRipName;
//        this.fileToArchiveName = fileToArchiveName;
//        arguments = new Arguments(INPUT_FILENAME, INPUT_TARGET_FILENAME, INPUT_IS_WIKTIONARY);
//        arguments.setArgument(INPUT_FILENAME, createSelector(fileToRipName));
//        arguments.setArgument(INPUT_FILENAME, createSelector(fileToArchiveName));
//    }

    public void ripWikiFile() {

//        fileToRipName = (String) arguments.getSelector(INPUT_FILENAME).getData();
//        fileToArchiveName = (String) arguments.getSelector(INPUT_TARGET_FILENAME).getData();
//        isWiktionary = (Boolean) arguments.getSelector(INPUT_IS_WIKTIONARY).getData();

        File file = new File(fileToRipName);

        if (!file.exists()) {
            throw new IllegalArgumentException("file: '" + file.getName() + "' could not be found on filesystem");
        }

        try {
            InputStream stream = new FileInputStream(file);

            Smooks smooks = new Smooks();
            smooks.addVisitor(new WikiPageVisitor(), "page/title");

            // for the time being there are only two variants of wiki-parsing
            // one is for Wiktionary and the other is for Wikipedia
            if (isWiktionary) {
                smooks.addVisitor(new WiktionaryPageTextVisitor(), "page/revision/text");
            } else {
                smooks.addVisitor(new WikipediaPageTextVisitor(), "page/revision/text");
            }

            // now create the output stream
            FileOutputStream outStream = new FileOutputStream(fileToArchiveName);
            ZipOutputStream zipStream = new ZipOutputStream(outStream);

            smooks.getApplicationContext().setAttribute("ZipOutputStream", zipStream);

            smooks.filterSource(new StreamSource(stream));

            zipStream.close();
            outStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileToRipName() {
        return fileToRipName;
    }

    public void setFileToRipName(String fileToRipName) {
        this.fileToRipName = fileToRipName;
    }

    public String getFileToArchiveName() {
        return fileToArchiveName;
    }

    public void setFileToArchiveName(String fileToArchiveName) {
        this.fileToArchiveName = fileToArchiveName;
    }

    public boolean isWiktionary() {
        return isWiktionary;
    }

    public void setWiktionary(boolean wiktionary) {
        this.isWiktionary = wiktionary;
    }

//    @thewebsemantic.Id
//    public String getUuid() {
//        return this.uuid;
//    }
}
