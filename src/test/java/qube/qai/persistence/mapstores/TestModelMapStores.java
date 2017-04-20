/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
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
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 4/6/17.
 */
public class TestModelMapStores extends TestCase {

    private String directoryName = "./test/dummy.model.directory";

    public void testUserMapStore() throws Exception {

        // this way i can use the injector even when it is active for other tests
        //Injector injector = QaiTestServerModule.initUsersInjector();

        ModelMapStore mapStore = new ModelMapStore(User.class);
        //injector.injectMembers(mapStore);

        User user = TestDatabaseMapStores.createUser();
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

        // this way i can use the injector even when it is active for other tests
        //Injector injector = QaiTestServerModule.initUsersInjector();

        PersistentModelMapStore mapStore = new PersistentModelMapStore(User.class, directoryName);
        mapStore.init();
        //injector.injectMembers(mapStore);

        User user = TestDatabaseMapStores.createUser();
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

        //Injector injector = QaiTestServerModule.initUsersInjector();
        ModelMapStore mapStore = new ModelMapStore(Role.class);
        //injector.injectMembers(mapStore);

        User user = TestDatabaseMapStores.createUser();
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

        //Injector injector = QaiTestServerModule.initUsersInjector();

        ModelMapStore mapStore = new ModelMapStore(Session.class);
        //injector.injectMembers(mapStore);

        User user = TestDatabaseMapStores.createUser();
        Session session = new Session(TestDatabaseMapStores.randomWord(10), new Date());
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

        //Injector injector = QaiTestServerModule.initStocksInjector();

        ModelMapStore mapStore = new ModelMapStore(StockEntity.class);
        //injector.injectMembers(mapStore);

        int number = 100;
        Map<String, StockEntity> entityMap = new HashMap<String, StockEntity>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = TestDatabaseMapStores.createEntity(name);
            String uuid = entity.getUuid();
            if (uuid == null || "".equals(uuid)) {
                uuid = UUIDService.uuidString();
                entity.setUuid(uuid);
            }
            mapStore.store(uuid, entity);

            // now we create and add the quotes
            Collection<StockQuote> quotes = TestDatabaseMapStores.generateQuotes(name, 100);
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
}
