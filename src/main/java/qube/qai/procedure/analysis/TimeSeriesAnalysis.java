package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.analysis.Statistics;
import qube.qai.data.TimeSeries;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/28/15.
 */
public class TimeSeriesAnalysis extends ProcedureChain {

    public static String NAME = "Time-Series Analysis";

    private static String DESCRIPTION = "This is a procedure to analyze a given time series.";
    /**
     * this is a procedure to analyze a given time series
     * statistical:
     *          average value
     *          result value variance etc.
     * top 10/bottom 10/average entities- prepare those results as charts
     */
    public TimeSeriesAnalysis() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_TIME_SERIES);
        arguments.putResultNames(TIME_SERIES_METRICS);
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

        Number[] data = timeSeries.toArray();
        Statistics stats = new Statistics(data);
        Metrics metrics = stats.buildMetrics();
        log("adding '" + TIME_SERIES_METRICS + "' to return values");
        arguments.addResult(TIME_SERIES_METRICS, metrics);
    }
}
