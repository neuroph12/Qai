package qube.qai.data;

import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.ojalgo.random.Normal;
import org.ojalgo.random.RandomNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.TimeSeries;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by rainbird on 12/10/15.
 */
public class TestTimeSeries extends TestCase {

    private boolean debug = true;
    Logger logger = LoggerFactory.getLogger("TestTimeSeries");

    public void testTimeSeries() throws Exception {
        Date startDate = DateTime.parse("2015-1-1").toDate();
        Date endDate = DateTime.now().toDate();
        TimeSeries<Double> timeSeries = createTimeSeries(startDate, endDate);

        int count = 0;
        for (Iterator<Double> iterator = timeSeries.iterator(); iterator.hasNext(); count++) {
            log("next entry: " + iterator.next());
        }

        Number[] values = timeSeries.toArray();
        Date[] dates = timeSeries.toDates();
        for (int i = 0; i < values.length; i++) {
            log("on date: " + dates[i] + " value: " + values[i].doubleValue());
        }

    }

    public static TimeSeries<Double> createTimeSeries(Date start, Date end) {
        TimeSeries timeSeries = new TimeSeries();
        DateTime startDate = new DateTime(start);
        DateTime endDate = new DateTime(end);

        RandomNumber generator = new Normal(0.5, 0.1);

        DateTime tmp = startDate;
        while(tmp.isBefore(endDate) || tmp.equals(endDate)) {
            timeSeries.add(tmp.toDate(), generator.doubleValue());
            tmp = tmp.plusDays(1);
        }
        return timeSeries;
    }

    private void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.debug(message);
        }
    }
}
