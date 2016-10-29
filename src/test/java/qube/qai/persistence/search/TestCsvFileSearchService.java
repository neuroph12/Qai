package qube.qai.persistence.search;

import org.apache.jena.propertytable.graph.GraphCSV;
import org.apache.jena.rdf.model.*;
import junit.framework.TestCase;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.propertytable.lang.CSV2RDF;

/**
 * Created by rainbird on 10/21/16.
 */
public class TestCsvFileSearchService extends TestCase {

    private String pathToCsvFiles = "/media/rainbird/ALEPH/datasets/";
    private String sNp500File = "S&P_500_constituents_financials.csv";

    @Override
    protected void setUp() throws Exception {
        CSV2RDF.init();
    }

    public void restCsvFileModel() throws Exception {

        String filename = pathToCsvFiles + sNp500File;
        Model csvModel = ModelFactory.createModelForGraph(new GraphCSV(filename));
        log("model size: " + csvModel.size());
//        csvModel.write(System.out);

        int count = 0;

        // this way you can read the data row-by-row
        Property rowProperty = csvModel.getProperty("http://w3c/future-csv-vocab/row");
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

    public void testCsvFileSparql() throws Exception {

    }

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

    private void log(String message) {
        System.out.println(message);
    }
}
