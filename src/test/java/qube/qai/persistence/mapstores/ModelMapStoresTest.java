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

package qube.qai.persistence.mapstores;

import junit.framework.TestCase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.procedure.utils.SelectForAll;
import qube.qai.procedure.utils.SelectForEach;
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;

import java.util.*;

/**
 * Created by rainbird on 4/6/17.
 */
public class ModelMapStoresTest extends TestCase {

    private String userDirectory = "./test/dummy.user.model.directory";

    private String procedureDirectory = "./test/dummy.procedure.model.directory";

    public void testUserMapStore() throws Exception {

        ModelMapStore mapStore = new ModelMapStore(User.class);

        User user = DatabaseMapStoresTest.createUser();
        Session session = user.createSession();

        Role role = new Role(user, "DO_ALL_ROLE", "this role will allow you to do everything");
        user.addRole(role);

        mapStore.store(user.getUuid(), user);

        User readUser = (User) mapStore.load(user.getUuid());
        assertNotNull(readUser);
        assertTrue(user.equals(readUser));
        assertTrue(!user.getSessions().isEmpty());
        Session readSession = readUser.getSessions().iterator().next();
        assertTrue(session.equals(readSession));
        mapStore.delete(user.getUuid());

        User lostUser = (User) mapStore.load(user.getUuid());
        assertTrue(lostUser == null);

    }

    public void testPersistentUserMapStore() throws Exception {

        PersistentModelMapStore mapStore = new PersistentModelMapStore(userDirectory, User.class);
        mapStore.init();

        User user = DatabaseMapStoresTest.createUser();
        Session session = user.createSession();

        Role role = new Role(user, "DO_ALL_ROLE", "this role will allow you to do everything");
        user.addRole(role);

        mapStore.store(user.getUuid(), user);

        User readUser = (User) mapStore.load(user.getUuid());
        assertNotNull(readUser);
        assertTrue(user.equals(readUser));
        assertTrue(!user.getSessions().isEmpty());
//        Session readSession = readUser.getSessions().iterator().next();
//        assertTrue(session.equals(readSession));
        mapStore.delete(user.getUuid());

        User lostUser = (User) mapStore.load(user.getUuid());
        assertTrue(lostUser == null);

    }

    public void testRoleMapStore() throws Exception {

        ModelMapStore mapStore = new ModelMapStore(Role.class);

        User user = DatabaseMapStoresTest.createUser();
        Role role = new Role(user, "DO_ALL_ROLE", "this role will allow you to do everything");
        mapStore.store(role.getUuid(), role);

        Role foundRole = (Role) mapStore.load(role.getUuid());
        assertNotNull(foundRole);
        assertTrue(role.equals(foundRole));

        mapStore.delete(role.getUuid());

        Role lostRole = (Role) mapStore.load(role.getUuid());
        assertTrue(lostRole == null);

    }

    public void testSessionMapStore() throws Exception {

        ModelMapStore mapStore = new ModelMapStore(Session.class);

        User user = DatabaseMapStoresTest.createUser();
        Session session = new Session(DatabaseMapStoresTest.randomWord(10), new Date());
        session.setUser(user);

        mapStore.store(session.getUuid(), session);

        Session foundSession = (Session) mapStore.load(session.getUuid());
        assertNotNull(foundSession);
        assertTrue(session.equals(foundSession));

        mapStore.delete(session.getUuid());

        Session lostSession = (Session) mapStore.load(session.getUuid());
        assertTrue(lostSession == null);
    }

    public void testStockEntityMapStore() throws Exception {

        ModelMapStore mapStore = new ModelMapStore(StockEntity.class);

        int number = 100;
        Map<String, StockEntity> entityMap = new HashMap<String, StockEntity>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = DatabaseMapStoresTest.createEntity(name);
            String uuid = entity.getUuid();
            if (uuid == null || "".equals(uuid)) {
                uuid = UUIDService.uuidString();
                entity.setUuid(uuid);
            }
            mapStore.store(uuid, entity);

            // now we create and add the quotes
            Collection<StockQuote> quotes = DatabaseMapStoresTest.generateQuotes(name, 100);
            for (StockQuote quote : quotes) {
                entity.addQuote(quote);
            }
            entityMap.put(uuid, entity);
        }

        for (String uuid : entityMap.keySet()) {
            StockEntity storedEntity = (StockEntity) mapStore.load(uuid);
            StockEntity cachedEntity = entityMap.get(uuid);
            assertTrue("stored and cached entites must be equal", cachedEntity.equals(storedEntity));
        }
    }

    /**
     * this is where we try and see how our procedures from disk will work
     */
    public void testPersistentProcedureMapStore() throws Exception {

        PersistentModelMapStore mapStore = new PersistentModelMapStore(procedureDirectory,
                Procedure.class, SelectForAll.class, SelectForEach.class);
        mapStore.init();

        ProcedureLibrary procedureLibrary = new ProcedureLibrary();

        Collection<String> uuids = new ArrayList<>();
        for (ProcedureTemplate template : procedureLibrary.getTemplateMap().values()) {
            Procedure procedure = template.createProcedure();
            String uuid = procedure.getUuid();
            uuids.add(uuid);
            mapStore.store(uuid, procedure);

            log("loading procedure of type " + procedure.getName());
            Procedure storedProcedure = (Procedure) mapStore.load(uuid);
            String procedureName = template.getProcedureName();
            assertNotNull("there has to be a stored procedure for " + procedureName, storedProcedure);

        }

        /*ChangePoints changePoint = new ChangePoints();
        String uuid = changePoint.getUuid();
        mapStore.store(uuid, changePoint);

        log("loading procedure of type " + changePoint.getName());
        Procedure found = (Procedure) mapStore.load(uuid);
        assertNotNull("there has to be a object stored", found);*/

    }

    private void log(String message) {
        System.out.println(message);
    }
}
