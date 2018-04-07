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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import info.bliki.wiki.dump.WikiArticle;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;

import javax.inject.Inject;

public class WikiNetworkBuilderTest extends QaiTestBase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    public void testWikiNetworkBuilder() throws Exception {

        WikiNetworkBuilder builder = new WikiNetworkBuilder();

        String filename = "Mouse.xml";
        IMap<String, WikiArticle> wikiMap = hazelcastInstance.getMap(WIKIPEDIA);
        QaiDataProvider<WikiArticle> provider = new DataProvider(wikiMap);
        WikiNetwork wikiNetwork = (WikiNetwork) builder.buildNetwork(provider);
        assertNotNull("duh!", wikiNetwork);

    }
}
