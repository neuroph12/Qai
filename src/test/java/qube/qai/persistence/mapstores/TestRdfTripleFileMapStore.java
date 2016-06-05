package qube.qai.persistence.mapstores;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.uwyn.jhighlight.tools.StringUtils;
import junit.framework.TestCase;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import org.openrdf.model.Statement;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.RDFId;
import qube.qai.persistence.RDFTriple;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 5/24/16.
 */
public class TestRdfTripleFileMapStore extends TestCase {

    private EntityManager manager;

    private Injector injector;

    protected Logger logger = LoggerFactory.getLogger("TestRdfTripleFileMapStore");

    private String[][] rdfids = {{"http://dbpedia.org/resource/Aristotle", "http://dbpedia.org/ontology/region"},
            {"http://dbpedia.org/resource/Autism", "http://dbpedia.org/ontology/diseasesdb"},
            {"http://dbpedia.org/resource/Autism", "http://dbpedia.org/ontology/emedicineTopic"},
            {"http://dbpedia.org/resource/Aristotle", "http://dbpedia.org/ontology/deathYear"}};

    @Override
    protected void setUp() throws Exception {
        injector = Guice.createInjector(new JpaPersistModule("TEST_DBPEDIA"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        manager = injector.getInstance(EntityManager.class);

    }

    public void bestSomethingElse() throws Exception {

        Query query = manager.createQuery("SELECT t FROM RDFTriple t", RDFTriple.class);
        List<RDFTriple> triples = query.getResultList();

        for (RDFTriple triple : triples) {
            log("found: " + triple.getSubject() + " " + triple.getPredicate() + " " + triple.getObject());
        }
    }

    public void bestForInsertingTurtle() throws Exception {

        manager.getTransaction().begin();

        RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
        RDFHandler handler = new DummyRDFHandler();
        parser.setRDFHandler(handler);

        File turtleFile = new File("/media/rainbird/ALEPH/qai-persistence.db/dbpedia_en/dummy.ttl");
        InputStream stream = new FileInputStream(turtleFile);


        long start = System.currentTimeMillis();
        parser.parse(stream, turtleFile.getName());

        // when all is done, we can commit the entered data
        manager.getTransaction().commit();
        long duration = System.currentTimeMillis() - start;

        log("written: " + ((DummyRDFHandler)handler).getCount() + " records in " + duration + "ms");

    }

    class DummyRDFHandler implements RDFHandler {

            public long count = 0;

            @Override
            public void startRDF() throws RDFHandlerException {

            }

            @Override
            public void endRDF() throws RDFHandlerException {

            }

            @Override
            public void handleNamespace(String s, String s1) throws RDFHandlerException {
                //log("namespace: " + s + " " + s1);
            }

            @Override
            public void handleStatement(Statement statement) throws RDFHandlerException {

                RDFTriple triple = new RDFTriple(statement.getSubject().stringValue(), statement.getPredicate().getLocalName(), statement.getObject().stringValue());
                manager.persist(triple);
                count++;
            }

            @Override
            public void handleComment(String s) throws RDFHandlerException {
                //System.out.println("comment: " + s);
            }

            public long getCount() {
                return count;
            }
    }

    public void testDbPediaRdfTripleMapStore() throws Exception {

        final RdfTripleFileMapStore mapStore = new RdfTripleFileMapStore();
        injector.injectMembers(mapStore);

        // now we try something else...
        // we will parse the data using rio
        final List<String> uuidList = new ArrayList<String>();
        RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
        parser.setRDFHandler(new RDFHandler() {
            @Override
            public void startRDF() throws RDFHandlerException {

            }

            @Override
            public void endRDF() throws RDFHandlerException {

            }

            @Override
            public void handleNamespace(String s, String s1) throws RDFHandlerException {
                log("namespace: " + s + " " + s1);
            }

            @Override
            public void handleStatement(Statement statement) throws RDFHandlerException {

                RDFTriple triple = new RDFTriple(statement.getSubject().stringValue(), statement.getPredicate().getLocalName(), statement.getObject().stringValue());
                mapStore.store(triple.getUuid(), triple);
                log("UUID: " + triple.getUuid() + " statement: " + statement);
                uuidList.add(triple.getUuid());
            }

            @Override
            public void handleComment(String s) throws RDFHandlerException {
                System.out.println("comment: " + s);
            }
        });

        File turtleFile = new File("/media/rainbird/ALEPH/qai-persistence.db/dbpedia_en/dummy.ttl");
        InputStream stream = new FileInputStream(turtleFile);

        parser.parse(stream, turtleFile.getName());

        int count = 0;
        Iterable<String> idList = mapStore.loadAllKeys();
        for (String id : idList) {
            RDFTriple triple = mapStore.load(id);
            if (triple == null) {
                continue;
            }
            count++;
        //    assertNotNull(triple);

        }
        log("retrieved " + count + " ids from mapstore");

        // now try with the id's we have collected
        for (String uuid : uuidList) {
            RDFTriple triple = mapStore.load(uuid);
            assertNotNull(triple);
            log("retrieved: " + triple.getUuid());
        }

        Map<String, RDFTriple> allTriples = mapStore.loadAll(uuidList);
        assertNotNull(allTriples);
        assertTrue(allTriples.size() == uuidList.size());

    }

    private void log(String message) {
        System.out.println(message);
    }
}
