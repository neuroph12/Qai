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

package qube.qai.persistence;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import qube.qai.services.implementation.SearchResult;

/**
 * Created by rainbird on 6/28/17.
 */
public class MapDataProvider implements DataProvider {

    private String context;

    private HazelcastInstance hazelcastInstance;

    public MapDataProvider(String context) {
        this.context = context;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public Object getData(String uuid) {
        IMap map = hazelcastInstance.getMap(context);
        return map.get(uuid);
    }

    @Override
    public Object brokerSearchResult(SearchResult result) {
        if (!context.equals(result.getContext())) {
            context = result.getContext();
        }
        return getData(result.getUuid());
    }
}
