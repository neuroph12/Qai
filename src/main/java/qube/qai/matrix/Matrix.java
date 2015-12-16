package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.random.Normal;
import qube.qai.data.MetricTyped;
import qube.qai.data.Metrics;
import qube.qai.network.neural.function.ActivationFunction;

import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 11/22/15.
 * base class for the
 */
public class Matrix implements MetricTyped {

    /**
     * matrix nodes are mainly for wrapping whatever matrix-library is to be used
     * at this point the decision seems to be in favor of ojAlgo
     */

    protected BasicMatrix matrix;

    public Matrix() {
    }

    public Matrix(BasicMatrix matrix) {
        this.matrix = matrix;
    }

    public List<? extends Number> getElementsAsList() {
        if (matrix == null) {
            return null;
        }
        return matrix.toListOfElements();
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

    public static Matrix buildFromArray(double[][] array) {
        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix columns = factory.columns(array);
        Matrix matrix = new Matrix(columns);
        return matrix;
    }

    public List<? extends Number> toListOfElements() {
        if (matrix != null) {
            return matrix.toListOfElements();
        }
        return null;
    }

    /**
     * @return
     */
    public Matrix transpose() {
        BasicMatrix result = matrix.transpose();
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @return
     */
    public Matrix negate() {
        BasicMatrix result = this.matrix.negate();
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @return
     */
    public Matrix add(Matrix input) {
        BasicMatrix result = this.matrix.add(input.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @return
     */
    public Matrix multiplyElements(Matrix input) {
        BasicMatrix result = this.matrix.multiplyElements(input.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @return
     */
    public Matrix multiply(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiply(QaiMatrix.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @return
     */
    public Matrix multiplyLeft(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiplyLeft(QaiMatrix.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @return
     */
    public Matrix modify(ActivationFunction function) {
        BasicMatrix result = matrix.modify(function);
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    @Override
    public String toString() {
        if (matrix != null) {
            return matrix.toString();
        }
        return super.toString();
    }

    public BasicMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(BasicMatrix matrix) {
        this.matrix = matrix;
    }
}
