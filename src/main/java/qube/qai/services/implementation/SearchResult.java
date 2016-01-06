package qube.qai.services.implementation;

import java.io.Serializable;

/**
 * Created by rainbird on 11/27/15.
 */
public class SearchResult implements Serializable {

    private String title;

    private String filename;

    private double relevance;

    public SearchResult() {
    }

    public SearchResult(String title, String filename, double relevance) {
        this.title = title;
        this.filename = filename;
        this.relevance = relevance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
