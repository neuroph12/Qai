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
import org.apache.jena.propertytable.graph.GraphCSV;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import qube.qai.services.implementation.UUIDService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rainbird on 10/21/16.
 */
public class TestCsvFileSearchService extends TestCase {

    private String pathToCsvFiles = "/media/rainbird/ALEPH/datasets/";
    private String sNp500File = "S&P_500_constituents_financials.csv";
    private String otherListedFile = "other-listed.csv";
    private String nasdaqFile = "nasdaq-listed.csv";
    private String nyseFile = "nyse-listed.csv";
    private String namespace = "file:///media/rainbird/ALEPH/datasets/S&P_500_constituents_financials.csv";
    private String rowPropertyName = "http://w3c/future-csv-vocab/row";

    @Override
    protected void setUp() throws Exception {
        //CSV2RDF.init();
    }

    public void restUUIDNumbers() {
        for (int i = 0; i < 100; i++) {
            String uuid = UUIDService.uuidString();
            log(uuid);
        }
    }

//    public void testCsvFileSearchService() throws Exception {
//        SearchServiceInterface searchService = new CsvFileSearchService();
//        Collection<SearchResult> results = searchService.searchInputString("something", null, 100);
//        assertNotNull("there must be some results", results);
//        assertTrue("there must be results", !results.isEmpty());
//    }

    public void testCsvFileSparqlAndSelectors() throws Exception {

        String filename = pathToCsvFiles + sNp500File; //otherListedFile; //nyseFile ;
        Model csvModel = RDFDataMgr.loadModel(filename);

        // see if this works
        //csvModel.write(System.out, "RDF/XML-ABBREV");

        // first we query the model in oder to see the
        // field names which come with the rows and rows start with 1...
        int count = 1;
        boolean done = false;

        while (!done) {

            Property property = csvModel.createProperty(rowPropertyName);
            Literal countLiteral = csvModel.createTypedLiteral(count);
            ResIterator resIt = csvModel.listSubjectsWithProperty(property, countLiteral);

            // if there is nothing in the iterator to iterate upon, we simply break
            if (resIt == null || !resIt.hasNext()) {
                log("stopping because res-iterator at end");
                done = true;
            } else {

                while (resIt.hasNext()) {
                    Resource resource = resIt.next();
                    log("-> row (" + count + ")");
                    // read the properties of the resource
                    StmtIterator stmtIt = resource.listProperties();
                    while (stmtIt.hasNext()) {
                        Statement statement = stmtIt.next();
                        log(statement.getPredicate().getLocalName() + ": " + statement.getObject().asLiteral().getValue().toString());
                    }
                    log("============================================================================");

                }
                // now increment the row-number to pick
                count++;
            }
        }
        // the count might seem strange, but this the 1 we started with
        log("printed " + (count - 1) + " rows from csv-file");
    }

    public void restCsvFileModel() throws Exception {

        String filename = pathToCsvFiles + sNp500File;
        Model csvModel = ModelFactory.createModelForGraph(new GraphCSV(filename));
        log("model size: " + csvModel.size());
//        csvModel.write(System.out);

        int count = 0;

        // this way you can read the data row-by-row
        Property rowProperty = csvModel.getProperty(rowPropertyName);
        log("listing resources with property: " + rowProperty);
        for (ResIterator resIt = csvModel.listSubjectsWithProperty(rowProperty); resIt.hasNext(); count++) {
            Resource resource = resIt.next();
            log("item: " + count + " row number: " + resource.getProperty(rowProperty).getObject().asLiteral().getValue());
            for (StmtIterator stmtIt = resource.listProperties(); stmtIt.hasNext(); ) {
                Statement stmt = stmtIt.next();
                String predicate = stmt.getPredicate().getLocalName().toString();
                if ("row".equals(predicate) || "Filings".equals(predicate)) {
                    continue;
                }
                log("property: '" + predicate + "' -> '" + stmt.getObject().asLiteral().getValue() + "'");
            }
            log("=================================================================");
        }
    }

