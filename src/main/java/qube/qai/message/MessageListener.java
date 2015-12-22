package qube.qai.message;

import com.hazelcast.core.Message;

/**
 * Created by rainbird on 11/27/15.
 */
public abstract class MessageListener implements com.hazelcast.core.MessageListener {

    private String uuid;

    public MessageListener() {
    }

    public String getUUID() {
        return uuid;
    }

    public MessageListener(String uuid) {
        this.uuid = uuid;
    }

    public abstract void onMessage(Message message);
}
