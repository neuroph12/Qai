package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
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

    public Matrix() {
    }

    public Matrix(BasicMatrix matrix) {
        rows = (int) matrix.countRows();
        columns = (int) matrix.countColumns();
        values = new double[rows][columns];
        PhysicalStore<Double> primitive = matrix.toPrimitiveStore();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                values[i][j] = primitive.get(i, j);
            }

        }
        //this.matrix = matrix;
    }

    public List<? extends Number> getElementsAsList() {
        if (values == null) {
            return null;
        }
        return matrix().toListOfElements();
    }

    /**
     * returns the contents of the matrix as a double-array
     * @return
     */
    public double[] toArray() {
        List<? extends Number> elements = toListOfElements();
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
        metrics.putValue("infinity norm", matrix.getInfinityNorm().doubleValue());
        metrics.putValue("eigenvalues", matrix.getEigenvalues());

        return metrics;
    }

    /**
     * convenience method for creating simple matrices
     * if option filled is true, a normally distributed sample will be generated
     * if option filled is false, a matrix with all zero elements will be generated
     * @param filled
     * @param rows
     * @param columns
     * @return
     */
    public static Matrix createMatrix(boolean filled, int rows, int columns) {

        BasicMatrix basicMatrix;
        if (filled) {
            basicMatrix = PrimitiveMatrix.FACTORY.makeFilled(rows, columns, new Normal(0.5, 10));
        } else {
            basicMatrix = PrimitiveMatrix.FACTORY.makeZero(rows, columns);
        }
        Matrix matrix = new Matrix(basicMatrix);

        return matrix;
    }

    public static Matrix buildFromList(List<? extends Number> list) {
        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix matrix = factory.rows(list);
        return new Matrix(matrix);
    }

    private static BasicMatrix build(double[][] array) {
        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        return factory.columns(array);
    }

    public static Matrix buildFromArray(double[][] array) {
        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix columns = build(array);
        Matrix matrix = new Matrix(columns);
        return matrix;
    }

    public List<? extends Number> toListOfElements() {
        if (values != null) {
            return matrix().toListOfElements();
        }
        return null;
    }

    /**
     * @return
     */
    public Matrix transpose() {
        BasicMatrix result = matrix().transpose();
        return new Matrix(result);
    }

    /**
     * @return
     */
    public Matrix negate() {
        BasicMatrix result = matrix().negate();
        return new Matrix(result);
    }

    /**
     * @return
     */
    public Matrix add(Matrix input) {
        BasicMatrix result = matrix().add(input.getMatrix());
        return new Matrix(result);
    }

    /**
     * @return
     */
    public Matrix multiplyElements(Matrix input) {
        BasicMatrix result = matrix().multiplyElements(input.getMatrix());
        return new Matrix(result);
    }

    /**
     * @return
     */
    public Matrix multiply(Matrix QaiMatrix) {
        BasicMatrix result = matrix().multiply(QaiMatrix.getMatrix());
        return new Matrix(result);
    }

    /**
     * @return
     */
    public Matrix multiplyLeft(Matrix QaiMatrix) {
        BasicMatrix result = matrix().multiplyLeft(QaiMatrix.getMatrix());
        return new Matrix(result);
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

//    public void setMatrix(BasicMatrix matrix) {
//        this.matrix = matrix;
//    }
}
