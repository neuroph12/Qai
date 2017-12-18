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

import qube.qai.services.implementation.SearchResult;

/**
 * Created by rainbird on 6/28/17.
 */
public class DummyQaiDataProvider<T> implements QaiDataProvider<T> {

    private String context;

    private T data;

    public DummyQaiDataProvider() {
    }

    public DummyQaiDataProvider(T data) {
        this.data = data;
    }

    public DummyQaiDataProvider(String context, T data) {
        this.context = context;
        this.data = data;
    }

    public String getContext() {

        return context;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public void putData(String uuid, T data) {

    }

    @Override
    public T brokerSearchResult(SearchResult result) {
        return null;
    }
}
