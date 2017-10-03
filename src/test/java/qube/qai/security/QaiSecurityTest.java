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

package qube.qai.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import qube.qai.main.QaiTestBase;
import qube.qai.main.QaiTestSecurityModule;

public class QaiSecurityTest extends QaiTestBase {

    public void testQaiSecurityModule() throws Exception {


        Injector securityInjector = Guice.createInjector(new QaiTestSecurityModule());

        assertNotNull("security injector has not been created", securityInjector);

        QaiSecurityManager security = new QaiSecurityManager();
        injector.injectMembers(security);

        assertNotNull("there has to be a security to begin with", security);

//        fail("this was just the initialization which went alright, rest of the test need to be implemented");

    }
}
