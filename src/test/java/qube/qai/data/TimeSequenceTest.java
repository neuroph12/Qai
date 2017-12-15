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

package qube.qai.data;

import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by rainbird on 12/10/15.
 */
public class TimeSequenceTest extends TestCase {

    Logger logger = LoggerFactory.getLogger("TestTimeSeries");

    public void testTimeSeries() throws Exception {
        Date startDate = DateTime.parse("2015-1-1").toDate();
        Date endDate = DateTime.now().toDate();
        TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(startDate, endDate);

        for (Iterator<Double> iterator = timeSequence.iterator(); iterator.hasNext(); ) {
            logger.debug("next entry: " + iterator.next());
        }

        Number[] values = timeSequence.toArray();
        Date[] dates = timeSequence.toDates();
        for (int i = 0; i < values.length; i++) {
            logger.debug("on date: " + dates[i] + " value: " + values[i].doubleValue());
        }

    }

}
