package qube.qai.matrix;

import junit.framework.TestCase;
import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.ArrayAnyD;
import org.ojalgo.finance.data.YahooSymbol;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.machine.JavaType;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.ComplexMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.random.Uniform;
import org.ojalgo.random.Weibull;
import org.ojalgo.type.CalendarDateUnit;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.ojalgo.constant.PrimitiveMath.PI;
import static org.ojalgo.constant.PrimitiveMath.ZERO;
import static org.ojalgo.function.PrimitiveFunction.DIVIDE;
import static org.ojalgo.function.PrimitiveFunction.SUBTRACT;

/**
 * Created by rainbird on 11/22/15.
 */
public class TestMatrix extends TestCase {

    private boolean debug = true;

   /* public void restOjAlgoFinance() throws Exception {

        YahooSymbol goog = new YahooSymbol("GOOG");

        File file = new File("test/goog.array");
        log("copying and printing the obtained data list of GOOG:");

        List<YahooSymbol.Data> dataList = goog.getHistoricalPrices();
        Array2D<Double> fileArray = BufferArray.make(file, dataList.size(), 2);
        int count = 0;
        for (YahooSymbol.Data data : dataList) {

            long millis = data.getKey().toTimeInMillis(CalendarDateUnit.CENTURY);
            String dataContent = "close: " + data.close + " adjustedClose: " + data.adjustedClose
                    + " high: " + data.high + " low: " + data.low + " volume: " + data.volume;
            log(dataContent);
            fileArray.set(count, 0, millis);
            fileArray.set(count, 1, data.getValue());
            count++;
        }

        // in order to be able to save the whole thing to a buffer-array, you would have
        // to convert the date values to long first and save [double][double]
        log("data saved on file-system:");
        for (int i = 0; i < fileArray.countRows(); i++) {
            double date = fileArray.get(i, 0);
            double value = fileArray.get(i, 1);
            String dateString = new Date((long)date).toString();
            log("value: " + value + " on: " + dateString);
        }
    }*/

    /*public void restArrayThings() throws Exception {
        BasicLogger.debug();
        BasicLogger.debug("ArrayBasics");
        BasicLogger.debug(OjAlgoUtils.getTitle());
        BasicLogger.debug(OjAlgoUtils.getDate());
        BasicLogger.debug();

        // The file pathname - previously existing or not
        final File tmpFile = new File("BasicDemo.array");

        final long tmpJvmMemory = OjAlgoUtils.ENVIRONMENT.memory;
        BasicLogger.debug("The JVM was started with a max heap size of {}MB", (tmpJvmMemory / 1024L) / 1024L);

        final long tmpMaxDimension = (long) Math.sqrt(tmpJvmMemory / JavaType.DOUBLE.memory());
        BasicLogger.debug("The maximum number of rows and columns: {}", tmpMaxDimension);
        // Disregarding any overhead and all other objects

        // A max sized 2-dimensional file based array
        final Array2D<Double> tmpArray2D = BufferArray.make(tmpFile, tmpMaxDimension, tmpMaxDimension);

        // An equally sized multi/any-dimensional array, based on the same file
        final ArrayAnyD<Double> tmpArrayAnyD = BufferArray.make(tmpFile, tmpMaxDimension, tmpMaxDimension, 1L, 1L);
        // An any-dimensional array can of course be 1- or 2-dimensional. In this case we instantiated a 4-dimensional
        // array, but the size of the 3:d and 4:th dimensions are just 1. Effectively this is just a 2-dimensioanl array.

        // Fill the entire array/file with zeros
        tmpArrayAnyD.fillAll(ZERO);

        BasicLogger.debug("Number of elements in...");
        BasicLogger.debug("\t2D: {}", tmpArray2D.count());
        BasicLogger.debug("\tAnyD: {}", tmpArrayAnyD.count());

        final AggregatorFunction<Double> tmpCardinality = PrimitiveAggregator.getSet().cardinality();

        final long tmpRowIndex = Uniform.randomInteger(tmpMaxDimension);
        final long tmpColumnIndex = Uniform.randomInteger(tmpMaxDimension);

        // Using the arbitrary dimensinal interface/facade we will update an entire row (all columns) of the first matrix of the first cube...
        long[] tmpReferenceToFirstElement = new long[] { tmpRowIndex, 0L, 0L, 0L };
        int tmpDimension = 1; // That's the column-dimension
        tmpArrayAnyD.fillSet(tmpReferenceToFirstElement, tmpDimension, PI);

        // Using the arbitrary dimensional interface/facade we will update an entire row (all columns) of the first matrix of the first cube...
        tmpReferenceToFirstElement = new long[] { 0L, tmpColumnIndex, 0L, 0L };
        tmpDimension = 0; // That's the row-dimension
        tmpArrayAnyD.fillSet(tmpReferenceToFirstElement, tmpDimension, PI);

        // So far we've been writing to the array-file using ArrayAnyD
        // Now we'll switch to using Array2D, but they're both mapped to the same files

        tmpCardinality.reset();
        tmpArray2D.visitRow(tmpRowIndex, 0L, tmpCardinality);
        BasicLogger.debug("Number of nonzero elements in row {}: {}", tmpRowIndex, tmpCardinality.intValue());

        tmpCardinality.reset();
        tmpArray2D.visitColumn(0L, tmpColumnIndex, tmpCardinality);
        BasicLogger.debug("Number of nonzero elements in column {}: {}", tmpColumnIndex, tmpCardinality.intValue());

        tmpCardinality.reset();
        tmpArray2D.visitAll(tmpCardinality);
        BasicLogger.debug("Number of nonzero elements in the 2D array: {}", tmpCardinality.intValue());

        BasicLogger.debug("Divide the elements of row {} to create 1.0:s", tmpRowIndex);
        tmpArray2D.modifyRow(tmpRowIndex, 0L, DIVIDE.second(PI));
        BasicLogger.debug("Subtract from the elements of column {} to create 0.0:s", tmpColumnIndex);
        tmpArray2D.modifyColumn(0L, tmpColumnIndex, SUBTRACT.second(PI));
        BasicLogger.debug("Explictly set the intersection element to 0.0 using the arbitrary-dimensional array.");
        tmpArrayAnyD.set(new long[] { tmpRowIndex, tmpColumnIndex }, ZERO);

        final AggregatorFunction<Double> tmpSum = PrimitiveAggregator.getSet().sum();
        BasicLogger.debug("Expected sum of all elements: {}", tmpMaxDimension - 1L);
        tmpSum.reset();
        tmpArray2D.visitAll(tmpSum);
        BasicLogger.debug("Actual sum of all elements: {}", tmpSum.intValue());

    }*/

