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
import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.model.WikiModel;
import org.apache.commons.lang3.StringUtils;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class SemanticNetworkBuilder extends Procedure implements NetworkBuilder {

    private boolean debug = true;

    private String[] titles;

    @Inject
    @Named("Wiktionary_en")
    private SearchServiceInterface wikipediaSearchService;

    @Inject
    private QaiDataProvider<WikiArticle> dataProvider;

    private SemanticNetwork network;

    public SemanticNetworkBuilder() {
        super("WikiNetworkBuilder");
        this.titles = new String[0];
    }

    /**
     * This network builder, creates the semantic network of a given article based on
     * the index data and only using lucene-queries about the article
     *
     * @param input
     * @return
     * @TODO this builder is to be implemented as soon as the lucene-upgrade to the latest version is done
     */
    @Override
    public Network buildNetwork(QaiDataProvider... input) {

        addInputs(input);

        execute();

        return network;
    }

    private WikiModel createModel(WikiArticle wikiArticle) {
        WikiModel wikiModel = new WikiModel("${image}", "${title}");

        try {
            StringBuilder bufferOut = new StringBuilder();
            WikiModel.toText(wikiModel, new HTMLConverter(), wikiArticle.getContent(), bufferOut, false, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wikiModel;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

    @Override
    public void execute() {

        if (getInputs() == null || getInputs().isEmpty()) {
            info("No inputs- terminating execution.");
            return;
        }

        network = new SemanticNetwork();

        WikiArticle wikiArticle = (WikiArticle) getInputs().iterator().next().getData();

        String wikiTitle = wikiArticle.getTitle();
        addTitle(wikiTitle);
        Network.Vertex baseVertex = new Network.Vertex(wikiTitle);
        network.addVertex(baseVertex);

        // this is more or less equivalent to parsing the thing
        WikiModel wikiModel = createModel(wikiArticle);
        WikiArticle article = wikiArticle;

        // those are just for debugging
        ArrayList<String> existing = new ArrayList<String>();
        ArrayList<String> addedEdges = new ArrayList<String>();
        int count = 1;
        for (Network.Vertex vertex : network.getVertices()) {

            log("_________________________________________________________________________________");
            String title = vertex.getName();
            // so that we skip the first round
            if (article == null && wikiModel == null) {
                try {
                    String filename = title + ".xml";
                    article = dataProvider.getData();
                    wikiModel = createModel(article);
                } catch (Exception e) {
                    log("Exception while loading: '" + title + "', removing from graph");
                    // remove the vertex
                    network.removeVertex(vertex);
                    continue;
                }
            }

            log("loading edges of: '" + title + "' vertex " + count
                    + " of " + network.getNumberOfVertices()
                    + " and " + network.getNumberOfEdges() + " edges");
            Collection<String> links = wikiModel.getLinks();
            int skipCount = 0;
            int linkCount = 0;
            for (String link : links) {
                if (StringUtils.isBlank(link)) {
                    //log("empty entry- skipping");
                    skipCount++;
                    continue;
                }

                Network.Vertex linkTo = new Network.Vertex(link);
                //log("vertex: '" + title + "' adding edge to: '" + link + "'");
                if (!network.containsVertex(linkTo)) {
                    network.addVertex(linkTo);
                    addedEdges.add(link);
                }

                existing.add(link);
                Network.Edge edge = new Network.Edge(vertex, linkTo);
                if (!network.getEdges().contains(edge)) {
                    network.addSimpleEdge(vertex, edge, linkTo);
                }

                linkCount++;

            }
            log("added " + linkCount + " edges to '" + title + "' with " + skipCount + " skips.");
            log("added edges to: " + existing.toString());
            log("added vertices: " + addedEdges.toString());
            // don't forget to set the article and model to null so that it can be retrieved at the start of the loop
            article = null;
            wikiModel = null;
            addedEdges.clear();
            existing.clear();
            count++;
        }
    }

    private void addTitle(String title) {

        ArrayList<String> tmpTitles = new ArrayList<>();
        for (String t : titles) {
            tmpTitles.add(t);
        }

        tmpTitles.add(title);
        titles = new String[tmpTitles.size()];
        tmpTitles.toArray(titles);


    }

    @Override
    public Procedure createInstance() {
        return new SemanticNetworkBuilder();
    }

    @Override
    protected void buildArguments() {

    }
}
