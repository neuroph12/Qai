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

package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 11/19/15.
 */
public class QaiTestBase extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("QaiTest");

    protected boolean debug = true;

    protected Injector injector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        injector = Guice.createInjector(new QaiTestModule());
        injector.injectMembers(this);
    }

    public static void main(String[] params) {
        String[] tests = {QaiTestBase.class.getName()};
        TestRunner.main(tests);
    }

    protected void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.info(message);
        }
    }

}
