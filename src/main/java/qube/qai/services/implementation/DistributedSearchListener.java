package qube.qai.services.implementation;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by rainbird on 1/6/16.
 */
public class DistributedSearchListener implements MessageListener {

    @Inject //@Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    private SearchServiceInterface searchService;

    private String topicName; // = "Wikipedia_en";

    public DistributedSearchListener(String topicName) {
        this.topicName = topicName;
    }

    public void initialize() {
        ITopic topic = hazelcastInstance.getTopic(topicName);
        topic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        Object mesasgeObject = message.getMessageObject();
        if (mesasgeObject instanceof DistributedSearchService.SearchRequest) {
            DistributedSearchService.SearchRequest request = (DistributedSearchService.SearchRequest) mesasgeObject;
            Collection<SearchResult> results = searchService.searchInputString(request.searchString, request.fieldName, request.hitsPerPage);
            ITopic<Collection> topic = hazelcastInstance.getTopic(topicName);
            topic.publish(results);
        }
    }

    public void setSearchService(SearchServiceInterface searchService) {
        this.searchService = searchService;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
