package qube.qai.parsers;

import junit.framework.TestCase;
import org.codehaus.jparsec.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by rainbird on 1/29/16.
 */
public class TestOntologyIntegration extends TestCase {

    private String owlFile = "/media/rainbird/ALEPH/wiki-archives/dbpedia_2014.owl";
    private String propertiesFile = "/media/rainbird/ALEPH/wiki-archives/mappingbased_properties_en.ttl";
    private String personsFile = "/media/rainbird/ALEPH/wiki-archives/persondata_en.ttl";

    /**
     * here we want to see how apache Jena works and how useful
     * the thing really is. our model and data comes from DBPedia
     * which is the normalized form of Wikipedia really, but in this
     * form it helps to classify the things
     */
    public void restRdfPerson() throws Exception {
//        Model model = ModelFactory.createDefaultModel();
//
//        File modelFile = new File(personsFile);
//        assertTrue("model file could not be found: " + personsFile, modelFile.exists());
//
//        InputStream modelStream = new FileInputStream(modelFile);
//        model.read(modelStream, null);
//
        Parser urlParser;


    }

    /**
     * this is in order to see how declaring the ttl-files as HsqlDb text-tables
     * and how the performance in that case would be.if the performance is acceptable
     * this would be the easiest way for integrating the whole Dbpedia in the application-
     * including queries really...
     * @throws Exception
     */
    public void testDirectIntegration() throws Exception {
        String createTable = "CREATE TEXT TABLE PersonData (Subject String, Predicate String, Object String)";
        String assignFile = "SET TABLE PersonData SOURCE \"/media/rainbird/ALEPH/wiki-archives/persondata_en.ttl;fs=\\space\" [DESC]";

    }

    private void log(String message) {
        System.out.println(message);
    }
}
