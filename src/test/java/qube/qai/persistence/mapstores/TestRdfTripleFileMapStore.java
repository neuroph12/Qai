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
        injector.injectMembers(mapStore);

        fail("test not yet implemented ");
    }

//    public void testDbPediaRdfTripleMapStore() throws Exception {
//
//        this.injector = injector.createChildInjector(new JpaPersistModule("DBPEDIA"));
//        PersistService service = injector.getInstance(PersistService.class);
//        service.start();
//
//        RdfTripleFileMapStore mapStore = new RdfTripleFileMapStore();
//        injector.injectMembers(mapStore);
//
//        fail("test not yet implemented ");
//    }
}
