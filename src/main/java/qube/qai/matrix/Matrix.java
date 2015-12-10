package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.random.Normal;
import qube.qai.data.MetricTyped;
import qube.qai.data.Metrics;
import qube.qai.network.neural.function.ActivationFunction;

/**
 * Created by rainbird on 11/22/15.
 * base class for the
 */
public class Matrix implements MetricTyped {

    /**
     * matrix nodes are mainly for wrapping whatever matrix-library is to be used
     * at this point the decision seems to be in favor of ojAlgo
     */

    private BasicMatrix matrix;

    public Matrix() {
    }

    public Matrix(BasicMatrix matrix) {
        this.matrix = matrix;
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

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix transpose() {
        BasicMatrix result = matrix.transpose();
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix negate() {
        BasicMatrix result = this.matrix.negate();
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix add(Matrix input) {
        BasicMatrix result = this.matrix.add(input.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix multiplyElements(Matrix input) {
        BasicMatrix result = this.matrix.multiplyElements(input.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix multiplyRight(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiply(QaiMatrix.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix multiplyLeft(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiplyLeft(QaiMatrix.getMatrix());
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix modify(ActivationFunction function) {
        BasicMatrix result = matrix.modify(function);
        Matrix newInstance = new Matrix(result);
        return newInstance;
    }

    public BasicMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(BasicMatrix matrix) {
        this.matrix = matrix;
    }
}
