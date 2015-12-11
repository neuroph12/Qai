package qube.qai.network.semantic;

import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.commons.lang3.StringUtils;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import qube.qai.matrix.Matrix;
import qube.qai.network.Network;
import qube.qai.persistence.WikiArticle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rainbird on 11/22/15.
 */
public class SemanticNetwork extends Network {

    private boolean debug = false;

    private Tokenizer tokenizer = null;

    /**
     * the routine which does the actual construction
     * of the network, in fact this time, we can actually
     * work directly with the adjacency matrix and build
     * the network using that- i think, that would be
     * a much easier way to work the problem
     * @param wikiArticle
     */
    public void buildNetwork(WikiArticle wikiArticle) {
        // this unfortunately does not work
        //this = Network.createTestNetwork();
        String wikiContent = wikiArticle.getContent();
        //HashSet<String> wordList = new HashSet<String>();

        WikiModel wikiModel = new WikiModel("${image}", "${title}");
        String plainText = wikiModel.render(new PlainTextConverter(), wikiContent);
        Tokenizer tokenizer = createTokenizer();
        // i think, we will have to run two passes- one for creating the workd list
        // and the second for creating the adjacency matrix
        String[] tokens = tokenizer.tokenize(plainText);

        // first loop to create the vertices
        for (String token : tokens) {
            Vertex vertex = new Vertex(token);
            if (!getVertices().contains(vertex) && isValidToken(token)) {
                log("Adding token: '" + token + "' to word list");
                addVertex(vertex);
            }
        }

        int size = getVertices().size();
        log("final graph will have: " + size + " words");
        // create the adjacency-matrix
        // decided against using complex-matrices for semantic-networks
        // doesn't really make sense in this case
        int wordCount = 0;
        int linkCount = 0;
        PrimitiveDenseStore matrixStore = PrimitiveDenseStore.FACTORY.makeZero(size, size);
        for (int i = 0; i < tokens.length; i++) {

            String token = tokens[i];

            // skip if this is an illegal token
            if (!isValidToken(token)) {
                continue;
            }

            int nextIndex = i + 1;
            wordCount++;
            String nextToken = null;
            if (nextIndex < tokens.length) {
                nextToken = tokens[nextIndex];
            }

            // create the vertices we need to search for
            Vertex currentVertex = new Vertex(token);
            int currentTokenIndex = v2i(currentVertex);

            Vertex nextVertex = new Vertex(nextToken);
            // check if the next token is in graph,
            // if not simply add edge to self
            if (!getVertices().contains(nextVertex)) {
                // the next token is not in word-list
                // so we add the edge to the vertex itself
                Number value = matrixStore.get(currentTokenIndex, currentTokenIndex);
                matrixStore.set(currentTokenIndex, currentTokenIndex, value.doubleValue() + 1);
            } else {
                int nextTokenIndex = v2i(nextVertex);
                Number value = matrixStore.get(currentTokenIndex, nextTokenIndex);
                matrixStore.set(currentTokenIndex, nextTokenIndex, value.doubleValue() + 1);
                linkCount++;
            }
        }

        log("added all edges to adjacency-matrix total " + wordCount + " words and " + linkCount + " links connecting");

        // we have to normalize the matrix content
//        double normalization = 1 / wordCount;
//        matrixStore.multiply(normalization);
        matrixStore.signum();

        Access2D.Builder<PrimitiveMatrix> builder = PrimitiveMatrix.FACTORY.getBuilder(matrixStore.getRowDim(), matrixStore.getColDim());
        for (int i = 0; i < matrixStore.getRowDim(); i++) {
            for (int j = 0; j < matrixStore.getColDim(); j++) {
                builder.set(i, j, matrixStore.get(i, j));
            }
        }
        // @TODO make a new matrix
        PrimitiveMatrix matrix = builder.build();
        this.adjacencyMatrix = new Matrix(matrix);

        buildFromAdjacencyMatrix();
    }

    private boolean isValidToken(String token) {
        boolean valid = true;

        if (StringUtils.isBlank(token)) {
            valid = false;
        } else if (StringUtils.contains(token, "{")) {
            valid = false;
        } else if (StringUtils.contains(token, "}")) {
            valid = false;
        } else if (StringUtils.contains(token, "(")) {
            valid = false;
        } else if (StringUtils.contains(token, ")")) {
            valid = false;
        }

        return valid;
    }

    private Tokenizer createTokenizer() {

        // rather than having the try-block in if curls...
        if (tokenizer != null) {
            return tokenizer;
        }

        try {
            InputStream modelIn = new FileInputStream("/home/rainbird/projects/work/qai/src/main/resources/opennlp/en-token.bin");
            TokenizerModel tokenizerModel = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(tokenizerModel);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize tokenizer, resource: 'en-token.bin' is missing.");
        }

        return tokenizer;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
