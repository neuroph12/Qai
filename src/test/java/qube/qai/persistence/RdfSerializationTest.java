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

package qube.qai.persistence;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.ConcatenationNode;
import qube.qai.persistence.mapstores.DatabaseMapStoresTest;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;
import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by rainbird on 3/29/17.
 */
public class RdfSerializationTest extends TestCase {

    private static Logger logger = LoggerFactory.getLogger("RdfSerializationTest");

    public void testRedSerializationOfStocks() throws Exception {

        StockGroup category = new StockGroup("My category");
        StockEntity entity = new StockEntity();
        entity.setName("Test Stock");
        entity.setTickerSymbol("TSTCK");
        StockQuote quote = new StockQuote();
        quote.setTickerSymbol("TSTCK");
        quote.setQuoteDate(new Date());
        entity.addQuote(quote);

        String categoryUuid = category.getUuid();
        String entityUuid = entity.getUuid();
        String quoteUuid = quote.getUuid();

        logger.info("Category with uuid: " + categoryUuid);
        logger.info("Entity with uuid: " + entityUuid);
        logger.info("Quote with uuid: " + quoteUuid);


        Model model = ModelFactory.createDefaultModel();
        Bean2RDF writer = new Bean2RDF(model);
        writer.save(category);
        model.write(System.out);

        RDF2Bean reader = new RDF2Bean(model);
        StockGroup storedCategory = reader.load(StockGroup.class, categoryUuid);
        assertNotNull("there has to be a user", storedCategory);
        assertTrue("the users must be equals", category.equals(storedCategory));
    }

    public void testRdfSerializationOfUsers() throws Exception {

        User user = new User("test user", "test password");
        user.addRole(new Role());
        Session session = user.createSession();
        String sessionUuid = session.getUuid();
        String uuid = user.getUuid();

        logger.info("User with uuid: " + uuid);

        Model model = ModelFactory.createDefaultModel();
        Bean2RDF writer = new Bean2RDF(model);
        writer.save(user);
        model.write(System.out);

        RDF2Bean reader = new RDF2Bean(model);
        User storedUser = reader.load(User.class, uuid);
        assertNotNull("there has to be a user", storedUser);
        assertTrue("the users must be equals", user.equals(storedUser));

        Session storedSession = reader.load(Session.class, sessionUuid);
        assertNotNull("there has to be a session", storedSession);
        assertTrue("the sessions has to be equal", session.equals(storedSession));
    }

    public void testRdfSerializationOfAntimirovNodes() throws Exception {

        // we make it easy for ourself creating the ast-tree
        BaseNode parsedNode = parseExpression("(foo baz[integer] bar[double])");
        String uuid = parsedNode.getUuid();
        logger.info("parsedNode with uuid: " + uuid + " " + parsedNode.toString());

        Model model = ModelFactory.createDefaultModel();
        Bean2RDF writer = new Bean2RDF(model);

        writer.save(parsedNode);
        model.write(System.out);

        // and now the tricky part
        RDF2Bean reader = new RDF2Bean(model);
        BaseNode storedNode = reader.load(ConcatenationNode.class, uuid);

        assertNotNull("there has to be a return value", storedNode);
        logger.info("parsed: '" + parsedNode.toString() + "' read: " + storedNode.toString() + "'");
        assertTrue("the nodes must be equal", parsedNode.toString().equals(storedNode.toString()));
        assertTrue("the nodes must be equal", parsedNode.equals(storedNode));
    }

    public void testRdfSerializationOfProcedures() throws Exception {

        Model model = ModelFactory.createDefaultModel();
        Bean2RDF writer = new Bean2RDF(model);

//        AppInfo appInfo = new AppInfo();
//        writer.save(appInfo);

        // go through all rpocedures and try them out
        Collection<Procedure> procedures = generateAllProcedures();
        for (Procedure procedure : procedures) {
            String uuid = procedure.getUuid();
            logger.info("procedure uuid: " + uuid + " " + procedure.toString());

            writer.save(procedure);
            model.write(System.out);

            RDF2Bean reader = new RDF2Bean(model);
            Procedure storedProcedure = reader.load(procedure.getClass(), uuid);
            assertNotNull("three has to be something stored after all", storedProcedure);
            assertTrue("the procedures must be equal", procedure.equals(storedProcedure));
        }
    }

    public void testRdfSerializationCollections() throws Exception {

        Model model = ModelFactory.createDefaultModel();
        Bean2RDF writer = new Bean2RDF(model);

        Collection<String> uuids = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            StockEntity entity = DatabaseMapStoresTest.createEntity(DatabaseMapStoresTest.randomWord(10));
            uuids.add(entity.getUuid());
            writer.save(entity);
        }

        RDF2Bean reader = new RDF2Bean(model);
        for (String uuid : uuids) {
            StockEntity entity = reader.load(StockEntity.class, uuid);
            assertNotNull("there has to be an entity", entity);
            assertTrue("uuids must be identical", uuid.equals(entity.getUuid()));
        }

        Collection<StockEntity> entities = Sparql.exec(model, StockEntity.class, "SELECT ?s WHERE { ?s a <http://qube.qai.persistence/StockEntity> }");
        //Collection<StockEntity> entities = reader.load(StockEntity.class);
        assertNotNull("there has to be entities", entities);
        assertTrue("entities list may not be empty", !entities.isEmpty());

    }

    public static Collection<Procedure> generateAllProcedures() {

        Collection<Procedure> procedures = new ArrayList<>();

        for (ProcedureTemplate template : ProcedureLibrary.getTemplateMap().values()) {
            Procedure proc = template.createProcedure();
            procedures.add(proc);
        }

        return procedures;
    }

    private BaseNode parseExpression(String expression) {
        //AntimirovParser parser = new AntimirovParser();
        BaseNode parsedNode = null; //parser.paranthesis(parser.expr()).parse(expression);
        return parsedNode;
    }
}
