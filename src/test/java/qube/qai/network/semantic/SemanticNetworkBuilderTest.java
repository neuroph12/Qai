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

package qube.qai.network.semantic;

import com.google.inject.name.Named;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import grph.oo.ObjectGrph;
import grph.oo.ObjectPath;
import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.DataSelectionOperator;
import qube.qai.main.QaiTestBase;
import qube.qai.network.Network;
import qube.qai.persistence.DummyQaiDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

public class SemanticNetworkBuilderTest extends QaiTestBase {

    public void testSemanticNetwork() throws Exception {

        Collection<SearchResult> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next().getUuid();
        log("name for the test case: " + filename);

        IMap<String, WikiArticle> wikiMap = hazelcastInstance.getMap(WIKIPEDIA);
        WikiArticle wikiArticle = wikiMap.get(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        SemanticNetworkBuilder builder = new SemanticNetworkBuilder();
        SelectionOperator wikiProvider = new DataSelectionOperator(wikiArticle);
        SemanticNetwork network = (SemanticNetwork) builder.buildNetwork(wikiProvider);
        assertNotNull(network);

        fail("for the time being there is no implementation for the whole- regard this as a TODO message");
    }

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    @Named("Wiktionary_en")
    private SearchServiceInterface wikipediaSearchService;

    private QaiDataProvider wikiDataProvider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        wikiDataProvider = new DummyQaiDataProvider("Wikipedia_en", new WikiArticle());
    }

    /**
     * not a test which you want to do very often
     * creating network took: 786628 ms
     * Network experiment number of vertices: 185842
     * Network experiment number of edges: 783178
     * Network experiment average degree: 8.428428449973634
     *
     * @throws Exception
     */
    public void testWikiNetwork() throws Exception {

        Collection<SearchResult> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next().getUuid();
        log("name for the test case: " + filename);
        IMap<String, WikiArticle> wikiMap = hazelcastInstance.getMap(WIKIPEDIA);
        WikiArticle wikiArticle = wikiMap.get(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        // now feed it to wiki-network class and build a network
        SemanticNetworkBuilder builder = new SemanticNetworkBuilder();
        SelectionOperator wikiProvider = new DataSelectionOperator(wikiArticle);
        //injector.injectMembers(network);

        long start = System.currentTimeMillis();
        log("started network-building process...");
        SemanticNetwork network = (SemanticNetwork) builder.buildNetwork(wikiProvider);
        //network.buildNetwork(wikiArticle);
        long duration = System.currentTimeMillis() - start;
        log("creating network took: " + duration + " ms");
        logNetwork(network);
    }

    private void logNetwork(Network network) {
        log("Network number of vertices: " + network.getNumberOfVertices());
        log("Network number of edges: " + network.getNumberOfEdges());
        log("Network average degree: " + network.getAverageDegree());
        //log("Network clustering coefficient: " + network.getClusteringCoefficient());
        log("Network density: " + network.getDensity());
        //log("Network diameter: " + network.getDiameter());
    }

    public void testGRPHNetwork() {
        // this line is recommended by the authors of graph-library
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        ObjectGrph graph = new ObjectGrph<String, String>();
        //IntSet vertices = graph.getVertices();
        String[] vertices = {"paris", "london", "vienna", "luxemburg", "varsaw",};

        for (String vertex : vertices) {
            graph.addVertex(vertex);
        }

        graph.addSimpleEdge("paris", "paris-vienna", "vienna", false);
        graph.addSimpleEdge("london", "london-vienna", "vienna", false);
        graph.addSimpleEdge("paris", "paris-luxemburg", "luxemburg", false);
        graph.addSimpleEdge("paris", "paris-varsaw", "varsaw", false);
        graph.addSimpleEdge("vienna", "vienna-luxemburg", "luxemburg", false);
        graph.addSimpleEdge("vienna", "vienna-varsaw", "varsaw", false);
        graph.addSimpleEdge("varsaw", "varsaw-luxemburg", "luxemburg", false);

        Collection<String> viennaEdges = graph.getIncidentEdges("vienna");
        log("vienna's edges:");
        for (String edge : viennaEdges) {
            log(edge);
        }

        Set<ObjectPath> allPaths = graph.getAllPaths();
        for (ObjectPath path : allPaths) {
            log("path: " + path.toString());
        }

    }
}
