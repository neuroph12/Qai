package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.WikiArticle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by rainbird on 12/21/15.
 */
public class WikiArticleMapStore implements MapStore<String, WikiArticle> {

    private static Logger logger = LoggerFactory.getLogger("WikiArticleMapStore");

    public String zipFilename;

    private XStream xStream;

    public WikiArticleMapStore(String zipFilename) {
        this.zipFilename = zipFilename;
        xStream = new XStream();
    }

    public void store(String key, WikiArticle value) {
        // do nothing
    }

    public void storeAll(Map<String, WikiArticle> map) {
        // do nothing
    }

    public void delete(String key) {
        // do nothing
    }

    public void deleteAll(Collection<String> keys) {
        // do nothing
    }

    public WikiArticle load(String key) {
        WikiArticle wikiArticle = null;

        try {
            ZipFile zipFile = new ZipFile(zipFilename);
            ZipEntry zipEntry = zipFile.getEntry(key);
            InputStream stream = zipFile.getInputStream(zipEntry);

            XStream xStream = new XStream();
            wikiArticle = (WikiArticle) xStream.fromXML(stream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wikiArticle;
    }

    public Map<String, WikiArticle> loadAll(Collection<String> keys) {
        Map<String, WikiArticle> all = new HashMap<String, WikiArticle>();
        for (String key : keys) {
            WikiArticle article = load(key);
            all.put(key, article);
        }
        return all;
    }

    public Iterable<String> loadAllKeys() {
        return null;
    }
}
