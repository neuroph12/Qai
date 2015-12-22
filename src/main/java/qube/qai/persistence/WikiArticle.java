package qube.qai.persistence;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by rainbird on 11/3/15.
 */
public class WikiArticle implements Serializable {

    private String id;

    private String source;

    private String title;

    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
