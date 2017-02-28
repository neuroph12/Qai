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
public class TestTimeSequence extends TestCase {

    private boolean debug = true;
    Logger logger = LoggerFactory.getLogger("TestTimeSeries");

    public void testTimeSeries() throws Exception {
        Date startDate = DateTime.parse("2015-1-1").toDate();
        Date endDate = DateTime.now().toDate();
        TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(startDate, endDate);

        int count = 0;
        for (Iterator<Double> iterator = timeSequence.iterator(); iterator.hasNext(); count++) {
            log("next entry: " + iterator.next());
        }

        Number[] values = timeSequence.toArray();
        Date[] dates = timeSequence.toDates();
        for (int i = 0; i < values.length; i++) {
            log("on date: " + dates[i] + " value: " + values[i].doubleValue());
        }

    }

//    public static List<Date> createDates(Date start, Date end) {
//        List<Date> dates = new ArrayList<Date>();
//        DateTime startDate = new DateTime(start);
//        DateTime endDate = new DateTime(end);
//        DateTime tmp = startDate;
//        while(tmp.isBefore(endDate) || tmp.equals(endDate)) {
//            dates.add(tmp.toDate());
//            tmp = tmp.plusDays(1);
//        }
//
//        return dates;
//    }

//    public static TimeSequence<Double> createTimeSeries(Date start, Date end) {
//        TimeSequence timeSequence = new TimeSequence();
//        DateTime startDate = new DateTime(start);
//        DateTime endDate = new DateTime(end);
//
//        RandomNumber generator = new Normal(0.5, 0.1);
//
//        DateTime tmp = startDate;
//        while(tmp.isBefore(endDate) || tmp.equals(endDate)) {
//            timeSequence.add(tmp.toDate(), generator.doubleValue());
//            tmp = tmp.plusDays(1);
//        }
//        return timeSequence;
//    }

    private void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.debug(message);
        }
    }
}
