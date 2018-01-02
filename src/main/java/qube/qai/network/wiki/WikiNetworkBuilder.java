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

package qube.qai.network.wiki;

import qube.qai.data.SelectionOperator;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.procedure.Procedure;

public class WikiNetworkBuilder extends Procedure implements NetworkBuilder {


    @Override
    public void execute() {

    }

    @Override
    public Procedure createInstance() {
        return new WikiNetworkBuilder();
    }

    @Override
    protected void buildArguments() {

    }

    public WikiNetworkBuilder() {
    }

    @Override
    public Network buildNetwork(SelectionOperator source) {
        return null;
    }
}
