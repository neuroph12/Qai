package qube.qai.network.wiki;

import com.google.inject.name.Named;
import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.model.WikiModel;
import org.apache.commons.lang3.StringUtils;
import qube.qai.network.Network;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by rainbird on 11/21/15.
 */
public class WikiNetwork extends Network {

    private boolean debug = true;

    private Vector<String> titles;

    @Inject
    @Named("Wiktionary_en")
    private SearchServiceInterface wikipediaSearchService;

    public WikiNetwork() {
        titles = new Vector<String>();
    }

    public void buildNetwork(WikiArticle wikiArticle) {

        String wikiTitle = wikiArticle.getTitle();
        titles.add(wikiTitle);
        Vertex baseVertex = new Vertex(wikiTitle);
        addVertex(baseVertex);

        // this is more or less equivalent to parsing the thing
        WikiModel wikiModel = createModel(wikiArticle);
        WikiArticle article = wikiArticle;

        // those are just for debugging
        ArrayList<String> existing = new ArrayList<String>();
        ArrayList<String> addedEdges = new ArrayList<String>();
        int count = 1;
        for (Network.Vertex vertex : getVertices()) {

            log("_________________________________________________________________________________");
            String title = vertex.getName();
            // so that we skip the first round
            if (article == null && wikiModel == null) {
                try {
                    String filename = title + ".xml";
                    article = wikipediaSearchService.retrieveDocumentContentFromZipFile(filename);
                    wikiModel = createModel(article);
                } catch (Exception e) {
                    log("Exception while loading: '" + title + "', removing from graph");
                    // remove the vertex
                    removeVertex(vertex);
                    continue;
                }
            }

            log("loading edges of: '" + title + "' vertex " + count + " of " + getNumberOfVertices() + " and " + getNumberOfEdges() + " edges");
            Collection<String> links = wikiModel.getLinks();
            int skipCount = 0;
            int linkCount = 0;
            for (String link : links) {
                if (StringUtils.isBlank(link)) {
                    //log("empty entry- skipping");
                    skipCount++;
                    continue;
                }

                Vertex linkTo = new Vertex(link);
                //log("vertex: '" + title + "' adding edge to: '" + link + "'");
                if (!containsVertex(linkTo)) {
                    addVertex(linkTo);
                    addedEdges.add(link);
                }

                existing.add(link);
                Edge edge = new Edge(vertex, linkTo);
                if (!getEdges().contains(edge)) {
                    addSimpleEdge(vertex, edge, linkTo);
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
}
