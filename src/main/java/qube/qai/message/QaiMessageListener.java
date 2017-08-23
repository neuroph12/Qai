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

package qube.qai.message;

import com.hazelcast.core.Message;

/**
 * Created by rainbird on 11/27/15.
 */
public abstract class QaiMessageListener implements com.hazelcast.core.MessageListener {

    public static String procedureTopicName = "Procedures";

    private String uuid;

    public QaiMessageListener() {
    }

    public QaiMessageListener(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    public abstract void onMessage(Message message);

    public abstract void initialize();
}
