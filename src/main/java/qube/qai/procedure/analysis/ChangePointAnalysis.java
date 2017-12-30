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

package qube.qai.procedure.analysis;

import qube.qai.data.TimeSequence;
import qube.qai.data.analysis.ChangepointAdapter;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by rainbird on 11/28/15.
 */
public class ChangePointAnalysis extends Procedure {

    public static String NAME = "Change-Point Analysis";

    public static String DESCRIPTION = "Analysis for finding likely change points in a time-series";

    public ChangePointAnalysis() {
        super(NAME);
    }

    private TimeSequence timeSequence;

    private QaiDataProvider<StockEntity> entityProvider;

    private Collection<ChangePointMarker> markers;

    @Override
    protected void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<TimeSequence>(INPUT_TIME_SEQUENCE, MIMETYPE_TIME_SERIES) {
            @Override
            public void setValue(TimeSequence value) {
                timeSequence = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Collection<ChangePointMarker>>(CHANGE_POINTS, MIMETYPE_CHANGE_POINT_MARKER) {
            @Override
            public Collection<ChangePointMarker> getValue() {
                return markers;
            }
        });
    }

    @Override
    public void execute() {

        if (timeSequence == null) {
            error("Input time-series has not been initialized properly: null value");
            return;
        }

        ChangepointAdapter changepointAdapter = new ChangepointAdapter();
        Date[] dates = timeSequence.toDates();
        Number[] rawData = timeSequence.toArray();
        double[][] data = new double[rawData.length][2];
        for (int i = 0; i < rawData.length; i++) {
            data[i][0] = i;
            data[i][1] = rawData[i].doubleValue();
        }

        Collection<ChangepointAdapter.ChangePoint> resultChangepoints = changepointAdapter.collectChangePoints(data);
        markers = new ArrayList<ChangePointMarker>();

        for (ChangepointAdapter.ChangePoint point : resultChangepoints) {
            int index = point.getIndex();
            Date date = dates[index];
            ChangePointMarker marker = new ChangePointMarker(index,
                    point.getY(), date, point.getProbability());
            markers.add(marker);
        }

        info("finished '" + CHANGE_POINTS + "' analysis with " + markers.size() + " change-points detected");
    }

    public QaiDataProvider<StockEntity> getEntityProvider() {
        return entityProvider;
    }

    public void setEntityProvider(QaiDataProvider<StockEntity> entityProvider) {
        this.entityProvider = entityProvider;
    }

    public Collection<ChangePointMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(Collection<ChangePointMarker> markers) {
        this.markers = markers;
    }

    /**
     * marker class to separate the periods
     */
    public static class ChangePointMarker implements Serializable {
        private int index;
        private double value;
        private Date date;
        private double probability;

        public ChangePointMarker() {
        }

        public ChangePointMarker(int index, double value, Date date, double probability) {
            this.index = index;
            this.value = value;
            this.date = date;
            this.probability = probability;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public double getProbability() {
            return probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }
    }
}
