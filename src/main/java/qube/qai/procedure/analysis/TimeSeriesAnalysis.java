package qube.qai.procedure.analysis;

import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/28/15.
 */
public class TimeSeriesAnalysis extends ProcedureChain {


    /**
     * this is a procedure to analyze a given time series
     * statistical:
     *          average value
     *          result value variance etc.
     * top 10/bottom 10/average entities- prepare those results as charts
     */
    public TimeSeriesAnalysis(String name) {
        super(name);
    }

    @Override
    public void run() {

    }
}
