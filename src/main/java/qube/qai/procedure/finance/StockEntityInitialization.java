/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

package qube.qai.procedure.finance;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.nodes.ValueNode;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Set;

/**
 * Created by rainbird on 1/21/17.
 */
public class StockEntityInitialization extends Procedure implements ProcedureConstants {

    protected Logger logger = LoggerFactory.getLogger("StockEntityInitialization");

    public static String NAME = "Stock Entity Initialization Procedure";

    public static String DESCRIPTION = "Rips wiki-based archives to individual files which are easier to parse and to read";

    public static String INPUT_FILENAME = "FILENAME";

    public static String GROUP_NAME = "NAME_OF_THE_STOCK_GROUP";

    public static String GROUP = "GROUP";

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

    private String groupName;

    private int numberOfRecords = 0;

    private int numberOfRecordsCreated = 0;

    public boolean addQuotes = false;

    //private StockGroup group;

    @Inject
    private EntityManager entityManager;

    public StockEntityInitialization() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<String>(INPUT_FILENAME) {
            @Override
            public void setValue(String value) {
                super.setValue(value);
                selectedFile = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<String>(GROUP_NAME) {
            @Override
            public void setValue(String value) {
                super.setValue(value);
                groupName = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Number>(NUMBER_OF_RECORDS, MIMETYPE_NUMBER) {
            @Override
            public Number getValue() {
                return numberOfRecords;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Number>(NUMBER_OF_RECORDS_CREATED, MIMETYPE_NUMBER) {
            @Override
            public Number getValue() {
                return numberOfRecordsCreated;
            }
        });
    }

    @Override
    public void execute() {

        //pathToCsvFiles = (String) getInputValueOf(INPUT_FILENAME);

        createCheckAndInsertStockEntitesFromFile();

        // after all is done and said, these are the results we are expected to record
        //setResultValueOf(GROUP_NAME, groupName);
        //setResultValueOf(NUMBER_OF_RECORDS_CREATED, numberOfRecordsCreated);
        hasExecuted = true;
    }

    public void createCheckAndInsertStockEntitesFromFile() {

        String filename = pathToCsvFiles + selectedFile; //otherListedFile; //nyseFile ;
        Model csvModel = RDFDataMgr.loadModel(filename);
        StockQuoteDataStore store = new StockQuoteDataStore();

        // begin the transaction in the database
//        entityManager.getTransaction().begin();
        StockGroup group = new StockGroup();
        group.setName(groupName);

        // field names which come with the rows and rows start with 1...
        int count = 1;
        boolean done = false;
        Property property = csvModel.createProperty(rowPropertyName);

        // start looping over the rows
        while (!done) {

            Literal countLiteral = csvModel.createTypedLiteral(count);
            ResIterator resIt = csvModel.listSubjectsWithProperty(property, countLiteral);

            // if there is nothing in the iterator to iterate upon, we simply break
            if (!resIt.hasNext()) {
                debug("stopping because res-iterator at end");
                done = true;
            } else {

                // loop over the row-properties and create the entity
                while (resIt.hasNext()) {

                    Resource resource = resIt.next();
                    info("-> row (" + count + ")");
                    StockEntity stockEntity = ripEntityFromRow(resource);

                    if (isaValidEntity(stockEntity)) {

                        StockEntity databaseCopy = findStockEntityDatabaseCopy(stockEntity);

                        if (databaseCopy == null) {

                            entityManager.persist(stockEntity);
                            info("persisted stock-entity: '" + stockEntity.getName() + "' symbol: '" + stockEntity.getTickerSymbol() + "'");

                            if (addQuotes) {
                                Set<StockQuote> quotes = store.retrieveQuotesFor(stockEntity.getTickerSymbol());
                                for (StockQuote quote : quotes) {
                                    entityManager.persist(quote);
                                }
                                stockEntity.setQuotes(quotes);
                                info("persisted quotes for entity: '" + stockEntity.getName() + "' symbol: '" + stockEntity.getTickerSymbol() + "' #quotes: " + quotes.size());
                            }

                            group.addStockEntity(stockEntity);

                        } else {

                            group.addStockEntity(databaseCopy);
                        }
                        numberOfRecordsCreated++;
                    }
                }
                // now increment the row-number to pick
                count++;
            }
        }

        // now save the group we have just created
        try {
            entityManager.persist(group);
            // write the rest of data- this writes the entries in relation-table
            // and commit the transaction
//            entityManager.flush();
//            entityManager.getTransaction().commit();
            info("persisted group: '" + group.getName() + "' with: '" + group.getEntities().size() + " entities");
            // now we are done and can change the flag
            numberOfRecords = count - 1;
            info("read " + (count - 1) + " rows from csv-file");
        } catch (Exception e) {
            error("StockEntityInitializationProcedure interrupted with exception", e);
//            if (entityManager.getTransaction().isActive()) {
//                entityManager.close();
//            }
        }
    }

    public StockEntity findStockEntityDatabaseCopy(StockEntity entity) {
        String queryString = "select c from StockEntity c where c.tickerSymbol like :tickerSymbol and c.name like :name";
        String query = String.format(queryString, entity.getTickerSymbol(), entity.getName());
        StockEntity foundEntity = null;
        try {
            foundEntity = entityManager.createQuery(query, StockEntity.class).
                    setParameter("tickerSymbol", entity.getTickerSymbol()).
                    setParameter("name", entity.getName()).
                    getSingleResult();
            info("query for: " + entity.getTickerSymbol() + " '" + entity.getName() + "' resulted with " + foundEntity.getUuid());
        } catch (Exception e) {
            info("query for: " + entity.getTickerSymbol() + " '" + entity.getName() + "' resulted nothing");
        }
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
            if (StringUtils.isNotBlank(value)) {
                value = value.trim();
            }

            info(name + ": " + value);

            // now assign the values to the entity
            if ("Sector".equals(name)) {
                entity.setGicsSector(value);
            } else if ("Symbol".equals(name)) {
                entity.setTickerSymbol(value);
            } else if ("Filings".equals(name)) {
                entity.setCIK(value);
            } else if ("Name".equals(name) || "Company Name".equals(name)) {
                entity.setName(value);
            } else if ("Yield".equals(name)) {
                entity.setYield(statement.getObject().asLiteral().getDouble());
            } else if ("Cap".equals(name)) {
                entity.setCapital(statement.getObject().asLiteral().getDouble());
            } else if ("high".equals(name)) {
                entity.setWeeklyHigh(statement.getObject().asLiteral().getDouble());
            } else if ("Book".equals(name)) {
                entity.setBookingValue(statement.getObject().asLiteral().getDouble());
            } else if ("row".equals(name)) {
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


    public String getCompleteNameSpace() {
        return namespace + selectedFile;
    }

    public static String getPathToCsvFiles() {
        return pathToCsvFiles;
    }

    public static void setPathToCsvFiles(String pathToCsvFiles) {
        StockEntityInitialization.pathToCsvFiles = pathToCsvFiles;
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
        StockEntityInitialization.namespace = namespace;
    }

    public String getRowPropertyName() {
        return rowPropertyName;
    }

    public void setRowPropertyName(String rowPropertyName) {
        this.rowPropertyName = rowPropertyName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public int getNumberOfRecordsCreated() {
        return numberOfRecordsCreated;
    }

    public void setNumberOfRecordsCreated(int numberOfRecordsCreated) {
        this.numberOfRecordsCreated = numberOfRecordsCreated;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
