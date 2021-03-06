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

package qube.qai.services.implementation;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.message.QaiMessageListener;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by rainbird on 1/6/16.
 */
public class DistributedSearchListener extends QaiMessageListener {

    private Logger logger = LoggerFactory.getLogger("DatabaseSearchService");

    @Inject
    private HazelcastInstance hazelcastInstance;

    private SearchServiceInterface searchService;

    private String topicName;

    private Collection<SearchResult> lastResults;

    public DistributedSearchListener(String topicName) {
        this.topicName = topicName;
    }

    public void initialize() {

        if (hazelcastInstance == null || searchService == null) {
            throw new RuntimeException("Hazelcast instance or the wiki service to be employed are not configured right- exiting");
        }

        ITopic topic = hazelcastInstance.getTopic(topicName);
        topic.addMessageListener(this);
        logger.info("Registered listener for '" + topicName + "' wiki-service");
    }

    @Override
    public void onMessage(Message message) {

        Object mesasgeObject = message.getMessageObject();

        if (mesasgeObject instanceof DistributedSearchService.SearchRequest) {
            DistributedSearchService.SearchRequest request = (DistributedSearchService.SearchRequest) mesasgeObject;
            lastResults = searchService.searchInputString(request.searchString, request.fieldName, request.hitsPerPage);
            if (lastResults != null && !lastResults.isEmpty()) {
                ITopic<Collection> topic = hazelcastInstance.getTopic(topicName);
                topic.publish(lastResults);
                logger.info("Search in '" + topicName + "' : '" + request.searchString + "' brokered with " + lastResults.size() + " results");
            } else {
                logger.info("Search in '" + topicName + "' : '" + request.searchString + "' returned no results ");
            }
        }

    }

    public Collection<SearchResult> getLastResults() {
        return lastResults;
    }

    public void setSearchService(SearchServiceInterface searchService) {
        this.searchService = searchService;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
