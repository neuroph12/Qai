package qube.qai.network;

import com.fasterxml.uuid.Generators;
import junit.framework.TestCase;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.network.neural.function.LogitFunction;
import qube.qai.network.neural.function.SigmoidFunction;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by rainbird on 11/23/15.
 */
public class TestNeuralNetwork extends TestCase {

    private int EPOCH = 10000;

    protected boolean debug = true;

    @Override
    protected void setUp() throws Exception {
        //super.setUp();
    }

    public void testPropagate() throws Exception {

        // we first make a NeuralNetworkNode
        NeuralNetwork ann = new NeuralNetwork();
        ann.setActivationFunction(new SigmoidFunction());
        ann.setInverseFunction(new LogitFunction());
        // create the bias and weight matrices
        double[][] valuesA = {{0.1}, {0.1}, {0.2}};
        double[][] valuesB = {{0.1,0.1,-0.1}, {0.1,0.2,0.1}, {0.2,-0.1,0.6}};

        BasicMatrix a = PrimitiveMatrix.FACTORY.columns(valuesA);
        BasicMatrix b = PrimitiveMatrix.FACTORY.columns(valuesB);

        BasicMatrix c = b.multiplyLeft(a);

        //log(c.toString());

        Vector bias = new Vector();
        bias.setMatrix(a);
        ann.setBias(bias);

        Matrix weights = new Matrix();
        weights.setMatrix(b);
        ann.setWeights(weights);

        double[][] in = {{311},{189},{273}};
        BasicMatrix d = PrimitiveMatrix.FACTORY.columns(in);
        Vector input = new Vector();
        input.setMatrix(d);
        Vector result = ann.propagate(input);

        log(result.getMatrix().toString());
    }

    /**
     * this is for running the training routine
     * with something easy- XOR is pretty much easiest what you get
     */
    public void testXOR() throws Exception {
//        double[][] inputPatterns  = {{1,1}, {1,0}, {0,1}, {0,0}};
//        double[][] outputPatterns = {{0,0}, {1,0}, {1,0}, {0,0}};

        ArrayList<Double[][]> inputPatterns = new ArrayList<Double[][]>();
        ArrayList<Double[][]> outputPatterns = new ArrayList<Double[][]>();

        inputPatterns.add(new Double[][]{{1.0},{1.0}});
        inputPatterns.add(new Double[][]{{1.0},{0.0}});
        inputPatterns.add(new Double[][]{{0.0},{1.0}});
        inputPatterns.add(new Double[][]{{0.0},{0.0}});

        outputPatterns.add(new Double[][]{{0.0},{0.0}});
        outputPatterns.add(new Double[][]{{1.0},{0.0}});
        outputPatterns.add(new Double[][]{{1.0},{0.0}});
        outputPatterns.add(new Double[][]{{0.0},{0.0}});

        // we first make a NeuralNetworkNode
        NeuralNetwork ann = new NeuralNetwork();
        ann.setActivationFunction(new SigmoidFunction());
        ann.setInverseFunction(new LogitFunction());
        // create the bias and weight matrices
        double[][] valuesA = {{0.1},{0.1}};
        double[][] valuesB = {{0.1,0.1}, {0.1,0.1}};

        BasicMatrix a = PrimitiveMatrix.FACTORY.columns(valuesA);
        BasicMatrix b = PrimitiveMatrix.FACTORY.columns(valuesB);

        Vector bias = new Vector();
        bias.setMatrix(a);
        ann.setBias(bias);

        Matrix weights = new Matrix();
        weights.setMatrix(b);
        ann.setWeights(weights);

        Vector input = new Vector();
        Vector target = new Vector();
        for (int i = 0; i < EPOCH; i++) {
            for (int j = 0; j < inputPatterns.size(); j++) {
                input.setMatrix(PrimitiveMatrix.FACTORY.columns(inputPatterns.get(j)));
                target.setMatrix(PrimitiveMatrix.FACTORY.columns(outputPatterns.get(j)));
                //ann.trainNetwork(input, target);
            }
            log("pass: " + i + " error: " + ann.getError().getMatrix().toString());
        }
    }

    /*
    depending on how you generate the UUID you have different fields which are accessible
    this test is in order to demonstrate the differences
     */
    public void testUUIDGenerators() throws Exception {

        int numerOfUUIDsToGenerate = 1;

        log("time based UUIDS:");
        for (int i = 0; i < numerOfUUIDsToGenerate; i++) {
            UUID uuid = Generators.timeBasedGenerator().generate();
            log("Generated: " + uuid.toString());
            log("least significant bits:" + uuid.getLeastSignificantBits());
            log("most significant bits:" + uuid.getMostSignificantBits());
            log("timestamp: " + uuid.timestamp());
            log("clock sequence:" + uuid.clockSequence());
            log("node: " + uuid.node());
            log("version: " + uuid.version());
            log("variant: " + uuid.variant());
        }

        log("name based UUIDS:");
        for (int i = 0; i < numerOfUUIDsToGenerate; i++) {
            String dummyName = "a name @" + i; // + "@" + System.currentTimeMillis();
            UUID uuid = Generators.nameBasedGenerator().generate(dummyName);
            log("name used for generation: " + dummyName);
            log("Generated: " + uuid.toString());
            log("least significant bits:" + uuid.getLeastSignificantBits());
            log("most significant bits:" + uuid.getMostSignificantBits());
            log("version: " + uuid.version());
            log("variant: " + uuid.variant());
            // in name based UUID these fields are not accessible
            //log("timestamp: " + uuid.timestamp());
            //log("clock sequence:" + uuid.clockSequence());
            //log("node: " + uuid.node());
        }
    }

    protected void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}