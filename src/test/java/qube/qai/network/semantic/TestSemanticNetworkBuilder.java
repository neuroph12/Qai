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


import com.google.inject.name.Named;
import qube.qai.data.Metrics;
import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.DataSelectionOperator;
import qube.qai.main.QaiTestBase;
import qube.qai.network.Network;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.DummyDataProvider;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by rainbird on 11/24/15.
 */
public class TestSemanticNetworkBuilder extends QaiTestBase {

    @Inject
    @Named("Wiktionary_en")
    private SearchServiceInterface wikipediaSearchService;

    private DataProvider wikiArticleProvider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        wikiArticleProvider = new DummyDataProvider("Wikipedia_en", new WikiArticle());
    }

    /**
     * this is in order to test the semantic network building algorithms
     * we'll see how long it take to build up a semantic network of
     * a given text's content.
     *
     * @throws Exception
     */
    public void testSemanticNetwork() throws Exception {
        Collection<SearchResult> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next().getUuid();
        log("name for the test case: " + filename);
        WikiArticle wikiArticle = (WikiArticle) wikiArticleProvider.getData(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        SemanticNetworkBuilder builder = new SemanticNetworkBuilder();
        SelectionOperator<WikiArticle> selectionOperator = new DataSelectionOperator<WikiArticle>(wikiArticle);
        SemanticNetwork semanticNetwork = (SemanticNetwork) builder.buildNetwork(selectionOperator);

        logNetwork(semanticNetwork);
    }

    /**
     * check that the basic properties of the networks are the same
     */
    public void testAdjacencyMatrix() throws Exception {
        Collection<SearchResult> results = wikipediaSearchService.searchInputString("test", "title", 1);
        assertNotNull("there has to be a result for the search", results);

        String filename = results.iterator().next().getUuid();
        log("name for the test case: " + filename);
        WikiArticle wikiArticle = (WikiArticle) wikiArticleProvider.getData(filename);
        assertNotNull("there has to be a wiki-article", wikiArticle);

        SemanticNetworkBuilder builder = new SemanticNetworkBuilder();
        SelectionOperator<WikiArticle> selectionOperator = new DataSelectionOperator<WikiArticle>(wikiArticle);
        SemanticNetwork semanticNetwork = (SemanticNetwork) builder.buildNetwork(selectionOperator);
        semanticNetwork.buildAdjacencyMatrix();
        logNetwork(semanticNetwork);

        // now we try to build another network from the adjacency matrix
        Network network = new Network();
        network.buildFromAdjacencyMatrix(semanticNetwork.getAdjacencyMatrix());

        Metrics sematicMetrics = semanticNetwork.buildMetrics();
        Metrics copyMetrics = network.buildMetrics();

        double semVertexCount = (Double) sematicMetrics.getValue("number of vertices");
        double copyVertexCount = (Double) copyMetrics.getValue("number of vertices");
        log("vertex counts, original: " + semVertexCount + " copy: " + copyVertexCount);
        assertTrue("number of vertices must be same", semVertexCount == copyVertexCount);

        double semEdgeCount = (Double) sematicMetrics.getValue("number of edges");
        double copyEdgeCount = (Double) copyMetrics.getValue("number of edges");
        log("edge counts, original: " + semEdgeCount + " copy: " + copyEdgeCount);
        assertTrue("number of edges", semEdgeCount == copyEdgeCount);
    }

    private void logNetwork(Network network) {
        log("Network number of vertices: " + network.getNumberOfVertices());
        log("Network number of edges: " + network.getNumberOfEdges());
        //log("Network average degree: " + network.getAverageDegree());
        //log("Network clustering coefficient: " + network.getClusteringCoefficient());
        //log("Network density: " + network.getDensity());
        //log("Network diameter: " + network.getDiameter());
    }

}
