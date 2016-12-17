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
