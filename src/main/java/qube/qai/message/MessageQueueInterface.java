package qube.qai.message;

/**
 * Created by rainbird on 12/22/15.
 */
public interface MessageQueueInterface {

    void sendMessage(String topic, String message);

    void addListener(String topic, com.hazelcast.core.MessageListener listener);

}
