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

import org.joda.time.DateTime;
import org.ojalgo.random.Normal;
import org.ojalgo.random.RandomNumber;

import java.io.Serializable;
import java.util.*;

/**
 * Created by zenpunk on 12/4/15.
 */
public class TimeSequence implements Serializable {

    private String sequenceOf;

    private TimeEntry[] entries;

    public TimeSequence() {
        entries = new TimeEntry[0];
    }

    public TimeSequence(String sequenceOf) {
        this();
        this.sequenceOf = sequenceOf;
    }

    public void add(Date date, Double value) {

        TreeMap<Date, Double> entryMap = new TreeMap<>();
        for (TimeEntry entry : entries) {
            entryMap.put(entry.date, entry.value);
        }
        entryMap.put(date, value);
        entries = new TimeEntry[entryMap.size()];

        int index = 0;
        for (Date dDate : entryMap.keySet()) {
            entries[index] = new TimeEntry(dDate, entryMap.get(dDate));
            index++;
        }
    }

    public double getValue(Date date) {
        for (TimeEntry entry : entries) {
            if (date.equals(entry.date)) {
                return entry.value;
            }
        }
        return Double.NaN;
    }



    public Date[] toDates() {

        Date[] dates = new Date[entries.length];
        int count = 0;
        for (TimeEntry entry : entries) {
            dates[count] = entry.date;
            count++;
        }
        return dates;
    }

    public Double[] toArray() {
        Double[] array = new Double[entries.length];
        int count = 0;
        for (TimeEntry entry : entries) {
            array[count] = entry.value;
            count++;
        }
        return array;
    }

    public Set<Date> dates() {

        TreeMap<Date, Double> entryMap = new TreeMap<>();
        for (TimeEntry entry : entries) {
            entryMap.put(entry.date, entry.value);
        }

        return entryMap.keySet();
    }

    public Iterator<Double> iterator() {

        TreeMap<Date, Double> entryMap = new TreeMap<>();
        for (TimeEntry entry : entries) {
            entryMap.put(entry.date, entry.value);
        }

        return entryMap.values().iterator();
    }

    public String getSequenceOf() {
        return sequenceOf;
    }

    public void setSequenceOf(String sequenceOf) {
        this.sequenceOf = sequenceOf;
    }

    public static class TimeEntry implements Serializable {

        Date date;

        Double value;

        public TimeEntry(Date date, Double value) {
            this.date = date;
            this.value = value;
        }
    }

    public static TimeSequence createTimeSeries(Date start, Date end) {

        TimeSequence timeSequence = new TimeSequence("Dummy");
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);

        RandomNumber generator = new Normal(0.5, 0.1);

        DateTime tmp = startDate;
        while (tmp.isBefore(endDate) || tmp.equals(endDate)) {
            timeSequence.add(tmp.toDate(), generator.doubleValue());
            tmp = tmp.plusDays(1);
        }
        return timeSequence;
    }

    public static List<Date> createDates(Date start, Date end) {
        List<Date> dates = new ArrayList<Date>();
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);
        DateTime tmp = startDate;
        while (tmp.isBefore(endDate) || tmp.equals(endDate)) {
            dates.add(tmp.toDate());
            tmp = tmp.plusDays(1);
        }

        return dates;
    }
}
