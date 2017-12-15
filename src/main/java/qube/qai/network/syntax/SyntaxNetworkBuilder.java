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

package qube.qai.network.syntax;

import info.bliki.wiki.model.WikiModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.SelectionOperator;
import qube.qai.network.Graph;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.persistence.WikiArticle;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rainbird on 12/24/15.
 */
public class SyntaxNetworkBuilder implements NetworkBuilder {

    private static Logger logger = LoggerFactory.getLogger("SyntaxNetworkBuilder");

    private String opennlp_module_en = "/opennlp/en/en-token.bin";

    public Network buildNetwork(SelectionOperator selectionOperator) {

        WikiArticle wikiArticle = (WikiArticle) selectionOperator.getData();
        Graph graph = new Graph();
        Network network = new SyntaxNetwork();
        String wikiContent = wikiArticle.getContent();
        //WikiModel wikiModel = new WikiModel("${image}", "${title}");
        WikiModel wikiModel =
                new WikiModel("./VAADIN/tmp/${image}",
                        "./VAADIN/tmp/${title}");
        String plainText = null;
        try {
            plainText = wikiModel.render("This is a simple [[Hello World]] wiki tag");
        } catch (IOException e) {
            logger.error("Exception during wiki-to-text conversion", e);
            return null;
        }
        //String plainText = WikiModel.toHtml(wikiContent);
        Tokenizer tokenizer = createTokenizer();

        // i think, we will have to run two passes- one for creating graph
        // and the second for creating the adjacency matrix
        int added = 0;
        int increments = 0;
        String[] tokens = tokenizer.tokenize(plainText);
        for (int i = 0; i < tokens.length; i++) {
            String current = tokens[i];
            String next = null;
            if (i + 1 < tokens.length) {
                next = tokens[i + 1];
            }

            // now we have current and next tokens
            // check if they are valid
            if (!isValidToken(current)) {
                continue;
            }

            // check if the token already has been added
            Network.Vertex currentVertex = new Network.Vertex(current);
            if (!graph.containsVertex(currentVertex)) {
                graph.addVertex(currentVertex);
            }

            // now the second token, if not valid we are done
            if (!isValidToken(next)) {
                continue;
            }

            // now check if the token has already been added
            Network.Vertex nextVertex = new Network.Vertex(next);
            if (!graph.containsVertex(nextVertex)) {
                graph.addVertex(nextVertex);
            }

            // now see if there is already an edge between them
            Network.Edge edge = new Network.Edge(currentVertex, nextVertex);
            if (graph.containsEdge(edge)) {
                int index = graph.e2i(edge);
                Network.Edge e = graph.i2e(index);
                e.incrementWeight();
                increments++;
            } else {
                graph.addDirectedSimpleEdge(currentVertex, edge, nextVertex);
                added++;
            }
        }

        // and now the second pass- we create the adjacency matrix
//        int size = graph.getVertices().size();
//        int edgeCount = 0;
//        adjacencyMatrix = new Matrix(size, size);
//        for (Network.Edge edge : graph.getEdges()) {
//            int fromIndex = graph.v2i(edge.getFrom());
//            int toIndex = graph.v2i(edge.getTo());
//            double weight = edge.getWeight();
//            if (Double.isNaN(weight)
//                    || Double.isInfinite(weight)
//                    || weight == 0) {
//                weight = 1.0;
//            }
//            adjacencyMatrix.setValueAt(fromIndex, toIndex, weight);
//            edgeCount++;
//        }
//
        String message = "building semantic network ended #vertex: " + added + " #edges in adjacency matrix: " + increments
                + " added edges: " + added + " incremented edges: " + increments;
        logger.debug(message);
//        // record the changes, and we are done
//        record(graph);

        network.setGraph(graph);
        return network;
    }

    private boolean isValidToken(String token) {
        boolean valid = true;

        if (StringUtils.isBlank(token)) {
            valid = false;
        } else if (StringUtils.containsAny(token, '{', '}', '(', ')')) {
            valid = false;
        } else if (!StringUtils.isAsciiPrintable(token)) {
            valid = false;
        }

        return valid;
    }

    private Tokenizer createTokenizer() {

        Tokenizer tokenizer = null;
        try {
            InputStream modelIn = getClass().getResourceAsStream(opennlp_module_en);
            TokenizerModel tokenizerModel = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(tokenizerModel);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize tokenizer, resource: 'en-token.bin' is missing.");
        }

        return tokenizer;
    }
}
