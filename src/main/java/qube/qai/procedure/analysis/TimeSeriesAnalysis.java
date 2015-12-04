package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/28/15.
 */
public class TimeSeriesAnalysis extends ProcedureChain {

    private static String NAME = "Time-Series Analysis";



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

    }
}
