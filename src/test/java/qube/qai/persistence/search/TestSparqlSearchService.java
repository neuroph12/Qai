package qube.qai.persistence.search;


import junit.framework.TestCase;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.VCARD;

import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by rainbird on 6/8/16.
 */
public class TestSparqlSearchService extends TestCase {

    Logger logger = Logger.getLogger("TestSparqlSsearchService");

    private String serviceURL = "http://192.168.1.2:8890/sparql";


    public void testRemoteSparql() throws Exception {
        HttpAuthenticator authenticator = new SimpleAuthenticator("dba", "dba".toCharArray());
        try(QueryExecution qe = QueryExecutionFactory.sparqlService(serviceURL,
                "SELECT * WHERE { ?s a ?type }",
                authenticator)) {
            Model model = qe.execConstruct();

        }
    }


    public void restRdfModelAndAll() throws Exception {

        // some definitions
        String personURI = "http://wikipedia.org";
        String givenName = "John";
        String familyName = "Smith";
        String fullName = givenName + " " + familyName;

        // create an empty Model
        Model model = ModelFactory.createDefaultModel();

        // create the resource
        //   and add the properties cascading style
        Resource johnSmith
                = model.createResource(personURI)
                .addProperty(VCARD.FN, fullName)
                .addProperty(VCARD.N,
                        model.createResource()
                                .addProperty(VCARD.Given, givenName)
                                .addProperty(VCARD.Family, familyName));

        logger.info("resource: " + johnSmith.toString());

        // this should be the same
        model.write(System.out);

        for (Iterator<Statement> statements = model.listStatements(); statements.hasNext(); ) {
            Statement statement = statements.next();
            logger.info(statement.toString());
        }

    }

    public void restRdfReadingFile() throws Exception {

        // create an empty model
        Model model = ModelFactory.createDefaultModel();
        String inputFileName = "rdf-test.xml";

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }

        // read the RDF/XML file
        model.read(in, null);

        // write it to standard out
        model.write(System.out);
    }

}