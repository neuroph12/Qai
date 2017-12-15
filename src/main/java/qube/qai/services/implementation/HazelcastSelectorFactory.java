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

package qube.qai.services.implementation;

import com.hazelcast.core.HazelcastInstance;
import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.HazelcastSelectionOperator;
import qube.qai.services.SelectorFactoryInterface;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/30/15.
 */
public class HazelcastSelectorFactory<T> implements SelectorFactoryInterface {

    @Inject //@Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    public SelectionOperator buildSelector(String dataSource, String uuid, Object data) {
        SelectionOperator<T> selectionOperator = new HazelcastSelectionOperator<T>(hazelcastInstance, dataSource, uuid);
        return selectionOperator;
    }
}
