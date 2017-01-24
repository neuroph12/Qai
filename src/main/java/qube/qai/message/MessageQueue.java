package qube.qai.message;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 11/27/15.
 */
public class MessageQueue implements MessageQueueInterface {

    private static Logger logger = LoggerFactory.getLogger("MessageQueue");

    @Inject //@Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    private Set<String> topics = new HashSet<String>();

    public MessageQueue() {
    }

    @Inject
    public MessageQueue(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void sendMessage(String topic, String message) {
        sendMessage(topic, message, null);
    }

    public void sendMessage(String topic, String message, String signal) {

        ITopic itopic = hazelcastInstance.getTopic(topic);
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
        if (signal != null) itopic.publish(signal);
        itopic.publish(message);
    }

    public void addListener(String topic, MessageListener listener) {
        createTopic(topic);
        ITopic itopic = hazelcastInstance.getTopic(topic);
        if (itopic != null) {
            itopic.addMessageListener(listener);
        } else {
            logger.error("no topic for the topic?!?");
        }

    }

    public void createTopic(String topic) {
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
    }

    public Set<String> getTopics() {
        return topics;
    }

}
