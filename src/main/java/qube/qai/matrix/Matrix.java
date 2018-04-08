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

package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.random.Normal;
import qube.qai.data.MetricTyped;
import qube.qai.data.Metrics;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rainbird on 11/22/15.
 * base class for the
 */
public class Matrix implements Serializable, MetricTyped {

    /**
     * matrix nodes are mainly for wrapping whatever matrix-library is to be used
     * at this point the decision seems to be in favor of ojAlgo
     */
    protected int rows;

    protected int columns;

    protected double[][] values;

    private static BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;

    public Matrix() {
    }

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.values = new double[rows][columns];
    }

    public Matrix(BasicMatrix matrix) {
        rows = (int) matrix.countRows();
        columns = (int) matrix.countColumns();
        values = new double[rows][columns];
        // @TODO encog upgrade. take a look and correct
        /*PhysicalStore<Double> primitive = matrix.toPrimitiveStore();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                values[i][j] = primitive.get(i, j);
            }

        }*/
        //this.matrix = matrix;
    }

    public double getValueAt(int row, int column) {
        return values[row][column];
    }

    public void setValueAt(int row, int column, double value) {
        values[row][column] = value;
    }

    public List<? extends Number> getElementsAsList() {
        if (values == null) {
            return null;
        }
        // @TODO encog updade related take a look
        return null; //matrix().toListOfElements();
    }

    /**
     * returns the contents of the matrix as a double-array
     *
     * @return
     */
    public double[] toArray() {
        // @TODO encog updade related take a look
        List<? extends Number> elements = null; //matrix().toListOfElements();
        if (elements == null) {
            return null;
        }

        double[] array = new double[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            Number number = elements.get(i);
            array[i] = number.doubleValue();
        }

        return array;
    }

    /**
     * build metric for the matrix
     */

    public Metrics buildMetrics() {

        BasicMatrix matrix = matrix();
        Metrics metrics = new Metrics();
        metrics.putValue("rank", matrix.getRank());
        metrics.putValue("rows", matrix.countRows());
        metrics.putValue("columns", matrix.countColumns());
        metrics.putValue("determinant", matrix.getDeterminant().doubleValue());
        // @TODO encog updade related take a look
        //metrics.putValue("infinity norm", matrix.getInfinityNorm().doubleValue());
        metrics.putValue("eigenvalues", matrix.getEigenvalues());

        return metrics;
    }

    /**
     * convenience method for creating simple matrices
     * if option filled is true, a normally distributed sample will be generated
     * if option filled is false, a matrix with all zero elements will be generated
     *
     * @param filled
     * @param rows
     * @param columns
     * @return
     */
    public static Matrix createMatrix(boolean filled, int rows, int columns) {

        BasicMatrix basicMatrix;
        if (filled) {
            basicMatrix = factory.makeFilled(rows, columns, new Normal(0.5, 10));
        } else {
            basicMatrix = factory.makeZero(rows, columns);
        }
        return new Matrix(basicMatrix);
    }

    public static Matrix buildFromList(List<? extends Number> list) {
        BasicMatrix matrix = factory.columns(list);
        return new Matrix(matrix);
    }

    private static BasicMatrix build(double[][] array) {
        return factory.columns(array);
    }

    public static Matrix buildFromArray(double[][] array) {
        return new Matrix(build(array));
    }

    public List<? extends Number> toListOfElements() {
        if (values != null) {
            // @TODO encog updade related take a look
            return null; //matrix().toListOfElements();
        }
        return null;
    }

    @Override
    public String toString() {
        if (values != null) {
            return matrix().toString();
        }
        return super.toString();
    }

    private BasicMatrix matrix() {
        return build(values);
    }

    public BasicMatrix getMatrix() {
        return matrix();
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}
