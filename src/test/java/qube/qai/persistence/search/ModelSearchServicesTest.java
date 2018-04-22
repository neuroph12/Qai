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

package qube.qai.persistence.search;

import junit.framework.TestCase;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import qube.qai.persistence.mapstores.DatabaseMapStoresTest;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureLibraryInterface;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.Role;
import qube.qai.user.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static qube.qai.main.QaiConstants.PROCEDURES;
import static qube.qai.main.QaiConstants.USERS;

/**
 * Created by rainbird on 1/20/17.
 */
public class ModelSearchServicesTest extends TestCase {

    private ProcedureLibraryInterface procedureLibrary;

    private String directoryName = "./test/dummy.model.directory";

    private String baseUrl = "http://www.qoan.org";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.procedureLibrary = new ProcedureLibrary();
    }

    public void estProcedureModelStore() throws Exception {

        ModelSearchService modelSearchService = new ModelSearchService(PROCEDURES, directoryName);
        modelSearchService.init();

        Collection<String> uuids = new ArrayList<>();
        Map<Class, ProcedureTemplate> templateMap = procedureLibrary.getTemplateMap();
        for (Class klazz : templateMap.keySet()) {
            ProcedureTemplate template = templateMap.get(klazz);
            Procedure procedure = (Procedure) klazz.newInstance();
            procedure.setNAME(template.getProcedureName());
            modelSearchService.save(klazz, procedure);
            log("saved a copy of '" + template.getProcedureName() + "'");
        }

        for (Class klazz : templateMap.keySet()) {
            ProcedureTemplate template = templateMap.get(klazz);
            log("now searching for " + template.getProcedureName());
            Collection<SearchResult> found = modelSearchService.searchInputString(template.getProcedureName(), PROCEDURES, 10);
            assertNotNull("results may not be null", found);
            assertTrue("there has to be results", !found.isEmpty());
        }

    }

    /**
     * since users will not be saved in rdf-models, this is not really
     * very interesting- so leaving it aside simply.
     *
     * @throws Exception
     */
    public void estUserModelStore() throws Exception {

        ModelSearchService modelSearchService = new ModelSearchService(USERS, directoryName);
        modelSearchService.init();

        Collection<String> uuids = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String userName = DatabaseMapStoresTest.randomWord(10);
            User user = new User(userName, "dummy_password");
            user.createSession().setName("dummy_user_session:_test_session");
            user.addRole(new Role(user, "dummy_role", "this_is_a_test_role_for_the_user"));

            modelSearchService.save(User.class, user);

            String query = userName;
            Collection<SearchResult> results = modelSearchService.searchInputString(query, ModelSearchService.USERS, 1);
            assertNotNull("there must be results", results);
            assertTrue("there has to be the user", !results.isEmpty());
            String foundUuid = results.iterator().next().getUuid();
            assertTrue("uuids must be equal", foundUuid.equals(user.getUuid()));
            modelSearchService.remove(User.class, user);
            uuids.add(user.getUuid());
        }

        // this way we can check that the leftover users are not one of those which we already deleted
        Collection<SearchResult> others = modelSearchService.searchInputString("*", ModelSearchService.USERS, 0);
        for (SearchResult result : others) {
            log("additionally found user with uuid: " + result.getUuid());
            assertTrue("the originals must have been deleted", !uuids.contains(result.getUuid()));
        }
    }

    /*public void testJeanBeanReadWrite() throws Exception {

        Dataset dataset = TDBFactory.createDataset(directoryName);
        dataset.begin(ReadWrite.WRITE);
        Model model = dataset.getNamedModel(baseUrl);
        Bean2RDF writer = new Bean2RDF(model);
        RDF2Bean reader = new RDF2Bean(model);

        Jenabean.instance().bind(model);

        Map<Class, ProcedureTemplate> templateMap =  procedureLibrary.getTemplateMap();
        for (Class klazz : templateMap.keySet()) {
            ProcedureTemplate template = templateMap.get(klazz);
            Procedure procedure = (Procedure) klazz.newInstance();
            procedure.setNAME(template.getProcedureName());
            procedure.setDESCRIPTION(template.getProcedureDescription());
            log("Now saving " + template.getProcedureName());
            writer.save(procedure);
        }


        for (Class klazz : templateMap.keySet()) {
            //Collection<Procedure> procedures = reader.load(klazz);
            ProcedureTemplate template = templateMap.get(klazz);
            String query = "Select ?s WHERE { ?s a <http://" + klazz.getPackage().getName() + "/" + klazz.getSimpleName() + "> }";
            log("Now searching for " + template.getProcedureName() + "with query: " + query);
            Collection<Procedure> procedures = Sparql.exec(model, klazz, query);
            assertNotNull("there has to be a procedure", procedures);
            assertTrue("there has to be some results", !procedures.isEmpty());
        }


        dataset.commit();
        dataset.end();

    }*/

    public void testAliBabaObjectStore() throws Exception {

        // create a repository, for trying the things out
        // a memory-store will be just fine. later move on
        // to some directory solution, so that data can actually be
        // persisted some place as well.
        File dataDir = new File(directoryName);
        //Repository store = new SailRepository(new MemoryStore(dataDir));
        Repository repository = new SailRepository(new MemoryStore(dataDir));
        repository.initialize();

        // wrap in an object repository
        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepository objectRepository = factory.createRepository(repository);

        ObjectConnection connection = objectRepository.getConnection();

        ValueFactory vf = connection.getValueFactory();
        Map<Class, ProcedureTemplate> templateMap = procedureLibrary.getTemplateMap();

        for (Class klazz : templateMap.keySet()) {
            ProcedureTemplate template = templateMap.get(klazz);
            Procedure procedure = (Procedure) klazz.newInstance();
            procedure.setNAME(template.getProcedureName());
            procedure.setDESCRIPTION(template.getProcedureDescription());
            log("Now saving " + template.getProcedureName());
            URI id = vf.createURI("http://www.qoan.org/procedures/" + procedure.getUuid());
            connection.addObject(id, procedure);
        }


    }


    private void log(String message) {
        System.out.println(message);
    }
}
