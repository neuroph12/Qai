package qube.qai.network.semantic;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import qube.qai.network.Network;
import qube.qai.persistence.WikiArticle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

/**
 * Created by rainbird on 11/22/15.
 */
public class SemanticNetwork extends Network {

    private boolean debug = true;

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
        HashSet<String> wordList = new HashSet<String>();

        Tokenizer tokenizer = createTokenizer();
        // i think, we will have to run two passes- one for creating the workd list
        // and the second for creating the adjacency matrix
        String[] tokens = tokenizer.tokenize(wikiContent);

        // first loop to

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
