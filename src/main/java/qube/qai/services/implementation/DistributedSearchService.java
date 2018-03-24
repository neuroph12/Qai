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
import qube.qai.services.DistributedSearchServiceInterface;
import qube.qai.services.SearchResultSink;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by rainbird on 1/6/16.
 */
public class DistributedSearchService extends QaiMessageListener implements DistributedSearchServiceInterface {

    private Logger logger = LoggerFactory.getLogger("DistributedSearchService");

    private String context;

    @Inject
    protected HazelcastInstance hazelcastInstance;

    protected Collection<SearchResult> results;

    protected int maxTryNumber = 10;

    protected boolean interrupt = false;

    protected boolean initialized = false;

    private SearchResultSink resultSink;

    public DistributedSearchService(String searchTopicName, HazelcastInstance hazelcastInstance) {
        this.context = searchTopicName;
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void initialize() {

        logger.info("Initializing DistributedSearchService: " + context);

        ITopic topic = hazelcastInstance.getTopic(context);
        topic.addMessageListener(this);

        initialized = true;
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        if (!initialized) {
            initialize();
        }

        results = null;

        ITopic<SearchRequest> topic = hazelcastInstance.getTopic(context);
        SearchRequest request = new SearchRequest(searchString, fieldName, hitsPerPage);
        topic.publish(request);

        /*int count = 0;
        try {
            while (results == null) {
                Thread.sleep(100);
                count++;
                if (count >= maxTryNumber || interrupt) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted with exception: " + e.getMessage());
        }*/

        return results;
    }

    @Override
    public void searchInputString(SearchResultSink searchSink, String searchString, String fieldName, int hitsPerPage) {

        if (!initialized) {
            initialize();
        }

        // first set the results to null
        resultSink = searchSink;

        ITopic<SearchRequest> topic = hazelcastInstance.getTopic(context);
        SearchRequest request = new SearchRequest(searchString, fieldName, hitsPerPage);
        topic.publish(request);

    }

    @Override
    public void onMessage(Message message) {
        Object messageObject = message.getMessageObject();
        if (messageObject instanceof Collection) {
            results = (Collection<SearchResult>) messageObject;
            if (resultSink != null) {
                resultSink.delayedResults(results);
                String logMessage = String.format("'%s' delivered search-results to'%s'", getContext(), resultSink.getName());
                logger.info(logMessage);
            }
        }
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public int getMaxTryNumber() {
        return maxTryNumber;
    }

    public void setMaxTryNumber(int maxTryNumber) {
        this.maxTryNumber = maxTryNumber;
    }

    public synchronized boolean isInterrupt() {
        return interrupt;
    }

    public synchronized void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    @Override
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setResultSink(SearchResultSink resultSink) {
        this.resultSink = resultSink;
    }

    static public class SearchResponse implements Serializable {

        public Collection<SearchResult> results;

        public SearchResponse(Collection<SearchResult> results) {
            this.results = results;
        }

        public Collection<SearchResult> getResults() {
            return results;
        }

        public void setResults(Collection<SearchResult> results) {
            this.results = results;
        }
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
