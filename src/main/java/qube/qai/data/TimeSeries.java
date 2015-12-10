package qube.qai.data;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by rainbird on 12/4/15.
 */
public class TimeSeries<T extends Number> {

    private TreeSet<TimeSeriesEntry> entries;

    public TimeSeries() {
        entries = new TreeSet<TimeSeriesEntry>(new DateComparator());
    }

    public void add(Date date, T value) {
        entries.add(new TimeSeriesEntry(date, value));
    }

    public Iterator<T> iterator() {
        return new TimeSeriesIterator<T>();
    }

    public Date[] toDates() {
        Date[] dates = new Date[entries.size()];
        int count = 0;
        for (TimeSeriesEntry entry : entries) {
            dates[count] = entry.getDate();
            count++;
        }
        return dates;
    }

    public T[] toArray() {
        T[] array = (T[]) Array.newInstance(Number.class, entries.size());
        int count = 0;
        for (TimeSeriesEntry entry : entries) {
            array[count] = entry.getEntry();
            count++;
        }
        return array;
    }

    class TimeSeriesIterator<T> implements Iterator<T> {

        private Iterator<TimeSeriesEntry> iterator;

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

    class TimeSeriesEntry {

        Date date;
        T entry;

        public TimeSeriesEntry(Date date, T entry) {
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

    class DateComparator implements Comparator<TimeSeriesEntry> {

        public int compare(TimeSeriesEntry o1, TimeSeriesEntry o2) {
            return o1.date.compareTo(o2.date);
        }
    }
}
