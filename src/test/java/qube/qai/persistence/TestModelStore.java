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

package qube.qai.persistence;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.Role;
import qube.qai.user.User;

import java.io.StringWriter;
import java.util.Collection;

/**
 * Created by rainbird on 1/20/17.
 */
public class TestModelStore extends TestCase {

    public void testModelStore() throws Exception {

        ModelStore modelStore = new ModelStore("./test/dummy.model.directory");
        modelStore.init();

        User user = new User("dummy_user", "dummy_password");
        user.createSession().setName("dummy_user_session:_test_session");
        user.addRole(new Role(user, "dummy_role", "this_is_a_test_role_for_the_user"));

        Model model = User.userAsModel(user);
        modelStore.save(model);

        Collection<SearchResult> results = modelStore.searchInputString(user.getUsername(), "username", 1);
        assertNotNull("there must be results", results);
        assertTrue("there has to be the user", !results.isEmpty());

        SearchResult result = results.iterator().next();
        assertNotNull("result may not be null", result);
        assertTrue("username has to be right", "user".equals(result.getContext()));
        assertTrue("uuid must be right", user.getUuid().equals(result.getUuid()));

        modelStore.remove(model);

        User serializedUser = (User) modelStore.getSerializedObject();
        assertNotNull("serialized user may not be null", serializedUser);
        assertTrue("uuid has to be equal", user.getUuid().equals(serializedUser.getUuid()));
        assertTrue("username has to be equal", user.getUsername().equals(serializedUser.getUsername()));
        assertTrue("password has to be equal", user.getPassword().equals(serializedUser.getPassword()));

    }

    public void testUserToModelConversion() throws Exception {

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
