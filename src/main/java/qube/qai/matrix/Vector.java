package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import qube.qai.data.TimeSequence;

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

    /*@Override
    public double[] toArray() {
        return values[columns];
    }*/

    public static Vector buildFromTimeSeries(TimeSequence timeSequence) {

        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix matrix = factory.columns(timeSequence.toArray());
        Vector vector = new Vector(matrix);

        return vector;
    }

    public static Vector buildFromList(List<? extends Number> list) {
        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix matrix = factory.columns(list);
        return new Vector(matrix);
    }

    public static Vector buildFromArray(double[] array) {

        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        BasicMatrix matrix = factory.columns(array);
        Vector vector = new Vector(matrix);

        return vector;
    }

    private BasicMatrix vector() {
        BasicMatrix.Factory<PrimitiveMatrix> factory = PrimitiveMatrix.FACTORY;
        return factory.rows(values);
    }

    @Override
    public Vector transpose() {
        BasicMatrix result = vector().transpose();
        return new Vector(result);
    }

    @Override
    public Vector negate() {
        BasicMatrix result = vector().negate();
        return new Vector(result);
    }

    @Override
    public Vector add(Matrix input) {
        BasicMatrix result = vector().add(input.getMatrix());
        return new Vector(result);
    }

    @Override
    public Vector multiplyElements(Matrix input) {
        BasicMatrix result = vector().multiplyElements(input.getMatrix());
        return new Vector(result);
    }

    @Override
    public Vector multiply(Matrix matrix) {
        BasicMatrix result = vector().multiply(matrix.getMatrix());
        return new Vector(result);
    }

    @Override
    public Vector multiplyLeft(Matrix matrix) {
        BasicMatrix result = vector().multiplyLeft(matrix.getMatrix());
        return new Vector(result);
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

}
