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

package qube.qai.services.implementation;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by rainbird on 1/6/16.
 */
public class DistributedSearchService implements SearchServiceInterface, MessageListener {

    private Logger logger = LoggerFactory.getLogger("DistributedSearchService");

    @Inject
    protected HazelcastInstance hazelcastInstance;

    protected String searchTopicName;

    protected Collection<SearchResult> results;

    public DistributedSearchService(String searchTopicName) {
        this.searchTopicName = searchTopicName;
    }

    public void initialize() {
        ITopic topic = hazelcastInstance.getTopic(searchTopicName);
        topic.addMessageListener(this);
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        // first set the results to null
        results = null;

        ITopic<SearchRequest> topic = hazelcastInstance.getTopic(searchTopicName);
        SearchRequest request = new SearchRequest(searchString, fieldName, hitsPerPage);
        topic.publish(request);

        try {
            while (results == null) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted with exception: " + e.getMessage());
        }

        return results;
    }

    @Override
    public void onMessage(Message message) {
        Object messageObject = message.getMessageObject();
        if (messageObject instanceof Collection) {
            results = (Collection<SearchResult>) messageObject;
        }
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    static public class SearchRequest implements Serializable {
        String searchString;
        String fieldName;
        int hitsPerPage;

        public SearchRequest(String searchString, String fieldName, int hitsPerPage) {
            this.searchString = searchString;
            this.fieldName = fieldName;
            this.hitsPerPage = hitsPerPage;
        }

        public String getSearchString() {
            return searchString;
        }

        public void setSearchString(String searchString) {
            this.searchString = searchString;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public int getHitsPerPage() {
            return hitsPerPage;
        }

        public void setHitsPerPage(int hitsPerPage) {
            this.hitsPerPage = hitsPerPage;
        }
    }
}
