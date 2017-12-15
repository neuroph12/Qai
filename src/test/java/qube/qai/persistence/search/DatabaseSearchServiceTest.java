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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;
import qube.qai.main.QaiConstants;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.User;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * Created by rainbird on 3/5/17.
 */
public class DatabaseSearchServiceTest extends TestCase {

    private boolean debug = true;

    public void testUserDatabaseSearchService() throws Exception {

        String jpaModuleName = "STAND_ALONE_TEST_USERS";
        Injector injector = createInjector(jpaModuleName);
        EntityManager entityManager = injector.getInstance(EntityManager.class);
        DatabaseSearchService databaseSearch = new DatabaseSearchService();
        databaseSearch.setEntityManager(entityManager);

        User user = new User("sa", "");

        User dummy = new User("dummy", "");

        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.persist(dummy);
        entityManager.flush();
        entityManager.getTransaction().commit();

        // now we can actually do some testing
        String selectString = "sa";
        Collection<SearchResult> results = databaseSearch.searchInputString(selectString, QaiConstants.USERS, 1);

        assertNotNull("there have to be results", results);
        assertTrue("the result set may not be empty", !results.isEmpty());
        assertTrue("there are more than one user with same name", results.size() == 1);
        assertTrue("", user.getUuid().equals(results.iterator().next().getUuid()));

        selectString = "";
        results = databaseSearch.searchInputString(selectString, QaiConstants.USERS, 100);
        assertNotNull("there have to be results", results);
        assertTrue("the result set may not be empty", !results.isEmpty());
        assertTrue("there has to be more than one user", results.size() > 1);
    }

    public void testStocksDatabaseSearchService() throws Exception {

        // create the entity-manager and inject to the search-service
        String jpaModuleName = "STAND_ALONE_TEST_STOCKS";
        Injector injector = createInjector(jpaModuleName);
        EntityManager entityManager = injector.getInstance(EntityManager.class);
        DatabaseSearchService databaseSearch = new DatabaseSearchService();
        databaseSearch.setEntityManager(entityManager);

        StockGroup group = new StockGroup("S&P 3");
        StockEntity goog = new StockEntity();
        goog.setTickerSymbol("GOOG");
        goog.setName("Google");
        group.addStockEntity(goog);

        entityManager.getTransaction().begin();
        entityManager.persist(group);
        entityManager.getTransaction().commit();

        // now we can actually do some testing
        Collection<SearchResult> results = databaseSearch.searchInputString("", QaiConstants.STOCK_GROUPS, 0);
        assertNotNull("there have to be results", results);
        assertTrue("the result set may not be empty", !results.isEmpty());
        assertTrue("the result set may not be empty", group.getUuid().equals(results.iterator().next().getUuid()));

        results = databaseSearch.searchInputString("S&P 3", QaiConstants.STOCK_ENTITIES, 0);
        assertNotNull("there have to be results", results);
        assertTrue("the result set may not be empty", !results.isEmpty());
        assertTrue("the result set may not be empty", goog.getUuid().equals(results.iterator().next().getUuid()));
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

    private Injector createInjector(String moduleName) {
        Injector injector = Guice.createInjector(new JpaPersistModule(moduleName));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();
        return injector;
    }
}
