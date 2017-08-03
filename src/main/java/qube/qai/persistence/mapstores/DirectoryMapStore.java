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

package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.ResourceData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 12/13/15.
 */
public class DirectoryMapStore implements MapStore<String, ResourceData> {

    private Logger logger = LoggerFactory.getLogger("DirectoryMapStore");

    private String directory;

    public DirectoryMapStore() {
        // any initialization required here?
    }

    public DirectoryMapStore(String directory) {
        this();
        this.directory = directory;
        // there are better ways of doing this really
        if (!this.directory.endsWith("/")) {
            this.directory = directory + "/";
        }
    }

    public void store(String key, ResourceData value) {
        File file = new File(directory + key);
        try {
            value.writeDataToFile(file);
        } catch (IOException e) {
            logger.error("error while reading file content", e);
        }
    }

    public void storeAll(Map<String, ResourceData> map) {
        for (String key : map.keySet()) {
            store(key, map.get(key));
        }
    }

    public void delete(String key) {
        // no idea... just forget about it i suppose
    }

    public void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    public ResourceData load(String key) {
        logger.info("load is called with key: " + key);
        ResourceData data = null;
        File file = new File(directory);
        if (file == null || !file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("Given is not a directory- can't return searched file in directory '" + directory + "'");
        }
        for (File found : file.listFiles()) {
            if (key.equals(found.getName())) {
                data = new ResourceData();
                data.setName(found.getName());
                try {
                    data.readFileData(found);
                } catch (IOException e) {
                    logger.error("error while reading file content", e);
                }

                break;
            }
        }

        return data;
    }

    public Map<String, ResourceData> loadAll(Collection<String> keys) {
        logger.info("loadAll is called");
        return null;
    }

    /**
     * i don't know if i should really implement this one...
     * 880k names of files... i don't think so...
     *
     * @return
     */
    public Iterable<String> loadAllKeys() {
        logger.info("loadAllKeys has been called...");
        List<String> filenames = new ArrayList<String>();
        // this would return only one item, which is not a file after all
        // if you really want to implement this method
        // you would have to traverse the sub-directories, obviously
//        File file = new File(indexDirectory);
//        String[] names = file.list();
//        if (names == null) {
//            return filenames;
//        }
//        for (String name : names) {
//            filenames.add(name);
//        }
        return filenames;
    }

//    public File findFile(String name) {
//
//        Collection<SearchResult> results = searchService.searchInputString(name, "file", 10);
//        if (results == null || results.isEmpty()) {
//            return null;
//        }
//
//        String filename = results.iterator().next().getUuid();
//        File found = new File(filename);
//        if (!found.exists()) {
//            throw new RuntimeException("Found file doesn't exist!?!");
//        }
//
//        return found;
//    }
}
