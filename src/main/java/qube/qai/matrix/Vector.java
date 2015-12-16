package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import qube.qai.data.TimeSeries;
import qube.qai.network.neural.function.ActivationFunction;

import java.util.List;

/**
 * Created by rainbird on 11/23/15.
 */
public class Vector extends Matrix {

    public Vector() {
    }

    public Vector(BasicMatrix matrix) {
        super(matrix);
    }

    public static Vector buildFromTimeSeries(TimeSeries timeSeries) {

        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix column = factory.columns(timeSeries.toArray());
        Vector vector = new Vector(column);

        return vector;
    }

    public static Vector buildFromList(List<? extends Number> list) {
        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix matrix = factory.columns(list);
        return new Vector(matrix);
    }

    public static Vector buildFromArray(double[] array) {

        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix column = factory.columns(array);
        Vector vector = new Vector(column);

        return vector;
    }



    @Override
    public Vector transpose() {
        BasicMatrix result = matrix.transpose();
        Vector newInstance = new Vector(result);
        return newInstance;
    }

    @Override
    public Vector negate() {
        BasicMatrix result = matrix.negate();
        Vector newInstance = new Vector(result);
        return newInstance;
    }

    @Override
    public Vector add(Matrix input) {
        BasicMatrix result = matrix.add(input.getMatrix());
        Vector newInstance = new Vector(result);
        return newInstance;
    }

    @Override
    public Vector multiplyElements(Matrix input) {
        BasicMatrix result = matrix.multiplyElements(input.getMatrix());
        Vector newInstance = new Vector(result);
        return newInstance;
    }

    @Override
    public Vector multiply(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiply(QaiMatrix.getMatrix());
        Vector newInstance = new Vector(result);
        return newInstance;
    }

    @Override
    public Vector multiplyLeft(Matrix QaiMatrix) {
        BasicMatrix result = matrix.multiplyLeft(QaiMatrix.getMatrix());
        Vector newInstance = new Vector(result);
        return newInstance;
    }

    @Override
    public Vector modify(ActivationFunction function) {
        BasicMatrix result =  matrix.modify(function);
        Vector newInstance = new Vector(result);
        return newInstance;
    }

    @Override
    public String toString() {
        boolean areChildren = false;
        StringBuffer buffer = new StringBuffer("(");

        for (Number number : getElementsAsList()) {
            buffer.append(number);
            buffer.append(", ");
            areChildren = true;
        }

        if (areChildren) {
            buffer.deleteCharAt(buffer.length()-1);
            buffer.deleteCharAt(buffer.length()-1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public BasicMatrix getMatrix() {
        return super.getMatrix();
    }

    @Override
    public void setMatrix(BasicMatrix matrix) {
        super.setMatrix(matrix);
    }
}
