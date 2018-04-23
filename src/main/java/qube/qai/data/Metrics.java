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

package qube.qai.data;

import org.openrdf.annotations.Iri;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static qube.qai.main.QaiConstants.BASE_URL;

/**
 * Created by zenpunk on 12/4/15.
 */
@Iri(BASE_URL + "map")
public class Metrics implements MetricTyped {

    @Iri(BASE_URL + "map")
    private Map<String, Object> map;

    public Metrics() {
        map = new TreeMap<String, Object>();
    }

    public Set<String> getNames() {
        return map.keySet();
    }

    public Object getValue(String name) {
        return map.get(name);
    }

    public void putValue(String name, double value) {
        map.put(name, value);
    }

    public void putValue(String name, Object value) {
        map.put(name, value);
    }

    public Metrics buildMetrics() {
        return this;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        for (String name : map.keySet()) {
            buffer.append("[");
            buffer.append(name).append(": ");
            Object value = map.get(name);
            buffer.append(value).append("]");
        }

        return buffer.toString();
    }
}