    public void restCsvFileSparql() throws Exception {
        String filename = pathToCsvFiles + sNp500File;
        Model csvModel = RDFDataMgr.loadModel(filename);

        // first try to construct a sparql-query which would read the row-properties
        String rowPropertiesQuery = "PREFIX w3c: <http://w3c/future-csv-vocab/>" +
                "SELECT ?p ?o" +
                "WHERE {" +
                "?s w3c:row 1;" +
                "?p ?o ." +
                "}";
        int count = 0;
        Collection<String> modelNames = new ArrayList<>();
        Query rowQuery = QueryFactory.create(rowPropertiesQuery);
        QueryExecution queryExecution = QueryExecutionFactory.create(rowQuery, csvModel);
        ResultSet resultSet = queryExecution.execSelect();
        while (resultSet.hasNext()) {
            log("names of row- result number: " + count);
            QuerySolution solution = resultSet.next();

            for (Iterator<String> it = solution.varNames(); it.hasNext(); ) {
                String name = it.next();
                RDFNode node = solution.get(name);
                String value = "";
                if (node.isLiteral()) {
                    value = node.asLiteral().getValue().toString();
                } else if (node.isResource()) {
                    value = node.asResource().getLocalName();
                } else {
                    value = node.toString();
                }
                log("variable: " + name + " value: " + value);
                modelNames.add(value);
            }
            count++;
        }

        // after reading the row-properties we can display those rows in their values
        String dataQuery = "PREFIX w3c: <http://w3c/future-csv-vocab/>" +
                "SELECT ?o" +
                "WHERE {" +
                "?s w3c:row %1$d;" +
                "?p ?o ." +
                "}";
        int rowNumber = 1;

        while (true) {
            String query = String.format(dataQuery, rowNumber);
            queryExecution = QueryExecutionFactory.create(query, csvModel);
            ResultSet dataResults = queryExecution.execSelect();

            // check if the query has actually returned something
            if (dataResults == null || !dataResults.hasNext()) {
                break;
            }

            // this is a stupid loop, because we would in fact always be getting one row at a time
            while (dataResults.hasNext()) {
                QuerySolution solution = dataResults.next();
                for (String name : modelNames) {
                    String value = "'" + solution.getLiteral(name).getValue() + "'";
                    log("on row: " + rowNumber + " property: '" + name + "' with value: '" + value + "'");
                }
            }
            // so that we move on to reading the next row
            rowNumber++;
        }
    }

    // the experiment below is no longer relevant- too primitive to work with
    /*
    public void restCsvFileModels() throws Exception {
        String filename = pathToCsvFiles + sNp500File;
        Model csvModel = RDFDataMgr.loadModel(filename);

        int count = 0;

        for (NsIterator it = csvModel.listNameSpaces(); it.hasNext(); ) {
            String name = it.next();
            log("names: " + count + " "+ name);
        }

        String namespace = "file:///media/rainbird/ALEPH/datasets/S&P_500_constituents_financials.csv";
        Property pName = csvModel.getProperty(namespace, "#Name");
        Property pSymbol = csvModel.getProperty(namespace, "#Symbol");
        Property pSector = csvModel.getProperty(namespace, "#Sector");
        Property pPrice = csvModel.getProperty(namespace, "#Price");
        Property pDividend = csvModel.getProperty(namespace, "#Dividend Yield");
        Property pPriceEarnings = csvModel.getProperty(namespace, "#Price/Earnings");
        Property pEarningsShare = csvModel.getProperty(namespace, "#Earnings/Share");
        Property pBookValue = csvModel.getProperty(namespace, "#Book Value");
        Property p52WeekLow = csvModel.getProperty(namespace, "#52 week low");
        Property p52WeekHigh = csvModel.getProperty(namespace, "#52 week high");
        Property pMarketCap = csvModel.getProperty(namespace, "#Market Cap");
        Property pPriceSales = csvModel.getProperty(namespace, "#Price/Sales");
        Property pPriceBook = csvModel.getProperty(namespace, "#Price/Book");
        Property pSECFilings = csvModel.getProperty(namespace, "#SEC Filings");

        log("listing resources with property: " + pName);
        for (ResIterator it = csvModel.listSubjectsWithProperty(pName); it.hasNext(); count++) {
            Resource resource = it.next();
            log("node " + count + " '" + resource.toString() + "'");
            log("Name: '"  + resource.getProperty(pName).getObject().toString() + "'");
            log("Symbol: '" + resource.getProperty(pSymbol).getObject().toString() + "'");
            log("Sector: '" + resource.getProperty(pSector).getObject().toString() + "'");
            log("Price: '" + resource.getProperty(pPrice).getObject().toString() + "'");
//            log("Dividend: '" + resource.getProperty(pDividend).getObject().toString() + "'");
//            log("Price/Earnings: '" + resource.getProperty(pPriceEarnings).getObject().toString() + "'");
//            log("Earnings/Share: '" + resource.getProperty(pEarningsShare).getObject().toString() + "'");
//            log("Book value: '" + resource.getProperty(pBookValue).getObject().toString() + "'");
//            log("52 week low: '" + resource.getProperty(p52WeekLow).getObject().toString() + "'");
//            log("52 week high: '" + resource.getProperty(p52WeekHigh).getObject().toString() + "'");
//            log("Market Cap: '" + resource.getProperty(pMarketCap).getObject().toString() + "'");
//            log("Price/Sales: '" + resource.getProperty(pPriceSales).getObject().toString() + "'");
//            log("Price/Book: '" + resource.getProperty(pPriceBook).getObject().toString() + "'");
//            log("SEC Filings: '" + resource.getProperty(pSECFilings).getObject().toString() + "'");
            log("=================================================================");
        }
    }
    */

    private void log(String message) {
        System.out.println(message);
    }
}
