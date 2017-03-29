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

package qube.qai.persistence.search;

import com.google.inject.Injector;
import junit.framework.TestCase;
import qube.qai.main.QaiTestServerModule;
import qube.qai.persistence.StockCategory;
import qube.qai.persistence.StockEntity;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.User;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Set;

/**
 * Created by rainbird on 3/5/17.
 */
public class TestDatabaseSearchService extends TestCase {

    private boolean debug = true;

    public void testUserDatabaseSearchService() throws Exception {

        Injector injector = QaiTestServerModule.initUsersInjector();
        DatabaseSearchService databaseSearch = new DatabaseSearchService();
        injector.injectMembers(databaseSearch);

        // now we can actually do some testing
        Collection<SearchResult> results = databaseSearch.searchInputString("*", "User", 1);

        assertNotNull("there have to be results", results);
        assertTrue("the result set may not be empty", !results.isEmpty());

        // now get the entity-manager to get the actual thing
        EntityManager entityManager = databaseSearch.getEntityManager();

        for (SearchResult result : results) {
            String uuid = result.getUuid();
            User user = entityManager.find(User.class, uuid);
            assertNotNull("the user not found in the database?!?", user);
        }

    }

    public void testStocksDatabaseSearchService() throws Exception {

        // create the entity-manager and inject to the search-service
        Injector injector = QaiTestServerModule.initStocksInjector();
        DatabaseSearchService databaseSearch = new DatabaseSearchService();
        injector.injectMembers(databaseSearch);

        // now we can actually do some testing
        Collection<SearchResult> results = databaseSearch.searchInputString("*", "StockCategory", 10);
        assertNotNull("there have to be results", results);
        assertTrue("the result set may not be empty", !results.isEmpty());

        // now get the entity-manager to get the actual thing
        EntityManager entityManager = databaseSearch.getEntityManager();

        for (SearchResult result : results) {
            String uuid = result.getUuid();
            StockCategory category = entityManager.find(StockCategory.class, uuid);
            assertNotNull("there has to be a category", category);
            log("found category name: '" + category.getName() + "' with entities as follows:");
            Set<StockEntity> entities = category.getEntities();
            assertNotNull("there have to be entities", entities);
            assertTrue("entities list must be full", !entities.isEmpty());
            int count = 0;
            for (StockEntity entity : entities) {
                assertNotNull("ther has to be an entity", entity);
                log("Stock entity: '" + entity.getName() + "'");
                count++;
            }
            log("in all " + count + " entities in category");
        }
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
