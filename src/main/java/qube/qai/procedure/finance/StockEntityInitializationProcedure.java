package qube.qai.procedure.finance;

import com.hazelcast.core.MapStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import qube.qai.data.Arguments;
import qube.qai.persistence.StockCategory;
import qube.qai.persistence.StockEntity;
import qube.qai.procedure.Procedure;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 * Created by rainbird on 1/21/17.
 */
public class StockEntityInitializationProcedure extends Procedure {

    public static String NAME = "WikiRipperProcedure";

    public static String DESCRIPTION = "Rips wiki-based archives to individual files which are easier to parse and to read";

    public static String INPUT_FILENAME  = "FILENAME";

    public static String CATEGORY_NAME = "NAME_OF_THE_STOCK_CATEGORY";

    public static String NUMBER_OF_RECORDS = "NUMBER_OF_RECORDS";

    public static String NUMBER_OF_RECORDS_CREATED = "NUMBER_OF_RECORDS_CREATED";

    private static String pathToCsvFiles = "/media/rainbird/ALEPH/datasets/";

    private static String namespace = "file:///media/rainbird/ALEPH/datasets/";

    public static String S_AND_P_500_LISTING = "S&P_500_constituents_financials.csv";

    public static String OTHER_LISTED_ENTITIES = "other-listed.csv";

    public static String NASDAQ_LISTING = "nasdaq-listed.csv";

    public static String NYSE_LISTING = "nyse-listed.csv";

    private String rowPropertyName = "http://w3c/future-csv-vocab/row";

    private String selectedFile;

    private String categoryName;

    private int numberOfRecords;

    private int getNumberOfRecordsCreated;

    @Inject
    private EntityManager entityManager;

    @Override
    public void execute() {

        createCheckAndInsertStockEntitesFromFile();

        // after all is done and said, these are the results we are expected to record
        logger.info("adding '" + CATEGORY_NAME + "' to results");
        arguments.addResult(CATEGORY_NAME, categoryName);
        logger.info("adding '" + NUMBER_OF_RECORDS + "' to results");
        arguments.addResult(NUMBER_OF_RECORDS, numberOfRecords);
        logger.info("adding '" + NUMBER_OF_RECORDS_CREATED + "' to results");
        arguments.addResult(NUMBER_OF_RECORDS_CREATED, getNumberOfRecordsCreated);

    }

    public void createCheckAndInsertStockEntitesFromFile() {

        String filename = pathToCsvFiles + selectedFile; //otherListedFile; //nyseFile ;
        Model csvModel = RDFDataMgr.loadModel(filename);

        StockCategory category = new StockCategory();
        category.setName(categoryName);

        // field names which come with the rows and rows start with 1...
        int count = 1;
        boolean done = false;

        while (!done) {

            Property property = csvModel.createProperty(rowPropertyName);
            Literal countLiteral = csvModel.createTypedLiteral(count);
            ResIterator resIt = csvModel.listSubjectsWithProperty(property, countLiteral);

            // if there is nothing in the iterator to iterate upon, we simply break
            if (!resIt.hasNext()) {
                logger.info("stopping because res-iterator at end");
                done = true;
            } else {

                while (resIt.hasNext()) {
                    Resource resource = resIt.next();
                    logger.info("-> row (" + count + ")");
                    StockEntity stockEntity = ripEntityFromRow(resource);
                    if (isaValidEntity(stockEntity)) {
                        StockEntity databaseCopy = findStockEntityDatabaseCopy(stockEntity);
                        if (databaseCopy == null) {
                            category.addStockEntity(stockEntity);
                        } else {
                            category.addStockEntity(databaseCopy);
                        }
                    }
                }
                // now increment the row-number to pick
                count++;
            }

            entityManager.persist(category);

            // now we are done and can change the flag
            done = true;
            numberOfRecords = count-1;
            logger.info("printed " + (count-1) + " rows from csv-file");
        }
    }

