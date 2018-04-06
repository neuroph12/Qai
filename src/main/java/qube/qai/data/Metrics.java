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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by zenpunk on 12/4/15.
 */
public class Metrics implements MetricTyped {

    private Map<String, Object> metrics;

    public Metrics() {
        metrics = new TreeMap<String, Object>();
    }

    public Set<String> getNames() {
        return metrics.keySet();
    }

    public Object getValue(String name) {
        return metrics.get(name);
    }

    public void putValue(String name, double value) {
        metrics.put(name, value);
    }

    public void putValue(String name, Object value) {
        metrics.put(name, value);
    }

    public Metrics buildMetrics() {
        return this;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        for (String name : metrics.keySet()) {
            buffer.append("[");
            buffer.append(name).append(": ");
            Object value = metrics.get(name);
            buffer.append(value).append("]");
        }

        return buffer.toString();
    }
}
