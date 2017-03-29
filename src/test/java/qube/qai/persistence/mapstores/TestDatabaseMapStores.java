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

import com.google.inject.Injector;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.main.QaiTestServerModule;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;

import java.util.*;

/**
 * Created by rainbird on 1/14/17.
 */
public class TestDatabaseMapStores extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("TestStockQuoteMapStore");

    private String[] names = {"HRS"};

    public void testUserMapStore() throws Exception {

        // this way i can use the injector even when it is active for other tests
        Injector injector = QaiTestServerModule.initUsersInjector();

        DatabaseMapStore mapStore = new DatabaseMapStore(User.class);
        injector.injectMembers(mapStore);

        User user = createUser();
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

    public void testRoleMapStore() throws Exception {

        Injector injector = QaiTestServerModule.initUsersInjector();
        DatabaseMapStore mapStore = new DatabaseMapStore(Role.class);
        injector.injectMembers(mapStore);

        User user = createUser();
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

        Injector injector = QaiTestServerModule.initUsersInjector();

        DatabaseMapStore mapStore = new DatabaseMapStore(Session.class);
        injector.injectMembers(mapStore);

        User user = createUser();
        Session session = new Session(randomWord(10), new Date());
        session.setUser(user);

        mapStore.store(session.getUuid(), session);

        Session foundSession = (Session) mapStore.load(session.getUuid());
        assertNotNull(foundSession);
        assertTrue(session.equals(foundSession));

        mapStore.delete(session.getUuid());

        Session lostSession = (Session) mapStore.load(session.getUuid());
        assertTrue(lostSession == null);
    }

    public void restStockQuoteMapStore() throws Exception {

        Injector injector = QaiTestServerModule.initStocksInjector();

        DatabaseMapStore mapStore = new DatabaseMapStore(StockQuote.class);
        injector.injectMembers(mapStore);

        for (int i = 0; i < names.length; i++) {
            Collection<String> keys = createKeys(names[i], mapStore);
            assertNotNull(keys);

            int count = 0;
            for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
                String key = it.next();
                StockQuote quote = (StockQuote) mapStore.load(key);
                assertNotNull("Quote shoud not be null", quote);
                String message = "Quote: " + quote.getTickerSymbol()
                        + " date: " + quote.getQuoteDate()
                        + " adj-close: " + quote.getAdjustedClose();
                logger.info(message);
                count++;
            }

            logger.info("found: " + keys.size() + " listed: " + count);

//            Map<String, StockQuote> result = mapStore.loadAll(keys);
//            assertNotNull(result);

        }

    }

    /**
     * @throws Exception
     */
    public void testStockEntityMapStore() throws Exception {

        Injector injector = QaiTestServerModule.initStocksInjector();

        DatabaseMapStore mapStore = new DatabaseMapStore(StockEntity.class);
        injector.injectMembers(mapStore);

        int number = 100;
        Map<String, StockEntity> entityMap = new HashMap<String, StockEntity>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = createEntity(name);
            String uuid = entity.getUuid();
            if (uuid == null || "".equals(uuid)) {
                uuid = UUIDService.uuidString();
                entity.setUuid(uuid);
            }
            mapStore.store(uuid, entity);

            // now we create and add the quotes
            Collection<StockQuote> quotes = generateQuotes(name, 100);
            for (StockQuote quote : quotes) {
                entity.addQuote(quote);
            }
            entityMap.put(uuid, entity);
        }

        // in this case the map-store should be returning all keys
//        Iterable<String> storedKeys = mapStore.loadAllKeys();
//        assertNotNull("stored keys may not be null", storedKeys);
//
//        // now read them back from database
//        for (String entityId : entityMap.keySet()) {
//            StockEntity cachedEntity = entityMap.get(entityId);
//            StockEntity storedEntity = (StockEntity) mapStore.load(entityId);
//            assertNotNull("there has to be an entity", storedEntity);
//            assertTrue("entities have to be equal", cachedEntity.equals(storedEntity));
//        }
//
//        // when we are done we delete the things as well, just to keep things managable
//        for (String entityId : entityMap.keySet()) {
//            mapStore.delete(entityId);
//        }
    }

    private Collection<String> createKeys(String name, DatabaseMapStore store) {
        Collection<String> keys = new ArrayList<>();

        StockQuoteDataStore dataStore = new StockQuoteDataStore();
        Collection<StockQuote> quotes = dataStore.retrieveQuotesFor(name);

        for (StockQuote quote : quotes) {
            store.store(quote.getUuid(), quote);
            keys.add(quote.getUuid());
        }

        return keys;
    }

    private Collection<StockQuote> generateQuotes(String tickerSymbol, int number) {
        Random random = new Random();
        Collection<StockQuote> quotes = new ArrayList<>();
        double startValue = random.nextInt(1000) * random.nextDouble();
        DateTime date = DateTime.now();

        for (int i = 0; i < number; i++) {
            StockQuote quote = new StockQuote();
            quote.setTickerSymbol(tickerSymbol);
            quote.setQuoteDate(date.minusDays(i).toDate());
            double close = startValue;
            if (random.nextBoolean()) {
                close = close + random.nextInt(10) * random.nextDouble();
            } else {
                close = close - random.nextInt(10) * random.nextDouble();
            }

            quote.setAdjustedClose(close);

            quotes.add(quote);
        }

        return quotes;
    }

    /**
     * creates a silly StockEntity
     *
     * @param name
     * @return
     */
    public static StockEntity createEntity(String name) {
        StockEntity entity = new StockEntity();
        entity.setName(name);
        entity.setAddress("address of " + name);
        entity.setGicsSector("gicsSector of " + name);
        entity.setGicsSubIndustry("gicsSubIndustry of " + name);
        entity.setSecurity("security of " + name);
        entity.setTradedIn("vsex");
        entity.setTickerSymbol(name);
        return entity;
    }

    public static User createUser() {
        Random random = new Random();
        String username = randomWord(1 + random.nextInt(11));
        String password = randomWord(1 + random.nextInt(8));
        User user = new User(username, password);
        return user;
    }

    public static String randomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(26)));
        }

        return word.toString();
    }
}
