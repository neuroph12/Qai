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

import qube.qai.data.Metrics;
import qube.qai.data.TimeSequence;
import qube.qai.data.analysis.Statistics;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.nodes.ValueNode;

/**
 * Created by rainbird on 11/28/15.
 */
public class TimeSequenceAnalysis extends Procedure implements ProcedureConstants {

    public static String NAME = "Time-Sequence Analysis";

    private static String DESCRIPTION = "This is a procedure to analyze a given time series.";

    private TimeSequence timeSequence;

    private Metrics metrics;

    /**
     * this is a procedure to analyze a given time series
     * statistical:
     * average value
     * result value variance etc.
     * top 10/bottom 10/average entities- prepare those results as charts
     */
    public TimeSequenceAnalysis() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<TimeSequence>(INPUT_TIME_SEQUENCE, MIMETYPE_TIME_SERIES) {
            @Override
            public void setValue(TimeSequence value) {
                super.setValue(value);
                timeSequence = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Metrics>(TIME_SEQUENCE_METRICS, MIMETYPE_METRICS) {
            @Override
            public Metrics getValue() {
                return metrics;
            }
        });
    }

    @Override
    public void execute() {

        executeInputProcedures();

        if (timeSequence == null) {
            error("Input time-series has not been initialized properly: null value");
        }

        Number[] data = timeSequence.toArray();
        Statistics stats = new Statistics(data);
        metrics = stats.buildMetrics();
        info("Time-sequence-analysis ended with '" + TIME_SEQUENCE_METRICS);
    }
}
