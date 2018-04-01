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
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

public class SearchResultCollectionProvider<T extends Collection> implements QaiDataProvider<T> {

    private T values;

    private Collection<SearchResult> searchResults;

    private String context;

    @Inject
    private HazelcastInstance hazelcastInstance;

    public SearchResultCollectionProvider(String context, Collection<SearchResult> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
        this.values = (T) new ArrayList();
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public T getData() {

        if (values.isEmpty()) {
            IMap imap = hazelcastInstance.getMap(context);
            for (SearchResult searchResult : searchResults) {
                Object value = imap.get(searchResult.getUuid());
                values.add(value);
            }
        }

        return values;
    }

    /**
     * as this class is meant only for picking collection results....
     *
     * @param uuid
     * @return
     */
    @Override
    public T getData(String uuid) {
        return null;
    }

    @Override
    public T brokerSearchResult(SearchResult result) {
        return getData();
    }

    @Override
    public void putData(String uuid, T data) {

        IMap<String, T> imap = hazelcastInstance.getMap(context);

        if (!imap.containsKey(uuid)) {
            imap.put(uuid, data);
        } else {
            imap.replace(uuid, data);
        }
    }
}
