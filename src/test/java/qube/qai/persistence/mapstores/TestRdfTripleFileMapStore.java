package qube.qai.persistence.mapstores;

import com.google.inject.Injector;
import junit.framework.TestCase;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.openrdf.model.Statement;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.QaiRdfHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by rainbird on 5/24/16.
 * @Deprecated class is to be replaced by DatabaseMapStore
 */
@Deprecated
public class TestRdfTripleFileMapStore extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("TestRdfTripleFileMapStore");

    private Model testModel;
    private Dataset testDataset;
    //private String directory = "/media/rainbird/ALEPH/qai-persistence.db/dbtest_tdb";
    private String directory = "/media/rainbird/ALEPH/qai-persistence.db/dbpedia_en";
    //private String filename = directory + "/dbpedia_en.ttl";
    private String filename = directory + "/dummy.ttl";

    @Override
    protected void setUp() throws Exception {
        testDataset = TDBFactory.createDataset(directory);
        testDataset.begin(ReadWrite.WRITE);
        testModel = testDataset.getDefaultModel();
    }

    @Override
    protected void tearDown() throws Exception {
        testDataset.end();
    }

    public void testForInsertingTurtle() throws Exception {

        RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
        RDFHandler handler = new QaiRdfHandler(testModel);
        parser.setRDFHandler(handler);

        //String filename = directory + "/dummy.ttl";
        File turtleFile = new File(filename);
        InputStream stream = new FileInputStream(turtleFile);


        long start = System.currentTimeMillis();
        log("started parsing at: " + new Date().toString());
        parser.parse(stream, turtleFile.getName());

        // when all is done, we can commit the entered data
        //manager.getTransaction().commit();
        long duration = System.currentTimeMillis() - start;


        log("written: " + ((QaiRdfHandler)handler).getCount() + " records in " + duration + "ms");

    }

//        public void restTDBStore() throws Exception {
//
//        // Make a TDB-backed testDataset
//        //String directory = "/media/rainbird/ALEPH/qai-persistence.db/dbpedia_en";
//        //String directory = "/media/rainbird/ALEPH/qai-persistence.db/dbperson_en";
//
//        Dataset dataset = TDBFactory.createDataset(directory) ;
//
//        dataset.begin(ReadWrite.READ);
//        // Get testModel inside the transaction
//        testModel = dataset.getDefaultModel();
//        dataset.end();
//
//        //
//        dataset.begin(ReadWrite.WRITE);
//        testModel = dataset.getDefaultModel() ;
//        dataset.end();
//    }
//
//
//    class DummyRDFHandler implements RDFHandler {
//
//        public long count = 0;
//        private final Model model;
//        public DummyRDFHandler(Model model) {
//            this.model = model;
//        }
//
//        @Override
//        public void startRDF() throws RDFHandlerException {
//
//        }
//
//        @Override
//        public void endRDF() throws RDFHandlerException {
//
//        }
//
//        @Override
//        public void handleNamespace(String s, String s1) throws RDFHandlerException {
//            //log("namespace: " + s + " " + s1);
//        }
//
//        @Override
//        public void handleStatement(Statement statement) throws RDFHandlerException {
//            //log("entering: " + statement.toString());
//            Resource resource = this.model.createResource(statement.getSubject().stringValue());
//            Property property = this.model.createProperty(statement.getPredicate().toString(), statement.getPredicate().getLocalName());
//            String object = statement.getObject().stringValue();
//            org.apache.jena.rdf.model.Statement stat = model.createStatement(resource, property, object);
//            //queryExecution.execConstruct().add(stat);
//            model.add(stat);
//            count++;
//        }
//
//        @Override
//        public void handleComment(String s) throws RDFHandlerException {
//            //System.out.println("comment: " + s);
//        }
//
//        public long getCount() {
//            return count;
//        }
//    }

//    public void bestDbPediaRdfTripleMapStore() throws Exception {
//
//        final RdfTripleFileMapStore mapStore = new RdfTripleFileMapStore();
//        injector.injectMembers(mapStore);
//
//        // now we try something else...
//        // we will parse the data using rio
//        final List<String> uuidList = new ArrayList<String>();
//
//        RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
//        parser.setRDFHandler(new RDFHandler() {
//            @Override
//            public void startRDF() throws RDFHandlerException {
//
//            }
//
//            @Override
//            public void endRDF() throws RDFHandlerException {
//
//            }
//
//            @Override
//            public void handleNamespace(String s, String s1) throws RDFHandlerException {
//                log("namespace: " + s + " " + s1);
//            }
//
//            @Override
//            public void handleStatement(Statement statement) throws RDFHandlerException {
//                RDFTriple triple = new RDFTriple(statement.getSubject().stringValue(), statement.getPredicate().getLocalName(), statement.getObject().stringValue());
//                mapStore.store(triple.getUuid(), triple);
//                log("UUID: " + triple.getUuid() + " statement: " + statement);
//                uuidList.add(triple.getUuid());
//            }
//
//            @Override
//            public void handleComment(String s) throws RDFHandlerException {
//                System.out.println("comment: " + s);
//            }
//        });
//
//        File turtleFile = new File("/media/rainbird/ALEPH/qai-persistence.db/dbpedia_en/dummy.ttl");
//        InputStream stream = new FileInputStream(turtleFile);
//
//        parser.parse(stream, turtleFile.getName());
//
//        int count = 0;
//        Iterable<String> idList = mapStore.loadAllKeys();
//        for (String id : idList) {
//            RDFTriple triple = mapStore.load(id);
//            if (triple == null) {
//                continue;
//            }
//            count++;
//        //    assertNotNull(triple);
//
//        }
//        log("retrieved " + count + " ids from mapstore");
//
//        // now try with the id's we have collected
//        for (String uuid : uuidList) {
//            RDFTriple triple = mapStore.load(uuid);
//            assertNotNull(triple);
//            log("retrieved: " + triple.getUuid());
//        }
//
//        Map<String, RDFTriple> allTriples = mapStore.loadAll(uuidList);
//        assertNotNull(allTriples);
//        assertTrue(allTriples.size() == uuidList.size());
//
//    }

    private void log(String message) {
        System.out.println(message);
    }
}
