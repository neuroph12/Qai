package qube.qai.persistence.mapstores;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.TestCase;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.RDFId;
import qube.qai.persistence.RDFTriple;

/**
 * Created by rainbird on 5/24/16.
 */
public class TestRdfTripleFileMapStore extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("TestRdfTripleFileMapStore");

    public void restDbPersonRdfTripleMapStore() throws Exception {


    }

    public void testDbPediaRdfTripleMapStore() throws Exception {

        Injector injector = Guice.createInjector(new JpaPersistModule("DBPEDIA"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        RdfTripleFileMapStore mapStore = new RdfTripleFileMapStore();
        mapStore.setRdfTurtleFile("dbpedia_en.ttl");
        injector.injectMembers(mapStore);

        mapStore.createFileTable();

        assertTrue("attaching file failed- an exception must have occurred ", true);

        // pick a few things to ask for
        RDFId id = new RDFId("<http://dbpedia.org/resource/Autism>", "<http://dbpedia.org/ontology/medlineplus>");
        RDFTriple triple = mapStore.load(id);
        assertNotNull("there has to be an associated triple", triple);
        assertTrue("this is the result found in there", "\"001526\"".equals(triple.getObject()));
    }
}
