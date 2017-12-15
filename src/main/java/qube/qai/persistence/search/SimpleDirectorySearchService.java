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

package qube.qai.persistence.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class SimpleDirectorySearchService implements SearchServiceInterface, QaiConstants {

    private static Logger logger = LoggerFactory.getLogger("SimpleDirectorySearchService");

    private String directoryName;

    private String context;

    public SimpleDirectorySearchService(String context, String directoryName) {
        this.context = context;
        this.directoryName = directoryName;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        Collection<SearchResult> results = new ArrayList<>();

        File file = new File(directoryName);
        if (!file.exists()) {
            //throw new IllegalArgumentException("Directory initialization failed- can't search without directory. '" + directoryName + "' does not exist");
            logger.error("Directory initialization failed- can't search without directory. '" + directoryName + "' does not exist");
            return results;
        }

        for (String name : file.list()) {
            results.add(new SearchResult(context, name, name, "directory listing", 1.0));
        }

        logger.info("Simple-Directory search-service: '" + context + "' brokering search: '" + searchString + "' with " + results.size() + " results");

        return results;
    }
}
