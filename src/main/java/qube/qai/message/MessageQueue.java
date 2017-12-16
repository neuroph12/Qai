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

package qube.qai.message;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 11/27/15.
 */
public class MessageQueue implements MessageQueueInterface {

    @Inject
    private Logger logger;

    @Inject
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
