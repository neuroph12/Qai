package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.TimeSequence;
import qube.qai.procedure.Procedure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import qube.qai.data.analysis.ChangepointAdapter;
import qube.qai.procedure.ProcedureDecorator;

/**
 * Created by rainbird on 11/28/15.
 */
public class ChangePointAnalysis extends ProcedureDecorator {

    public static String NAME = "Change-Point Analysis";

    public static String DESCRIPTION = "Analysis for finding likely change points in a time-series";

    /**
     * runs change-point analysis of a given time-series
     */
    public ChangePointAnalysis(Procedure procedure) {
        super(procedure);
    }


    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_TIME_SEQUENCE);
        arguments.putResultNames(CHANGE_POINTS);
    }

    @Override
    public void execute() {

        toDecorate.execute();

        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(toDecorate.getArguments());
        }

        // first get the selector
        TimeSequence timeSequence = (TimeSequence) arguments.getSelector(INPUT_TIME_SEQUENCE).getData();
        if (timeSequence == null) {
            logger.error("Input time-series has not been initialized properly: null value");
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
        Collection<ChangePointMarker> markers = new ArrayList<ChangePointMarker>();

        for (ChangepointAdapter.ChangePoint point : resultChangepoints) {
            int index = point.getIndex();
            Date date = dates[index];
            ChangePointMarker marker = new ChangePointMarker(index,
                    point.getY(), date, point.getProbability());
            markers.add(marker);
        }

        logger.info("adding '" + CHANGE_POINTS + "' to return values");
        arguments.addResult(CHANGE_POINTS, markers);
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
