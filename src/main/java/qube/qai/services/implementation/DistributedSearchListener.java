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
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by rainbird on 1/6/16.
 */
public class DistributedSearchListener implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger("DistributedSearchListener");

    @Inject
    private HazelcastInstance hazelcastInstance;

    private SearchServiceInterface searchService;

    private String topicName;

    public DistributedSearchListener(String topicName) {
        this.topicName = topicName;
    }

    public void initialize() {

        if (hazelcastInstance == null || searchService == null) {
            throw new RuntimeException("Hazelcast instance or the search service to be employed are not configured right- exiting");
        }

        ITopic topic = hazelcastInstance.getTopic(topicName);
        topic.addMessageListener(this);
        logger.info("Registered listener for '" + topicName + "' search-service");
    }

    @Override
    public void onMessage(Message message) {

        Object mesasgeObject = message.getMessageObject();

        if (mesasgeObject instanceof DistributedSearchService.SearchRequest) {
            DistributedSearchService.SearchRequest request = (DistributedSearchService.SearchRequest) mesasgeObject;
            Collection<SearchResult> results = searchService.searchInputString(request.searchString, request.fieldName, request.hitsPerPage);
            if (results != null && !results.isEmpty()) {
                ITopic<Collection> topic = hazelcastInstance.getTopic(topicName);
                topic.publish(results);
                logger.info("Search in '" + topicName + "' : '" + request.searchString + "' brokered with " + results.size() + " results");
            } else {
                logger.info("Search in '" + topicName + "' : '" + request.searchString + "' returned no results ");
            }
        }

    }

    public void setSearchService(SearchServiceInterface searchService) {
        this.searchService = searchService;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
