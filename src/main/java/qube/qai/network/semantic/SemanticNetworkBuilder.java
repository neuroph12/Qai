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

package qube.qai.network.semantic;

import qube.qai.data.SelectionOperator;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;

public class SemanticNetworkBuilder implements NetworkBuilder {

    /**
     * This network builder, creates the semantic network of a given article based on
     * the index data and only using lucene-queries about the article
     *
     * @param source
     * @return
     * @TODO this builder is to be implemented as soon as the lucene-upgrade to the latest version is done
     */
    @Override
    public Network buildNetwork(SelectionOperator source) {

        Network target = new SemanticNetwork();

        return target;
    }
}
