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

package qube.qai.main;


/**
 * Created by rainbird on 6/25/17.
 */
public interface QaiConstants {

    public String NODE_NAME = "QaiNode";

    public String USERS = "Users";

    public String USER_SESSIONS = "UserSessions";

    public String USER_ROLES = "UserRoles";

    public String STOCK_GROUPS = "StockGroups";

    public String STOCK_ENTITIES = "StockEntities";

    static String STOCK_QUOTES = "StockQuotes";

    public String PROCEDURES = "Procedures";

    public String PROCEDURE_TEMPLATES = "ProcedureTemplates";

    public String WIKIPEDIA = "Wikipedia_en";

    //@Named("PROCEDURE_BASE_DIRECTORY")
    public String PROCEDURE_BASE_DIRECTORY = "PROCEDURE_BASE_DIRECTORY";

    public String PROCEDURE_MODEL_DIRECTORY = "PROCEDURE_MODEL_DIRECTORY";

    public String UPLOAD_FILE_DIRECTORY = "UPLOAD_FILE_DIRECTORY";

    //    @Inject
//    @Named("WIKIPEDIA_ARCHIVE")
    public String WIKIPEDIA_ARCHIVE = "WIKIPEDIA_ARCHIVE";
    //public static final String WIKIPEDIA_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";
    //public static final String WIKIPEDIA_ARCHIVE = "/media/pi/BET/wiki-archives/wikipedia_en.zip";

    //    @Inject
//    @Named("WIKIPEDIA_DIRECTORY")
    public String WIKIPEDIA_DIRECTORY = "WIKIPEDIA_DIRECTORY";
    //public static final String WIKIPEDIA_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";
    //public static final String WIKIPEDIA_DIRECTORY = "/media/pi/BET/wiki-archives/wikipedia_en.index";

    public String WIKIPEDIA_RESOURCES = "WikiResources_en";

    public String MOLECULAR_RESOURCES = "MolecularResources";

    public String PDF_FILE_RESOURCES = "PdfFileResources";

    //@Named("WIKIPEDIA_RESOURCE_DIRECTORY")
    public String WIKIPEDIA_RESOURCE_DIRECTORY = "WIKIPEDIA_RESOURCE_DIRECTORY";
    //public static final String WIKIPEDIA_RESOURCE_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";
    //public static final String WIKIPEDIA_RESOURCE_DIRECTORY = "/media/pi/BET/wiki-archives/wikipedia_en.resources";

    //@Named("WIKIPEDIA_RESOURCE_INDEX")
    public String WIKIPEDIA_RESOURCE_INDEX = "WIKIPEDIA_RESOURCE_INDEX";
    //public static final String WIKIPEDIA_RESOURCE_INDEX = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources.index";
    //public static final String WIKIPEDIA_RESOURCE_INDEX = "/media/pi/BET/wiki-archives/wikipedia_en.resources.index";

    public String WIKTIONARY = "Wiktionary_en";

    //@InjectConfig(value = "WIKTIONARY_ARCHIVE")
    public String WIKTIONARY_ARCHIVE = "WIKTIONARY_ARCHIVE";
    //public static final String WIKTIONARY_ARCHIVE = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";
    //public static final String WIKTIONARY_ARCHIVE = "/media/pi/BET/wiki-archives/wiktionary_en.zip";

    //@InjectConfig("WIKTIONARY_DIRECTORY")
    public String WIKTIONARY_DIRECTORY = "WIKTIONARY_DIRECTORY";
    //public static final String WIKTIONARY_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";
    //public static final String WIKTIONARY_DIRECTORY = "/media/pi/BET/wiki-archives/wiktionary_en.index";

    public String WIKTIONARY_RESOURCES = "WiktionaryResources";

    //@InjectConfig(value = "WIKTIONARY_RESOURCE_DIRECTORY")
    public String WIKTIONARY_RESOURCE_DIRECTORY = "WIKTIONARY_RESOURCE_DIRECTORY";
    //public static final String WIKTIONARY_RESOURCE_DIRECTORY = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.resources";
    //public static final String WIKTIONARY_RESOURCE_DIRECTORY = "/media/pi/BET/wiki-archives/wiktionary_en.resources";

    //@InjectConfig(value = "WIKTIONARY_RESOURCE_INDEX")
    public String WIKTIONARY_RESOURCE_INDEX = "WIKTIONARY_RESOURCE_INDEX";
    //public static final String WIKTIONARY_RESOURCE_INDEX = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.resources.index";
    //public static final String WIKTIONARY_RESOURCE_INDEX = "/media/pi/BET/wiki-archives/wiktionary_en.resources.index";
}
