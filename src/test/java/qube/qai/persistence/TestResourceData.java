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

import java.io.File;

/**
 * Created by rainbird on 4/17/17.
 */
public class TestResourceData extends TestCase {

    public void testResourceData() throws Exception {
        String fileName = "/home/rainbird/projects/work/qai/test/goog.array";
        File file = new File(fileName);
        assertTrue("need a file in order to run the test", file.exists());

        ResourceData data = new ResourceData();
        data.readFileData(file);
        assertNotNull("there has to be data in the file", data.getBinaryData());
        assertTrue("the content may not be empty", data.getBinaryData().length > 0);
    }
}
