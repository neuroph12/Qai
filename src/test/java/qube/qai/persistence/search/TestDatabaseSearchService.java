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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import junit.framework.TestCase;

/**
 * Created by rainbird on 3/5/17.
 */
public class TestDatabaseSearchService extends TestCase {

    public void testDatabaseSearchService() throws Exception {

        // create the entity-manager and inject to the search-service
        Injector injector = Guice.createInjector(new JpaPersistModule("TEST_STOCKS"));
        PersistService service = injector.getInstance(PersistService.class);
        service.start();

        DatabaseSearchService databaseSearch = new DatabaseSearchService();
        injector.injectMembers(databaseSearch);

        // now we can actually do some testing
    }
}
