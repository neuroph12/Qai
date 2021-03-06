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

import org.apache.commons.lang.StringUtils;
import org.milyn.Smooks;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.zip.ZipOutputStream;

/**
 * Created by zenpunk on 11/3/15.
 */
public class WikiRipperProcedure extends Procedure {

    public static String NAME = "WikiRipperProcedure";
    public static String DESCRIPTION = "Rips wiki-based archives to individual files which are easier to parse and to read";

    public static String INPUT_FILENAME = "FILENAME";
    public static String INPUT_TARGET_FILENAME = "TARGET_FILENAME";
    public static String INPUT_IS_WIKTIONARY = "IS_WIKTIONARY";

    private Boolean isWiktionary;

    // file to rip the wiki-data from
    private String fileToRipName;

    // file to archive the ripped articles at
    private String fileToArchiveName;

    // mainly for testing reasons
    public WikiRipperProcedure() {
        super("WikiRipperProcedure");
    }

    @Override
    public void execute() {

        if (StringUtils.isEmpty(fileToRipName)) {
            error("Inputs are blank- " + INPUT_FILENAME + " is missing");
            throw new IllegalArgumentException("Inputs are blank- can't execute without input or target directory");
        }

        if (StringUtils.isEmpty(fileToArchiveName)) {
            error("Inputs are blank- " + INPUT_TARGET_FILENAME + " is missing");
            throw new IllegalArgumentException("Inputs are blank- can't execute without input or target directory");
        }

        if (isWiktionary == null) {
            error("Inputs are blank- " + INPUT_IS_WIKTIONARY + " is missing");
            throw new IllegalArgumentException("Inputs are blank- can't execute without input or target directory");
        }

        ripWikiFile();
    }

    @Override
    public Procedure createInstance() {
        return new WikiRipperProcedure();
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<String>(INPUT_FILENAME) {
            @Override
            public void setValue(String value) {
                super.setValue(value);
                fileToRipName = value;
            }
        });
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<String>(INPUT_TARGET_FILENAME) {
            @Override
            public void setValue(String value) {
                super.setValue(value);
                fileToArchiveName = value;
            }
        });
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<Boolean>(INPUT_IS_WIKTIONARY, MIMETYPE_BOOLEAN) {
            @Override
            public void setValue(Boolean value) {
                super.setValue(value);
                isWiktionary = value;
            }
        });
    }

    public void ripWikiFile() {

        if (StringUtils.isBlank(fileToRipName) || StringUtils.isBlank(fileToArchiveName) || isWiktionary == null) {
            throw new IllegalArgumentException("Inputs are blank- can't execute without input or target directory");
        }

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

}
