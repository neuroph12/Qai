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

package qube.qai.services;

import com.google.inject.Injector;

public class QaiInjectorService {

    private Injector injector;

    private static QaiInjectorService instance;

    public QaiInjectorService(Injector injector) {
        this.injector = injector;
        instance = this;
    }

    public static QaiInjectorService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No instance ");
        }
        return instance;
    }

    public Injector getInjector() {
        if (injector == null) {
            throw new IllegalStateException("No instance ");
        }
        return injector;
    }

    public void injectMembers(Object instance) {
        if (injector == null) {
            throw new IllegalStateException("No instance ");
        }
        injector.injectMembers(instance);
    }
}
