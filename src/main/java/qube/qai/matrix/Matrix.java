package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import qube.qai.network.neural.function.ActivationFunction;

/**
 * Created by rainbird on 11/22/15.
 * base class for the
 */
public class Matrix {

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

    public Matrix newInstance() {
        return new Matrix();
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix transpose() {
        BasicMatrix result = matrix.transpose();
        Matrix newInstance = newInstance();
        newInstance.setMatrix(result);

        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix negate() {
        BasicMatrix result = this.matrix.negate();
        Matrix newInstance = newInstance();
        newInstance.setMatrix(result);

        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix add(Matrix input) {
        BasicMatrix result = this.matrix.add(input.getMatrix());
        Matrix newInstance = newInstance();
        newInstance.setMatrix(result);

        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix multiplyElements(Matrix input) {
        BasicMatrix result = this.matrix.multiplyElements(input.getMatrix());
        Matrix newInstance = newInstance();
        newInstance.setMatrix(result);

        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix multiplyRight(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiply(QaiMatrix.getMatrix());
        Matrix newInstance = newInstance();
        newInstance.setMatrix(result);

        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix multiplyLeft(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiplyLeft(QaiMatrix.getMatrix());
        Matrix newInstance = newInstance();
        newInstance.setMatrix(result);

        return newInstance;
    }

    /**
     * @TODO consider method's return value
     * @return
     */
    public Matrix modify(ActivationFunction function) {
        BasicMatrix result = matrix.modify(function);
        Matrix newInstance = newInstance();
        newInstance.setMatrix(result);

        return newInstance;
    }

    public BasicMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(BasicMatrix matrix) {
        this.matrix = matrix;
    }
}
