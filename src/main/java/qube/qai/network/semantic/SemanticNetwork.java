package qube.qai.network.semantic;

import qube.qai.network.Network;
import qube.qai.persistence.WikiArticle;

/**
 * Created by rainbird on 11/22/15.
 */
public class SemanticNetwork extends Network {

    private boolean debug = true;

    /**
     * the routine which does the actual construction
     * of the network, in fact this time, we can actually
     * work directly with the adjacency matrix and build
     * the network using that- i think, that would be
     * a much easier way to work the problem
     * @param wikiArticle
     */
    public void buildNetwork(WikiArticle wikiArticle) {

    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
