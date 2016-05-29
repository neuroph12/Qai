package qube.qai.persistence.mapstores;

import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import qube.qai.main.QaiTestBase;

/**
 * Created by rainbird on 5/24/16.
 */
public class TestRdfTripleFileMapStore extends QaiTestBase {

    public void testDbPersonRdfTripleMapStore() throws Exception {

        this.injector = injector.createChildInjector(new JpaPersistModule("DBPERSON"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        RdfTripleFileMapStore mapStore = new RdfTripleFileMapStore();
        mapStore.setRdfTurtleFile("persondata_en.ttl");
        injector.injectMembers(mapStore);

        mapStore.createFileTable();

        assertTrue("attaching file failed- an exception must have occurred ", true);
    }

    public void testDbPediaRdfTripleMapStore() throws Exception {

        this.injector = injector.createChildInjector(new JpaPersistModule("DBPEDIA"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        RdfTripleFileMapStore mapStore = new RdfTripleFileMapStore();
        mapStore.setRdfTurtleFile("dbpedia_en.ttl");
        injector.injectMembers(mapStore);

        mapStore.createFileTable();

        assertTrue("attaching file failed- an exception must have occurred ", true);
    }
}
