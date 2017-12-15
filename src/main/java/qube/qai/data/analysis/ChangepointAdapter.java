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

import istu.samsroad.Analysis;
import istu.samsroad.AnalysisSettings;
import istu.samsroad.data.DataPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by rainbird on 11/25/15.
 */
public class ChangepointAdapter {

    // @TODO add a wrapper class for AnalysisSettings as well
    private AnalysisSettings settings;
    private Analysis analysis;

    public ChangepointAdapter() {
        settings = new AnalysisSettings();
    }

    // this is the part which we really need after all
    public Collection<ChangePoint> collectChangePoints(double[][] data) {

        Collection<ChangePoint> result = new ArrayList<ChangePoint>();

        analysis = new Analysis(data, settings);

        analysis.runAnalysis();
        Vector<DataPoint> changePoints = analysis.getChangePoints();

        for (DataPoint dataPoint : changePoints) {
            // don't add the change-points which are marked as removed
            // or which don't have a value
            if (dataPoint.isRemoved()
                    || !dataPoint.isValue()
                    || Double.isNaN(dataPoint.getX())) {
                continue;
            }
            ChangePoint changePoint = convert(dataPoint);
            result.add(changePoint);
        }

        return result;
    }

//    public AnalysisSettings getSettings() {
//        return settings;
//    }
//
//    public void setSettings(AnalysisSettings settings) {
//        this.settings = settings;
//    }

    private ChangePoint convert(DataPoint dataPoint) {

        ChangePoint changePoint = new ChangePoint();
        changePoint.index = dataPoint.getIndex();
        changePoint.x = dataPoint.getX();
        changePoint.y = dataPoint.getY();
        changePoint.probability = dataPoint.getProbability();
        changePoint.removed = dataPoint.isRemoved();
        changePoint.checked = dataPoint.isChecked();
        changePoint.value = dataPoint.isValue();

        return changePoint;
    }

    public static class ChangePoint {
        private int index;
        private double x;
        private double y;
        private double probability;
        private boolean removed;
        private boolean checked;
        private boolean value;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getProbability() {
            return probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }

        public boolean isRemoved() {
            return removed;
        }

        public void setRemoved(boolean removed) {
            this.removed = removed;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}
