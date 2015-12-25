package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import com.uwyn.jhighlight.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.archive.DirectoryIndexer;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;
import toools.io.file.Directory;

import javax.inject.Inject;
import java.io.*;
import java.util.*;

/**
 * Created by rainbird on 11/19/15.
 */
public class IndexedDirectoryMapStore implements MapStore<String, File> {

    private static Logger logger = LoggerFactory.getLogger("TarballMapStore");

    private long checkedFileCount = 0;

    private String directoryToIndex = "";
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

    /**
     * this should do the trick- but we first test the whole, of course
     */
    private void initialize() {
        DirectoryIndexer indexer = new DirectoryIndexer(directoryToIndex, indexDirectory);
        indexer.execute();
    }

    private boolean isInitialized() {
        File baseDirectory = new File(indexDirectory);
        if (baseDirectory.exists()) {
            return true;
        } else {
            return false;
        }
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
        String file2Search = StringUtils.replace(key, " ", "_");
        File baseDirectory = new File(indexDirectory);
        File found = findFile(file2Search, baseDirectory);
        logger.info("checked: " + checkedFileCount + " files at the end of search");
        checkedFileCount = 0;
        return found;
    }

    public Map<String, File> loadAll(Collection<String> keys) {
        return null;
    }

    public Iterable<String> loadAllKeys() {
        List<String> filenames = new ArrayList<String>();
        File file = new File(indexDirectory);
        String[] names = file.list();
        if (names == null) {
            return filenames;
        }
        for (String name : names) {
            filenames.add(name);
        }
        return filenames;
    }

    public File findFile(String name, File baseDirectory) {

        Collection<SearchResult> results =  searchService.searchInputString(name, "file", 10);
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
