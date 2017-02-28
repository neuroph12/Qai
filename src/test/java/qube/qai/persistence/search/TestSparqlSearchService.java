/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

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
import qube.qai.services.implementation.SearchResult;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by rainbird on 6/8/16.
 */
public class TestSparqlSearchService extends TestCase {

    Logger logger = Logger.getLogger("TestSparqlSsearchService");

    private String serviceURL = "http://192.168.1.2:8890/sparql";

    /**
     * this is the main test which should be running
     * all the others are either to be remved or to be checked
     * as to their relevance- experimental code, i suspect
     *
     * @throws Exception
     */
    public void testSparqlSearchService() throws Exception {

        SparqlSearchService searchService = new SparqlSearchService();
        Collection<SearchResult> results = searchService.searchInputString("*", "uuid", 0);
        assertNotNull("there has to be some results", results);
        assertTrue("there has to be something in the results", !results.isEmpty());

    }

    /**
     * i think this will be removed later on
     *
     * @throws Exception
     */
    public void restRemoteSparql() throws Exception {
        HttpAuthenticator authenticator = new SimpleAuthenticator("dba", "dba".toCharArray());
        try (QueryExecution qe = QueryExecutionFactory.sparqlService(serviceURL,
                "SELECT * WHERE { ?s a ?type }",
                authenticator)) {
            Model model = qe.execConstruct();

        }
    }

    /**
     * right now not in use
     *
     * @throws Exception
     */
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

    /**
     * currently not in use
     *
     * @throws Exception
     */
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
