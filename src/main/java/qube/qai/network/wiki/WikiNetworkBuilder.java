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

import org.openrdf.annotations.Iri;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.procedure.Procedure;

import static qube.qai.main.QaiConstants.BASE_URL;

@Iri(BASE_URL + "WikiNetworkBuilder")
public class WikiNetworkBuilder extends Procedure implements NetworkBuilder {

    public String NAME = "Wiki-Network Builder";

    public WikiNetworkBuilder() {
        super("Wiki-Network Builder");
    }

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

    @Override
    public Network buildNetwork(QaiDataProvider... input) {
        return null;
    }
}
