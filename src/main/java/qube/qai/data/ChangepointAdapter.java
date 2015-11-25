package qube.qai.data;

import istu.samsroad.Analysis;
import istu.samsroad.AnalysisSettings;
import istu.samsroad.data.DataPoint;

import java.util.Vector;

/**
 * Created by rainbird on 11/25/15.
 */
public class ChangepointAdapter {

    AnalysisSettings settings;
    Analysis analysis;

    public ChangepointAdapter() {
        settings = new AnalysisSettings();
    }

    // this is the part which we really need after all
    public Vector<DataPoint> collectChangePoints(double[][] data) {

        analysis = new Analysis(data, settings);

        analysis.runAnalysis();
        Vector<DataPoint> changePoints = analysis.getChangePoints();

        return analysis.getChangePoints();
    }

    public AnalysisSettings getSettings() {
        return settings;
    }

    public void setSettings(AnalysisSettings settings) {
        this.settings = settings;
    }

}
