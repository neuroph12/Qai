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

package qube.qai.data.selectors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import qube.qai.data.SelectionOperator;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/19/15.
 */
public class HazelcastSelectionOperator<T> implements SelectionOperator, HazelcastInstanceAware {

    private String dataSource;

    private String uuid;

    private Object idObject;

    @Inject //@Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    public HazelcastSelectionOperator(HazelcastInstance hazelcastInstance, String dataSource, String uuid) {
        this.dataSource = dataSource;
        this.uuid = uuid;
        this.hazelcastInstance = hazelcastInstance;
    }

    public HazelcastSelectionOperator(String dataSource, Object idObject) {
        this.dataSource = dataSource;
        this.idObject = idObject;
        this.hazelcastInstance = hazelcastInstance;
    }

    public HazelcastSelectionOperator(String dataSource, String uuid) {
        this.dataSource = dataSource;
        this.uuid = uuid;
    }

    public T getData() {

        IMap<String, T> map = hazelcastInstance.getMap(dataSource);

        if (uuid != null) {
            return map.get(uuid);
        } else {
            return map.get(idObject);
        }
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getIdObject() {
        return idObject;
    }

    public void setIdObject(Object idObject) {
        this.idObject = idObject;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
