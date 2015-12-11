package qube.qai.data.analysis;

import istu.samsroad.data.DataPoint;
import junit.framework.TestCase;
import qube.qai.data.ChangepointAdapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * Created by rainbird on 11/25/15.
 */
public class TestChangepointAdapter extends TestCase {

    private boolean debug = true;

    public void testChangePointAdapter() throws Exception {

        ChangepointAdapter changepointAdapter = new ChangepointAdapter();
        double[][] data = createDummyData(10000);

        Collection<ChangepointAdapter.ChangePoint> result = changepointAdapter.collectChangePoints(data);

        for (ChangepointAdapter.ChangePoint point : result) {
            if (Double.isNaN(point.getX())) {
                log("found nothing- change point a dude:" + point.getProbability());
            } else {
                log("found: x: " + point.getX() + " y:" + point.getY() + " probability: " + point.getProbability());
            }
        }

    }

    private double[][] createDummyData(int size) {
        double[][] data = new double[size][2];

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            data[i][0] = i;
            data[i][1] = random.nextDouble();
        }

        return data;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
