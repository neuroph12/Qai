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

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import qube.qai.persistence.mapstores.TestDatabaseMapStores;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.Role;
import qube.qai.user.User;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import static qube.qai.main.QaiConstants.USERS;

/**
 * Created by rainbird on 1/20/17.
 */
public class TestModelSearchServices extends TestCase {

    public void testUserModelStore() throws Exception {

        ModelSearchService modelSearchService = new ModelSearchService(USERS, "./test/dummy.model.directory");
        modelSearchService.init();

        Collection<String> uuids = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String userName = TestDatabaseMapStores.randomWord(10);
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

    /**
     * since we are now letting this be done by jenabeans, somewhat pointless test
     * don't need to run it
     *
     * @throws Exception
     */
    public void estUserToModelConversion() throws Exception {

        User user = new User("username", "password");
        user.createSession().setName("User Session #One");

        user.addRole(new Role(user, "user role", "just another role"));
        user.addRole(new Role(user, "test role", "test role"));

        Model userModel = User.userAsModel(user);
        assertNotNull("returned model may not be null", userModel);

        StringWriter writer = new StringWriter();
        userModel.write(writer);

        log(writer.toString());
    }

    private void log(String message) {
        System.out.println(message);
    }
}
