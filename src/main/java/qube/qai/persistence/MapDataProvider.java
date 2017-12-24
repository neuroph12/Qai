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

package qube.qai.persistence;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;

/**
 * Created by rainbird on 6/28/17.
 */
public class MapDataProvider implements QaiDataProvider {

    private String context;

    private String uuid;

    @Inject
    private HazelcastInstance hazelcastInstance;

    public MapDataProvider(HazelcastInstance hazelcastInstance, String context, String uuid) {
        this.hazelcastInstance = hazelcastInstance;
        this.context = context;
        this.uuid = uuid;
    }

    public MapDataProvider(HazelcastInstance hazelcastInstance, String context) {
        this.hazelcastInstance = hazelcastInstance;
        this.context = context;
    }

    public MapDataProvider(String context, String uuid) {
        this.context = context;
        this.uuid = uuid;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public Object getData() {
        if (hazelcastInstance == null
                || StringUtils.isBlank(context)
                || StringUtils.isBlank(uuid)) {
            throw new IllegalArgumentException("Setup is incomplete cannot broker data!");
        }

        return hazelcastInstance.getMap(context).get(uuid);
    }

    @Override
    public Object brokerSearchResult(SearchResult result) {
        if (hazelcastInstance == null
                || StringUtils.isBlank(result.getUuid())
                || StringUtils.isBlank(result.getContext())) {
            throw new IllegalArgumentException("Setup is incomplete cannot broker data!");
        }

        return hazelcastInstance.getMap(result.getContext()).get(result.getUuid());
    }

    public String getContext() {
        return context;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void putData(String uuid, Object data) {
        IMap map = hazelcastInstance.getMap(context);
        if (map.containsKey(uuid)) {
            map.replace(uuid, data);
        } else {
            map.put(uuid, data);
        }
    }
}
