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

package qube.qai.util;

import junit.framework.TestCase;
import qube.qai.persistence.StockEntity;

import java.util.Collection;

/**
 * Created by rainbird on 1/13/17.
 */
public class TestCsvFileRipper extends TestCase {

    private String pathToCsvFiles = "/media/rainbird/ALEPH/datasets/";
    private String sNp500File = "S&P_500_constituents_financials.csv";

    public void testCsvFileRipper() throws Exception {

        CsvFileRipper ripper = new CsvFileRipper(pathToCsvFiles + sNp500File);
        Collection<StockEntity> stockEntities = ripper.ripStockEntities();
        assertNotNull("return value may not be null", stockEntities);
        assertTrue("return value may not be enpty", !stockEntities.isEmpty());

    }
}
