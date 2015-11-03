package qube.qai.persistence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by rainbird on 11/3/15.
 */
@XmlRootElement(name="WikiArticle")
public class WikiArticle {

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
}
