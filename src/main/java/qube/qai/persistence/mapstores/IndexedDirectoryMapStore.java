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
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 11/19/15.
 */
public class IndexedDirectoryMapStore implements MapStore<String, File> {

    private static Logger logger = LoggerFactory.getLogger("TarballMapStore");

    private long checkedFileCount = 0;

    private String directoryToIndex = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources";
    private String indexDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.resources.index";

    @Inject
    private SearchServiceInterface searchService;

    /**
     * this class is mainly for retrieving the resources from wiki-tarballs
     */
    public IndexedDirectoryMapStore() {
    }

    public IndexedDirectoryMapStore(String directoryToIndex, String indexDirectory) {
        this();
        this.directoryToIndex = directoryToIndex;
        this.indexDirectory = indexDirectory;
    }

    public void store(String key, File value) {
        // we are not writing in those files
    }

    public void storeAll(Map<String, File> map) {
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

    public File load(String key) {
        return findFile(key);
    }

    public Map<String, File> loadAll(Collection<String> keys) {
        return null;
    }

    /**
     * i don't know if i should really implement this one...
     * 880k names of files... i don't think so...
     *
     * @return
     */
    public Iterable<String> loadAllKeys() {
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

    public File findFile(String name) {

        Collection<SearchResult> results = searchService.searchInputString(name, "file", 10);
        if (results == null || results.isEmpty()) {
            return null;
        }

        String filename = results.iterator().next().getFilename();
        File found = new File(filename);
        if (!found.exists()) {
            throw new RuntimeException("Found file doesn't exist!?!");
        }

        return found;
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

    public SearchServiceInterface getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchServiceInterface searchService) {
        this.searchService = searchService;
    }
}
