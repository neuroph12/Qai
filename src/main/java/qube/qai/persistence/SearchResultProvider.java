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
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;

public class SearchResultProvider<T> implements QaiDataProvider<T>, HazelcastInstanceAware {

    @Inject
    private HazelcastInstance hazelcastInstance;

    private T data;

    private SearchResult result;

    public SearchResultProvider(SearchResult result) {
        this.result = result;
    }

    public SearchResultProvider(HazelcastInstance hazelcastInstance, SearchResult result) {
        this.hazelcastInstance = hazelcastInstance;
        this.result = result;
    }

    @Override
    public void setContext(String context) {
        this.result.setContext(context);
    }

    @Override
    public T getData() {

        if (data == null) {
            IMap<String, T> map = hazelcastInstance.getMap(result.getContext());
            data = map.get(result.getUuid());
        }

        return data;
    }

    @Override
    public T getData(String uuid) {

        if (data == null) {
            IMap<String, T> map = hazelcastInstance.getMap(result.getContext());
            data = map.get(uuid);
        }

        return data;
    }

    @Override
    public T brokerSearchResult(SearchResult result) {
        IMap<String, T> map = hazelcastInstance.getMap(result.getContext());
        return map.get(result.getUuid());
    }

    @Override
    public void putData(String uuid, T data) {
        IMap<String, T> map = hazelcastInstance.getMap(result.getContext());
        if (map.containsKey(uuid)) {
            map.replace(uuid, data);
        } else {
            map.put(uuid, data);
        }
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
