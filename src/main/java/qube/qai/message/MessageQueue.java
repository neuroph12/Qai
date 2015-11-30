package qube.qai.message;

import com.hazelcast.core.*;
import com.hazelcast.core.MessageListener;
import qube.qai.procedure.Procedure;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 11/27/15.
 */
public class MessageQueue {

    private HazelcastInstance hazelcastInstance;

    private Set<String> topics = new HashSet<String>();

    private static MessageQueue mesasageQueue;

    public static MessageQueue getInstance() {
        if (mesasageQueue != null) {
            return mesasageQueue;
        }

        mesasageQueue = new MessageQueue();
        return mesasageQueue;
    }

    public void sendMessage(String topic, String message) {
        if (!topics.contains(topic)) {

        }
        ITopic itopic = hazelcastInstance.getTopic(topic);
        itopic.addMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                Object messageObject = message.getMessageObject();
            }
        });
    }

    public void addListener(qube.qai.message.MessageListener listener) {
        if (!topics.contains(listener.getUUID())) {
            topics.add(listener.getUUID());
        }
        hazelcastInstance.getTopic(listener.getUUID()).addMessageListener(listener);

    }

    public void createTopic(String topic) {
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
    }

    public Set<String> getTopics() {
        return topics;
    }

    private MessageQueue() {
    }
}
