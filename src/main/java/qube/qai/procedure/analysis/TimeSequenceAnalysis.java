/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
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

import qube.qai.data.Arguments;
import qube.qai.data.Metrics;
import qube.qai.data.TimeSequence;
import qube.qai.data.analysis.Statistics;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;

/**
 * Created by rainbird on 11/28/15.
 */
public class TimeSequenceAnalysis extends Procedure implements ProcedureConstants {

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

        if (getFirstChild() != null) {
            ((Procedure) getFirstChild()).execute();
        }

        if (!arguments.isSatisfied()) {
            arguments = arguments.mergeArguments(((Procedure) getFirstChild()).getArguments());
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
