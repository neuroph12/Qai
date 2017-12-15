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

import com.fasterxml.uuid.Generators;
import junit.framework.TestCase;

import java.util.UUID;

/**
 * Created by rainbird on 12/13/15.
 */
public class UUIDGeneratorTest extends TestCase {

    private boolean debug = true;

    /*
    depending on how you generate the UUID you have different fields which are accessible
    this test is in order to demonstrate the differences
     */
    public void testUUIDGenerators() throws Exception {

        int numerOfUUIDsToGenerate = 1;

        log("time based UUIDS:");
        for (int i = 0; i < numerOfUUIDsToGenerate; i++) {
            UUID uuid = Generators.timeBasedGenerator().generate();
            log("Generated: " + uuid.toString());
            log("least significant bits:" + uuid.getLeastSignificantBits());
            log("most significant bits:" + uuid.getMostSignificantBits());
            log("timestamp: " + uuid.timestamp());
            log("clock sequence:" + uuid.clockSequence());
            log("node: " + uuid.node());
            log("version: " + uuid.version());
            log("variant: " + uuid.variant());
        }

        log("name based UUIDS:");
        for (int i = 0; i < numerOfUUIDsToGenerate; i++) {
            String dummyName = "a name @" + i; // + "@" + System.currentTimeMillis();
            UUID uuid = Generators.nameBasedGenerator().generate(dummyName);
            log("name used for generation: " + dummyName);
            log("Generated: " + uuid.toString());
            log("least significant bits:" + uuid.getLeastSignificantBits());
            log("most significant bits:" + uuid.getMostSignificantBits());
            log("version: " + uuid.version());
            log("variant: " + uuid.variant());
            // in name based UUID these fields are not accessible
            //log("timestamp: " + uuid.timestamp());
            //log("clock sequence:" + uuid.clockSequence());
            //log("node: " + uuid.node());
        }
    }

    protected void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
