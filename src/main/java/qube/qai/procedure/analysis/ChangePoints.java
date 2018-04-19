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
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.services.QaiInjectorService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * Created by zenpunk on 11/28/15.
 */
public class ChangePoints extends Procedure {

    public String NAME = "Change-Point Analysis";

    public String DESCRIPTION = "Drop stock-entities from finance list so that their quotes can be analysed for change-points.";

    public ChangePoints() {
        super("Change-Point Analysis");
        markers = new ChangePointMarker[0];
    }

    private TimeSequence timeSequence;

    private ChangePointMarker[] markers;

    @Override
    protected void buildArguments() {
        /*getProcedureDescription().setDescription(DESCRIPTION);
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
        });*/
    }

    @Override
    public void execute() {

        if (getInputs() == null || getInputs().isEmpty()) {
            info("There are no inputs to work with- terminating execution.");
            return;
        }

        QaiDataProvider<StockEntity> provider = getInputs().iterator().next();
        QaiInjectorService.getInstance().injectMembers(provider);
        StockEntity entity = provider.getData();
        Set<StockQuote> quotes = entity.getQuotes();
        if (quotes == null || quotes.isEmpty()) {
            info("There are no quotes for entity with ticker-symbol: '" +
                    entity.getTickerSymbol() + "'. No data to work with- terminating execution.");
            return;
        }

        timeSequence = new TimeSequence(entity.getTickerSymbol());
        for (StockQuote quote : quotes) {
            timeSequence.add(quote.getQuoteDate(), quote.adjustedClose);
        }

        ChangepointAdapter changepointAdapter = new ChangepointAdapter();
        Date[] dates = timeSequence.toDates();
        Number[] rawData = timeSequence.toArray();
        double[][] data = new double[rawData.length][2];
        for (int i = 0; i < rawData.length; i++) {
            data[i][0] = i;
            data[i][1] = rawData[i].doubleValue();
        }

        ArrayList<ChangepointAdapter.ChangePoint> resultChangepoints = changepointAdapter.collectChangePoints(data);

        for (ChangepointAdapter.ChangePoint point : resultChangepoints) {
            int index = point.getIndex();
            Date date = dates[index];
            ChangePointMarker marker = new ChangePointMarker(index,
                    point.getY(), date, point.getProbability());
            addMarker(marker);
        }

        info("finished '" + CHANGE_POINTS + "' analysis with " + markers.length + " change-points detected");

        hasExecuted = true;
    }

    private void addMarker(ChangePointMarker marker) {

        ArrayList<ChangePointMarker> tempMarkers = new ArrayList<>();
        for (ChangePointMarker m : markers) {
            tempMarkers.add(m);
        }
        tempMarkers.add(marker);

        markers = new ChangePointMarker[tempMarkers.size()];
        tempMarkers.toArray(markers);
    }

    @Override
    public Procedure createInstance() {
        return new ChangePoints();
    }

    public ChangePointMarker[] getMarkers() {
        return markers;
    }

    /*public void setMarkers(ArrayList<ChangePointMarker> markers) {
        this.markers = markers;
    }*/

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
