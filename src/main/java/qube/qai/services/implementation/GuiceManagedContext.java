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

package qube.qai.services.implementation;

import com.google.inject.Injector;
import com.hazelcast.core.ManagedContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class GuiceManagedContext implements ManagedContext {

    private Logger logger = LoggerFactory.getLogger("GuiceManagedContext");

    private final Injector injector;

    @Inject
    public GuiceManagedContext(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object initialize(Object instance) {
        String message = "GuiceManagedContext injecting members of: " + instance;
        System.out.println(message);
        logger.info(message);
        injector.injectMembers(instance);
        return instance;
    }

}