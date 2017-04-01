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
import qube.qai.data.analysis.Statistics;
import qube.qai.matrix.Matrix;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;

import java.util.List;

/**
 * Created by rainbird on 11/28/15.
 */
public class MatrixStatistics extends Procedure implements ProcedureConstants {

    public static String NAME = "Matrix Statistics";

    public static String DESCRIPTION = "Analyses the distribution of the numbers in the matrix, " +
            "and eigenvalues and eigenvectors, as far as they exist, using eigenvalue decomposition";

    /**
     * Runs statistical analysis on the given matrix
     */
    public MatrixStatistics(Procedure procedure) {
        super(NAME, procedure);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_MATRIX);
        arguments.putResultNames(MATRIX_METRICS, MATRIX_DATA_METRICS);
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
        Matrix matrix = (Matrix) arguments.getSelector(INPUT_MATRIX).getData();
        if (matrix == null || matrix.getMatrix() == null) {
            logger.error("Input matrix has not been initialized properly: null value");
            return;
        }

        List elements = matrix.getMatrix().toListOfElements();
        Statistics stats = new Statistics(elements.toArray());
        Metrics dataMetrics = stats.buildMetrics();
        Metrics matrixMetrics = matrix.buildMetrics();

        logger.info("adding '" + MATRIX_METRICS + "' and '" + MATRIX_DATA_METRICS + "' to return values");
        arguments.addResult(MATRIX_DATA_METRICS, dataMetrics);
        arguments.addResult(MATRIX_METRICS, matrixMetrics);
    }

    @thewebsemantic.Id
    public String getUuid() {
        return this.uuid;
    }
}
