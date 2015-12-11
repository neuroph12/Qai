package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.ChangepointAdapter;
import qube.qai.data.TimeSeries;
import qube.qai.procedure.ProcedureChain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by rainbird on 11/28/15.
 */
public class ChangePointAnalysis extends ProcedureChain {

    public static String NAME = "Change-Point Analysis";

    public static String DESCRIPTION = "Analysis for finding likely change points in a time-series";

    /**
     * runs change-point analysis of a given time-series
     */
    public ChangePointAnalysis() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_TIME_SERIES);
        arguments.putResultNames(CHANGE_POINTS);
    }

    @Override
    public void run() {

        if (!arguments.isSatisfied()) {
            throw new RuntimeException("Process: " + name + " has not been initialized properly- missing argument");
        }

        // first get the selector
        TimeSeries timeSeries = (TimeSeries) arguments.getSelector(INPUT_TIME_SERIES).getData();
        if (timeSeries == null) {
            logger.error("Input time-series has not been initialized properly: null value");
        }

        ChangepointAdapter changepointAdapter = new ChangepointAdapter();
        Date[] dates = timeSeries.toDates();
        Number[] rawData = timeSeries.toArray();
        double[][] data = new double[rawData.length][2];
        for (int i = 0; i < rawData.length; i++) {
            data[i][0] = i;
            data[i][1] = rawData[i].doubleValue();
        }

        Collection<ChangepointAdapter.ChangePoint> resultChangepoints = changepointAdapter.collectChangePoints(data);
        Collection<ChangePointMarker> markers = new ArrayList<ChangePointMarker>();

        for (ChangepointAdapter.ChangePoint point : resultChangepoints) {
            int index = point.getIndex();
            Date date = dates[index];
            ChangePointMarker marker = new ChangePointMarker(index,
                    point.getY(), date, point.getProbability());
            markers.add(marker);
        }

        log("adding '" + CHANGE_POINTS + "' to return values");
        arguments.addResult(CHANGE_POINTS, markers);
    }

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