   /* public void restOjAlgoDietProblem() throws Exception {
        BasicLogger.debug();
        BasicLogger.debug("TheDietProblem");
        BasicLogger.debug(OjAlgoUtils.getTitle());
        BasicLogger.debug(OjAlgoUtils.getDate());
        BasicLogger.debug();

        // Create variables expressing servings of each of the considered foods
        // Set lower and upper limits on the number of servings as well as the weight (cost of a
        // serving) for each variable.
        final Variable tmpBread = Variable.make("Bread").lower(0).upper(10).weight(0.05);
        final Variable tmpCorn = Variable.make("Corn").lower(0).upper(10).weight(0.18);
        final Variable tmpMilk = Variable.make("Milk").lower(0).upper(10).weight(0.23);

        // Create a model and add the variables to it.
        final ExpressionsBasedModel tmpModel = new ExpressionsBasedModel();
        tmpModel.addVariable(tmpBread);
        tmpModel.addVariable(tmpCorn);
        tmpModel.addVariable(tmpMilk);

        // Create a vitamin A constraint.
        // Set lower and upper limits and then specify how much vitamin A a serving of each of the
        // foods contain.
        final Expression tmpVitaminA = tmpModel.addExpression("Vitamin A").lower(5000).upper(50000);
        tmpVitaminA.setLinearFactor(tmpBread, 0);
        tmpVitaminA.setLinearFactor(tmpCorn, 107);
        tmpVitaminA.setLinearFactor(tmpMilk, 500);

        // Create a calories constraint...
        final Expression tmpCalories = tmpModel.addExpression("Calories").lower(2000).upper(2250);
        tmpCalories.setLinearFactor(tmpBread, 65);
        tmpCalories.setLinearFactor(tmpCorn, 72);
        tmpCalories.setLinearFactor(tmpMilk, 121);

        // Solve the problem - minimise the cost
        Optimisation.Result tmpResult = tmpModel.minimise();

        // Print the result
        BasicLogger.debug();
        BasicLogger.debug(tmpResult);
        BasicLogger.debug();

        // Modify the model to require an integer valued solution.
        BasicLogger.debug("Adding integer constraints...");
        tmpBread.integer(true);
        tmpCorn.integer(true);
        tmpMilk.integer(true);

        // Solve again
        tmpResult = tmpModel.minimise();

        // Print the result, and the model
        BasicLogger.debug();
        BasicLogger.debug(tmpResult);
        BasicLogger.debug();
        BasicLogger.debug(tmpModel);
        BasicLogger.debug();

    }*/

