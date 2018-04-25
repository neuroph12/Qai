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
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.result.Result;
import org.openrdf.sail.memory.MemoryStore;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.mapstores.DatabaseMapStoresTest;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureLibraryInterface;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.Role;
import qube.qai.user.User;

import java.io.File;
import java.util.*;

import static qube.qai.main.QaiConstants.*;

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

    public void testAliBabaObjectStoreWithProcedures() throws Exception {

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
        Map<String, Procedure> savedProcedures = new HashMap<>();

        for (Class klazz : templateMap.keySet()) {
            ProcedureTemplate template = templateMap.get(klazz);
            Procedure procedure = (Procedure) klazz.newInstance();
            procedure.setNAME(template.getProcedureName());
            procedure.setDESCRIPTION(template.getProcedureDescription());
            log("Now saving " + template.getProcedureName() + " with uuid: '" + procedure.getUuid());
            String uriString = "http://www.qoan.org/data#Procedure##uuid#";
            URI id = vf.createURI(uriString, procedure.getUuid());
            connection.addObject(id, procedure);
            savedProcedures.put(procedure.getUuid(), procedure);
        }

        log("_______________________________so much to writing the values now starting to read them_______________________________");

        for (String uuid : savedProcedures.keySet()) {
            Procedure procedure = savedProcedures.get(uuid);
            String procedureUrl = BASE_URL + "Procedure"; //procedure.getClass().getSimpleName();
            log("Now searching for " + procedure.getProcedureName() + " with query");
            URI uri = vf.createURI(procedureUrl, uuid);
            Object found = connection.getObject(procedure.getClass(), uri);
            assertNotNull("there has to be a procedure", found);
            log("found object: " + found.toString());
            //assertTrue("uuids have to be the same", uuid.equals(procedure.getUuid()));
            try {
                Procedure p = (Procedure) found;
                log("Procedrue: " + p.getNAME() + " and uuid: " + p.getUuid());

            } catch (ClassCastException e) {
                //fail("this should really not happen");
                log("Skipping class cast exception....");
                continue;
            }
        }

        Result<Procedure> procedures = connection.getObjects(Procedure.class);
        assertNotNull("there have to be some results", procedures);
        assertTrue("there have to some procedures", !procedures.asList().isEmpty());
        for (Procedure procedure : procedures.asList()) {
            log("found- " + procedure.getNAME());
        }

        connection.commit();
        connection.close();
        repository.shutDown();
    }

    public void testAlibabaObjectStoreWithUsers() throws Exception {

        File dataDir = new File(directoryName);
        //Repository store = new SailRepository(new MemoryStore(dataDir));
        Repository repository = new SailRepository(new MemoryStore(dataDir));
        repository.initialize();

        // wrap in an object repository
        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepository objectRepository = factory.createRepository(repository);

        ObjectConnection connection = objectRepository.getConnection();

        ValueFactory vf = connection.getValueFactory();

        String userName = DatabaseMapStoresTest.randomWord(10);
        User user = new User(userName, "dummy_password");
        user.createSession().setName("dummy_user_session:_test_session");
        user.addRole(new Role(user, "dummy_role", "this_is_a_test_role_for_the_user"));

        URI id = vf.createURI("http://www.qoan.org/data#User#uuid#", user.getUuid());
        connection.addObject(id, user);

        Object found = connection.getObject(User.class, id);
        assertNotNull("there has to be a user", found);
        log("found user-object: " + found.toString());
        try {

            User u = (User) found;
            log("username: '" + u.getUsername() + "' password: '" + u.getPassword() + "'");

        } catch (Exception e) {
            fail("there should be no class-cast-exception");
        } finally {
            connection.commit();
            connection.close();
            repository.shutDown();
        }

    }

    public void testAliBabaObjectStoreWithStocks() throws Exception {

        File dataDir = new File(directoryName);
        //Repository store = new SailRepository(new MemoryStore(dataDir));
        Repository repository = new SailRepository(new MemoryStore(dataDir));
        repository.initialize();

        // wrap in an object repository
        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepository objectRepository = factory.createRepository(repository);

        ObjectConnection connection = objectRepository.getConnection();

        String stockName = DatabaseMapStoresTest.randomWord(10);
        String tickerSymbol = DatabaseMapStoresTest.randomWord(3).toUpperCase();
        StockEntity entity = new StockEntity();
        entity.setName(stockName);
        entity.setTickerSymbol(tickerSymbol);
        String uuid = entity.getUuid();
        // add a few quotes for good measure
        Random random = new Random();
        DateTime date = new DateTime(new Date());
        for (int i = 0; i < 10; i++) {
            StockQuote quote = new StockQuote();
            quote.setParentUUID(uuid);
            quote.setTickerSymbol(tickerSymbol);
            quote.setClose(100 * random.nextDouble());
            quote.setQuoteDate(date.minusDays(i).toDate());
            entity.addQuote(quote);
        }


        ValueFactory vf = connection.getValueFactory();
        URI id = vf.createURI("http://www.qoan.org/data#StockEntity#uuid", entity.getUuid());
        connection.addObject(id, entity);

        Object found = connection.getObject(StockEntity.class, id);
        assertNotNull("there has to be a stock-entity", found);
        log("found stock-entity: " + found.toString());
        try {

            StockEntity stock = (StockEntity) found;
            log("StockName: '" + stock.getName() + "' tickerSymbol: '" + stock.getTickerSymbol() + "'");
            assertNotNull("there has to be quotes", stock.getQuotes());
            assertTrue("quotes may not be empty", !stock.getQuotes().isEmpty());

        } catch (Exception e) {
            fail("there should be no class-cast-exception");
        } finally {
            connection.commit();
            connection.close();
            repository.shutDown();
        }

    }

    private void log(String message) {
        System.out.println(message);
    }
}
