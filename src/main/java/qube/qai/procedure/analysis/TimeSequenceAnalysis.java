package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.TimeSequence;
import qube.qai.data.analysis.Statistics;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureDecorator;

/**
 * Created by rainbird on 11/28/15.
 */
public class TimeSequenceAnalysis extends ProcedureDecorator {

    public static String NAME = "Time-Sequence Analysis";

    private static String DESCRIPTION = "This is a procedure to analyze a given time series.";

    /**
     * this is a procedure to analyze a given time series
     * statistical:
     * average value
     * result value variance etc.
     * top 10/bottom 10/average entities- prepare those results as charts
     */
    public TimeSequenceAnalysis(Procedure procedure) {
        super(NAME, procedure);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_TIME_SEQUENCE);
        arguments.putResultNames(TIME_SEQUENCE_METRICS);
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

        Number[] data = timeSequence.toArray();
        Statistics stats = new Statistics(data);
        Metrics metrics = stats.buildMetrics();
        logger.info("adding '" + TIME_SEQUENCE_METRICS + "' to return values");
        arguments.addResult(TIME_SEQUENCE_METRICS, metrics);
    }
}