   public void testBasicMatrixUsage() throws Exception {
        final BasicMatrix.Factory<PrimitiveMatrix> tmpFactory = PrimitiveMatrix.FACTORY;
        // A MatrixFactory has 13 different methods that return BasicMatrix instances.

        final BasicMatrix tmpA = tmpFactory.makeEye(5000, 300);
        // Internally this creates an "eye-structure" - not a large array...
        final BasicMatrix tmpB = tmpFactory.makeFilled(300, 2, new Weibull(5.0, 2.0));
        // When you create a matrix with random elements you can specify their distribution.

        final BasicMatrix tmpC = tmpB.multiplyLeft(tmpA);
        final BasicMatrix tmpD = tmpA.multiply(tmpB);
        // ojAlgo differentiates between multiplying from the left and from the right.
        // The matrices C and D will be equal, but the code executed to calculate them are different.
        // The second alternative, resulting in D, will be MUCH faster!

        final BasicMatrix tmpE = tmpA.add(1000, 19, 3.14);
        final BasicMatrix tmpF = tmpE.add(10, 270, 2.18);
        // The BasicMatrix interface does not specify a set-method for matrix elements.
        // BasicMatrix instances are immutable.
        // The add(...) method should only be used to modify a small number of elements.

        // Don't do this!!!
//        BasicMatrix tmpG = tmpFactory.makeZero(500, 500);
//        for (int j = 0; j < tmpG.countColumns(); j++) {
//            for (int i = 0; i < tmpG.countRows(); i++) {
//                tmpG = tmpG.add(i, j, 100.0 * Math.min(i, j));
//                // Note that add(..) actually adds the specified value to whatever is already there.
//                // In this case that, kind of, works since the base matrix is all zeros.
//                // Completely populating a matrix this way is a really bad idea!
//            }
//        }
        // Don't do this!!!

        final double[][] tmpData = new double[][] { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }, { 7.0, 8.0, 9.0 } };
        final BasicMatrix tmpH = tmpFactory.rows(tmpData);
        // A, perhaps, natural way to create a small matrix, but the arrays are copied.
        // You do not want to create that array just as an intermediate step towards populating your matrix.
        // Doing it this way is clumsy for larger matrices.

        final Access2D.Builder<PrimitiveMatrix> tmpBuilder = tmpFactory.getBuilder(500, 500);
        // If you want to individually set many/all elements of a larger matrix you should use the builder.
        for (int j = 0; j < 500; j++) {
            for (int i = 0; i < 500; i++) {
                tmpBuilder.set(i, j, 100.0 * Math.min(i, j));
            }
        }
        final BasicMatrix tmpI = tmpBuilder.build();
        // Now you've seen 4 of the 13 MatrixFactory methods...

        final BasicMatrix tmpJ = tmpA.mergeRows(tmpD);
        final BasicMatrix tmpK = tmpJ.selectRows(1, 10, 100, 1000);
        // Sometimes it's practical to only use the factory/builder to create

        ComplexMatrix.FACTORY.makeZero(100, 100);
    }

    /*public void restOtherMatrixThings() throws Exception {
        final PhysicalStore.Factory<Double, PrimitiveDenseStore> tmpFactory = PrimitiveDenseStore.FACTORY;

        final PrimitiveDenseStore tmpA = tmpFactory.makeEye(5000, 300);
        // A PrimitiveDenseStore is always a "full array". No smart data structures here...
        final PrimitiveDenseStore tmpB = tmpFactory.makeFilled(300, 2, new Weibull(5.0, 2.0));
        // The BasicMatrix and PhysicalStore factories are very similar. They both inherit a common interface.

        final MatrixStore<Double> tmpC = tmpB.multiplyLeft(tmpA);
        final MatrixStore<Double> tmpD = tmpA.multiply(tmpB);
        // When both matrices are PhysicalStore instances there is no major difference
        // between multiplying from the left and from the right.

        tmpA.set(1000, 19, 3.14);
        tmpA.set(10, 270, 2.18);
        // PhysicalStore instances are mutable - very mutable.
        // None of the methods defined in the PhysicalStore interface return matrices (of any type).
        // Most methods don't return anything at all... They just mutate the matrix.

        final PrimitiveDenseStore tmpE = tmpA.copy();
        final MatrixStore<Double> tmpF = tmpA.transpose();
        // If you need a copy to work with, you have to explicitly make that copy.

        final double[][] tmpData = new double[][] { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }, { 7.0, 8.0, 9.0 } };
        final PrimitiveDenseStore tmpH = tmpFactory.rows(tmpData);
        // A, perhaps, natural way to create a small matrix, but the arrays are copied.
        // Doing it this way is clumsy for larger matrices.
        // If you don't have to create that array - then don't.

        final PrimitiveDenseStore tmpI = tmpFactory.makeZero(10, 10);
        for (int j = 0; j < tmpI.getColDim(); j++) {
            for (int i = 0; i < tmpI.getRowDim(); i++) {
                tmpI.set(i, j, 100.0 * Math.min(i, j));
            }
        }

        log("the created matrix:");
        log(tmpI.toString());
//        for (int i = 0; i < tmpI.getRowDim(); i++) {
//            StringBuffer buffer = new StringBuffer();
//            for (int j = 0; j < tmpI.getColDim(); j++) {
//                buffer.append(tmpI.get(i, j) + " ");
//            }
//            log(buffer.toString());
//        }
        // Doing this is, of course, no problem at all! In many cases it's what you should do.

        final MatrixStore<Double> tmpJ = tmpA.builder().right(tmpD).build();
        final MatrixStore<Double> tmpK = tmpJ.builder().row(1, 10, 100, 1000).build();
        // Once you have a MatrixStore instance you can build on it, logically.

        final MatrixStore<Double> tmpG = tmpA.builder().right(tmpD).row(1, 10, 100, 1000).build();
        // And of course you can do it in one movement. The matrices K and G are equal.
    }*/

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
