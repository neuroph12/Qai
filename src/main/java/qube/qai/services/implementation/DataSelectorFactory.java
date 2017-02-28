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

import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.DataSelectionOperator;
import qube.qai.services.SelectorFactoryInterface;

/**
 * Created by rainbird on 11/30/15.
 */
public class DataSelectorFactory<T> implements SelectorFactoryInterface<T> {

    public SelectionOperator<T> buildSelector(String dataSource, String uuid, T data) {
        SelectionOperator<T> selectionOperator = new DataSelectionOperator<T>(data);
        return selectionOperator;
    }

}
