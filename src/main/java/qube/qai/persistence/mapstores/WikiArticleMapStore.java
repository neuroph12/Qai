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
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.WikiArticle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by rainbird on 12/21/15.
 */
public class WikiArticleMapStore implements MapStore<String, WikiArticle> {

    private static Logger logger = LoggerFactory.getLogger("WikiArticleMapStore");

    public String zipFilename;

    private XStream xStream;

    public WikiArticleMapStore(String zipFilename) {
        logger.info("initializing with zip file: " + zipFilename);
        this.zipFilename = zipFilename;
        xStream = new XStream();
    }

    public void store(String key, WikiArticle value) {
        // do nothing
    }

    public void storeAll(Map<String, WikiArticle> map) {
        // do nothing
    }

    public void delete(String key) {
        // do nothing
    }

    public void deleteAll(Collection<String> keys) {
        // do nothing
    }

    public WikiArticle load(String key) {

        logger.info("loading entry with key: " + key);
        WikiArticle wikiArticle = null;

        try {
            ZipFile zipFile = new ZipFile(zipFilename);
            ZipEntry zipEntry = zipFile.getEntry(key);
            if (zipEntry != null) {
                InputStream stream = zipFile.getInputStream(zipEntry);

                XStream xStream = new XStream();
                wikiArticle = (WikiArticle) xStream.fromXML(stream);
            }
        } catch (IOException e) {
            logger.error("Exception while loading: " + key, e);
        }
        return wikiArticle;
    }

    public Map<String, WikiArticle> loadAll(Collection<String> keys) {
        logger.info("loadAll has been called");
        Map<String, WikiArticle> all = new HashMap<String, WikiArticle>();
        for (String key : keys) {
            WikiArticle article = load(key);
            all.put(key, article);
        }
        return all;
    }

    public Iterable<String> loadAllKeys() {
        logger.info("loadAllKeys has been called");
        return null;
    }
}
