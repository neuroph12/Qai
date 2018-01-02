/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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
import qube.qai.data.analysis.Statistics;
import qube.qai.matrix.Matrix;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;
import qube.qai.procedure.nodes.ValueNode;

import java.util.List;

/**
 * Created by rainbird on 11/28/15.
 */
public class MatrixStatistics extends Procedure implements ProcedureConstants {

    public static String NAME = "Matrix Statistics";

    public static String DESCRIPTION = "Analyses the distribution of the numbers in the matrix, " +
            "and in its eigen-vectors, as far as they exist, using eigen-value decomposition";

    private Matrix matrix;

    private Metrics dataMetrics;

    private Metrics matrixMetrics;

    /**
     * Runs statistical analysis on the given matrix
     */
    public MatrixStatistics() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode<Matrix>(INPUT_MATRIX, MIMETYPE_MATRIX) {
            @Override
            public void setValue(Matrix value) {
                super.setValue(value);
                matrix = value;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Metrics>(MATRIX_METRICS, MIMETYPE_METRICS) {
            @Override
            public Metrics getValue() {
                return matrixMetrics;
            }
        });
        getProcedureDescription().getProcedureResults().addResult(new ValueNode<Metrics>(MATRIX_DATA_METRICS, MIMETYPE_METRICS) {
            @Override
            public Metrics getValue() {
                return dataMetrics;
            }
        });
    }

    @Override
    public void execute() {

        if (matrix == null || matrix.getMatrix() == null) {
            error("Input matrix has not been initialized properly: null value");
            return;
        }

        List elements = matrix.getMatrix().toListOfElements();
        Statistics stats = new Statistics(elements.toArray());
        dataMetrics = stats.buildMetrics();
        matrixMetrics = matrix.buildMetrics();

        info("adding '" + MATRIX_METRICS + "' and '" + MATRIX_DATA_METRICS + "' to return values");
    }

    @Override
    public Procedure createInstance() {
        return new MatrixStatistics();
    }

}
