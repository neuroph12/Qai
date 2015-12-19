package qube.qai.data;

import org.joda.time.DateTime;
import org.ojalgo.random.Normal;
import org.ojalgo.random.RandomNumber;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by rainbird on 12/4/15.
 */
public class TimeSequence<T extends Number> implements Serializable {

    private TreeSet<TimeSequenceEntry> entries;

    public TimeSequence() {
        entries = new TreeSet<TimeSequenceEntry>(new DateComparator());
    }

    public void add(Date date, T value) {
        entries.add(new TimeSequenceEntry(date, value));
    }

    public T getValue(Date date) {
        T value = null;
        for (TimeSequenceEntry entry : entries) {
            if (date.equals(entry.date)) {
                value = entry.entry;
                break;
            }
        }
        return value;
    }

    public Iterator<T> iterator() {
        return new TimeSeriesIterator<T>();
    }

    public Date[] toDates() {
        Date[] dates = new Date[entries.size()];
        int count = 0;
        for (TimeSequenceEntry entry : entries) {
            dates[count] = entry.getDate();
            count++;
        }
        return dates;
    }

    public T[] toArray() {
        T[] array = (T[]) Array.newInstance(Number.class, entries.size());
        int count = 0;
        for (TimeSequenceEntry entry : entries) {
            array[count] = entry.getEntry();
            count++;
        }
        return array;
    }

    class TimeSeriesIterator<T> implements Serializable, Iterator<T> {

        private Iterator<TimeSequenceEntry> iterator;

        public TimeSeriesIterator() {
            iterator = entries.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public T next() {
            return (T) iterator.next().getEntry();
        }
    }

    class TimeSequenceEntry implements Serializable {

        Date date;
        T entry;

        public TimeSequenceEntry(Date date, T entry) {
            this.date = date;
            this.entry = entry;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public T getEntry() {
            return entry;
        }

        public void setEntry(T entry) {
            this.entry = entry;
        }
    }

    class DateComparator implements Serializable, Comparator<TimeSequenceEntry> {

        public int compare(TimeSequenceEntry o1, TimeSequenceEntry o2) {
            return o1.date.compareTo(o2.date);
        }
    }

    public static TimeSequence<Double> createTimeSeries(Date start, Date end) {
        TimeSequence timeSequence = new TimeSequence();
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);

        RandomNumber generator = new Normal(0.5, 0.1);

        DateTime tmp = startDate;
        while(tmp.isBefore(endDate) || tmp.equals(endDate)) {
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
        while(tmp.isBefore(endDate) || tmp.equals(endDate)) {
            dates.add(tmp.toDate());
            tmp = tmp.plusDays(1);
        }

        return dates;
    }
}