    public StockEntity findStockEntityDatabaseCopy(StockEntity entity) {
        String queryString = "select c from StockEntity c where c.tickerSymbol like '%s' and c.name like '%s'";
        String query = String.format(queryString, entity.getTickerSymbol(), entity.getName());
        StockEntity foundEntity = entityManager.createQuery(query, StockEntity.class).getSingleResult();
        return foundEntity;
    }

    public boolean isaValidEntity(StockEntity stockEntity) {
        if (StringUtils.isNotEmpty(stockEntity.getTickerSymbol())
                && StringUtils.isNoneEmpty(stockEntity.getName())) {
            return true;
        }
        return false;
    }

    public StockEntity ripEntityFromRow(Resource rowResource) {
        StockEntity entity = new StockEntity();
        StmtIterator stmtIt = rowResource.listProperties();

        while (stmtIt.hasNext()) {
            Statement statement = stmtIt.next();
            String name = statement.getPredicate().getLocalName();
            String value = statement.getObject().asLiteral().getValue().toString();

            logger.info(name + ": " + value);

            // now assign the values to the entity
            if ("Sector".equals(name)) {
                entity.setGicsSector(value);
            } else if ("Symbol".equals(name)) {
                entity.setTickerSymbol(value);
            } else if ("Filings".equals(name)) {
                entity.setCIK(value);
            } else if ("Name".equals(name)) {
                entity.setName(value);
            } else if ("Yield".equals(name)) {
                entity.setYield(statement.getObject().asLiteral().getDouble());
            } else if ("Cap".equals(name)) {
                entity.setCapital(statement.getObject().asLiteral().getDouble());
            } else if ("high".equals(name)) {
                entity.setWeeklyHigh(statement.getObject().asLiteral().getDouble());
            } else if ("Book".equals(name)) {
                entity.setBookingValue(statement.getObject().asLiteral().getDouble());
            }  else if ("row".equals(name)) {
                // we simply skip this one...
            } else if ("low".equals(name)) {
                entity.setWeeklyLow(statement.getObject().asLiteral().getDouble());
            } else if ("Share".equals(name)) {
                entity.setShare(statement.getObject().asLiteral().getDouble());
            } else if ("Price".equals(name)) {
                entity.setMarketPrice(statement.getObject().asLiteral().getDouble());
            } else if ("Value".equals(name)) {
                entity.setBookingValue(statement.getObject().asLiteral().getDouble());
            } else if ("EBITDA".equals(name)) {
                entity.setEbitda(statement.getObject().asLiteral().getDouble());
            } else if ("Earnings".equals(name)) {
                entity.setEarnings(statement.getObject().asLiteral().getDouble());
            } else if ("Sales".equals(name)) {
                entity.setSales(statement.getObject().asLiteral().getDouble());
            }

        }
        return entity;
    }

    @Override
    public void buildArguments() {
        name = NAME;
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_FILENAME);
        arguments.putResultNames(CATEGORY_NAME, NUMBER_OF_RECORDS, NUMBER_OF_RECORDS_CREATED);
    }

    public String getCompleteNameSpace() {
        return namespace + selectedFile;
    }

    public static String getPathToCsvFiles() {
        return pathToCsvFiles;
    }

    public static void setPathToCsvFiles(String pathToCsvFiles) {
        StockEntityInitializationProcedure.pathToCsvFiles = pathToCsvFiles;
    }

    public String getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(String selectedFile) {
        this.selectedFile = selectedFile;
    }

    public static String getNamespace() {
        return namespace;
    }

    public static void setNamespace(String namespace) {
        StockEntityInitializationProcedure.namespace = namespace;
    }

    public String getRowPropertyName() {
        return rowPropertyName;
    }

    public void setRowPropertyName(String rowPropertyName) {
        this.rowPropertyName = rowPropertyName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public int getGetNumberOfRecordsCreated() {
        return getNumberOfRecordsCreated;
    }

    public void setGetNumberOfRecordsCreated(int getNumberOfRecordsCreated) {
        this.getNumberOfRecordsCreated = getNumberOfRecordsCreated;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
