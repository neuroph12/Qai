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

package qube.qai.matrix;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
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
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public BasicMatrix getMatrix() {
        return super.getMatrix();
    }

}
