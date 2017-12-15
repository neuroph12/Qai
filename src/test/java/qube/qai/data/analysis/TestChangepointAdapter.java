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

package qube.qai.data.analysis;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.Random;

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
