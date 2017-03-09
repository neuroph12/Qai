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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Arguments;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 3/9/17.
 */
public class StockDataImporter extends Procedure {

    private Logger logger = LoggerFactory.getLogger("StockDataImporter");

    public static String NAME = "StockDataImporter";

    public static String DESCRIPTION = "enters the stock-market listings in csv-format in the database for later usage";

    public static String DIRECTORY_TO_FILE = "directory of the file";

    public static String LISTING_NAME = "listing name";

    public static String FIELD_FILENAME = "filename";

    public static String NUMBER_OF_ENTITIES = "numberOfEntities";

    private String directoryName;

    private String filename;

    private String listingName;

    private long numberOfEntities;

    public StockDataImporter() {
        super(NAME);
    }

    @Override
    public void execute() {

    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(DIRECTORY_TO_FILE, FIELD_FILENAME, LISTING_NAME);
        arguments.putResultNames(NUMBER_OF_ENTITIES);
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getListingName() {
        return listingName;
    }

    public void setListingName(String listingName) {
        this.listingName = listingName;
    }

    public long getNumberOfEntities() {
        return numberOfEntities;
    }

    public void setNumberOfEntities(long numberOfEntities) {
        this.numberOfEntities = numberOfEntities;
    }
}
