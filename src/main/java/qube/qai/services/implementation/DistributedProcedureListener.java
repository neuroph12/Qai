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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.message.QaiMessageListener;
import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureRunnerInterface;

import javax.inject.Inject;
import java.util.Set;

public class DistributedProcedureListener extends QaiMessageListener implements ProcedureRunnerInterface {

    private Logger logger = LoggerFactory.getLogger("DistributedProcedureListener");

    @Inject
    HazelcastInstance hazelcastInstance;

    @Inject
    ProcedureRunnerInterface procedureRunner;

    private boolean initialized = false;


    public DistributedProcedureListener() {
    }

    public DistributedProcedureListener(HazelcastInstance hazelcastInstance, ProcedureRunnerInterface procedureRunner) {
        this.hazelcastInstance = hazelcastInstance;
        this.procedureRunner = procedureRunner;
    }

    @Override
    public void initialize() {

        logger.info("Initializing DistributedProcedureListener: " + procedureTopicName);

        ITopic topic = hazelcastInstance.getTopic(procedureTopicName);
        topic.addMessageListener(this);

        initialized = true;
    }

    @Override
    public void onMessage(Message message) {


    }

    @Override
    public void submitProcedure(Procedure procedure) {
        procedureRunner.submitProcedure(procedure);
    }

    @Override
    public STATE queryState(String uuid) {
        return procedureRunner.queryState(uuid);
    }

    @Override
    public Set<String> getStartedProcedures() {
        return procedureRunner.getStartedProcedures();
    }
}
