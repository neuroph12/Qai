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

package qube.qai.services.implementation;

import com.fasterxml.uuid.Generators;
import qube.qai.services.UUIDServiceInterface;

import java.util.UUID;

/**
 * Created by rainbird on 11/2/15.
 */
public class UUIDService implements UUIDServiceInterface {

    public UUID createUUID() {
        return Generators.timeBasedGenerator().generate();
    }

    public String createUUIDString() {
        return Generators.timeBasedGenerator().generate().toString();
    }

    public static String uuidString() {
        return Generators.timeBasedGenerator().generate().toString();
    }
}
