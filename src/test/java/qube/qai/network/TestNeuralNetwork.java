package qube.qai.network;

import junit.framework.TestCase;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.matrix.Matrix;
import qube.qai.matrix.Vector;
import qube.qai.network.neural.NeuralNetwork;

/**
 * Created by rainbird on 11/23/15.
 */
public class TestNeuralNetwork extends TestCase {

    private Logger logger = LoggerFactory.getLogger("TestNeuralNetwork");

    private int EPOCH = 10000;

    protected boolean debug = true;

    @Override
    protected void setUp() throws Exception {
        //super.setUp();
    }

    /*public void restPropagate() throws Exception {

        // we first make a NeuralNetworkNode
        NeuralNetwork ann = new NeuralNetwork();
        ann.setActivationFunction(new SigmoidFunction());
        ann.setInverseFunction(new LogitFunction());
        // create the bias and weight matrices
        double[] valuesA = {1.0, 1.0, 1.0};
        double[][] valuesB = {{0.1, 0.4, 0.2},
                              {0.4, 0.1, -0.1},
                              {0.2, -0.1, 0.1}};

        BasicMatrix a = PrimitiveMatrix.FACTORY.columns(valuesA);
        BasicMatrix b = PrimitiveMatrix.FACTORY.rows(valuesB);

        BasicMatrix c = b.multiply(a);
        log("matrix c- after multiplication: " + c.toString());

        Vector bias = Vector.buildFromArray(valuesA);
        ann.setBias(bias);
        log("bias: " + bias.toString());

        Matrix weights = new Matrix();
        weights.setMatrix(b);
        log("weights: " + weights.toString());
        ann.setWeights(weights);

        double[] in = {30.11, -17.89, 273.2};
        Vector input = Vector.buildFromArray(in);
        int numberIterations = 10;
        for (int i = 1; i < numberIterations; i++) {
            Vector result = ann.propagate(input);
            log("iteration step: " + i);
            log("input: " + input.toString());
            log("result: " + result.getMatrix().toString());
            input = result;
        }

    }

    public void restActivationFunction() throws Exception {
        ActivationFunction sigmoid = new SigmoidFunction();
        ActivationFunction logit = new LogitFunction();
        ActivationFunction diffSigmoid = new DiffSigmoidFunction();

        for (int i = 0; i < 1000; i++) {
            double in = 0.01 * i;
            double sig = sigmoid.invoke(in);
            double diff = diffSigmoid.invoke(sig);
            double inverse = logit.invoke(sig);
            double delta = Math.abs(Math.abs(in) - Math.abs(inverse));
            log("number: " + in + " sigmoid: " + sig + " diff: " + diff + " logit: " + inverse + " delta: " + delta);
        }
    }*/

    /**
     * in this test the created the network to compare the results with encog
     */
    public void testNeuralNetwork() {
        // decided to use something called Encog-
        // the thing looks to be pretty complete
        double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };

        double XOR_IDEAL[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 0.0 } };

        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, false, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));
        network.getStructure().finalizeStructure();
        network.reset();

        double[] weights = {-0.07436579220019145, 0.04068370154784863, -0.6118178821788891, -1.3482376121640667};
        double[][] weightArray = {{-0.07436579220019145, 0.04068370154784863}, {-0.6118178821788891, -1.3482376121640667}};
        Matrix matrix = Matrix.buildFromArray(weightArray);
        log("matrix created: " + matrix.toString());
        NeuralNetwork neuralNetwork = new NeuralNetwork(matrix);
        // this is in order to test the method- later make this call automatic
        //neuralNetwork.buildFromAdjacencyMatrix();

        for (int i = 0; i < XOR_INPUT.length; i++) {
            Vector inVector = Vector.buildFromArray(XOR_INPUT[i]);
            Vector outVector = neuralNetwork.propagate(inVector);
            log("in vector: " + inVector);
            log("result neural-network: " + outVector);

            network.decodeFromArray(weights);
            double[] outArray = new double[2];
            network.compute(inVector.toArray(), outArray);
            log("result encog: " + arrayAsString(outArray));

            double[] out = outVector.toArray();
            for (int j = 0; j < outArray.length; j++) {
                assertTrue(out[j] == outArray[j]);
            }
        }

    }

    public void testEncog_2() throws Exception {
        // decided to use something called Encog-
        // the thing looks to be pretty complete
        double XOR_INPUT[][] = { { 0.0, 0.0, 0.0 },
                                 { 1.0, 0.0, 0.0 },
                                 { 0.0, 0.0, 1.0 },
                                 { 1.0, 1.0, 1.0 },
                                 { 1.0, 1.0, 0.0 },
                                 { 0.0, 1.0, 1.0 }};

        double XOR_IDEAL[][] = { { 0.0, 0.0, 0.0 },
                                 { 1.0, 0.0, 0.0 },
                                 { 1.0, 0.0, 0.0 },
                                 { 1.0, 0.0, 0.0 },
                                 { 0.0, 0.0, 0.0 },
                                 { 0.0, 0.0, 0.0 }};

        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, false, 3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 3));
        network.getStructure().finalizeStructure();
        network.reset();

        // create training data
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

        // train the neural network
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;

        do {
            train.iteration();
            log("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while(train.getError() > 0.2);
        train.finishTraining();

        log("trained network weights:" + network.dumpWeights());
        int arrayLength = network.encodedArrayLength();
        double[] array = new double[arrayLength];
        network.encodeToArray(array);
        int layerCount = network.getLayerCount();
        log("network layer count: " + layerCount + " weight count: " + arrayLength);
        for (int i = 0; i < array.length; i++) {
            log("element i: " + i + " value:" + array[i]);
        }

        System.out.println("Neural Network Results:");
        for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());
            log(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
    }

    private String arrayAsString(double[] array) {
        StringBuffer buffer = new StringBuffer("(");
        boolean more = false;
        for (int i = 0; i < array.length; i++) {
            buffer.append(array[i]).append(", ");
            more = true;
        }
        if (more) {
            buffer.deleteCharAt(buffer.length()-1);
            buffer.deleteCharAt(buffer.length()-1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * this is in order to make some experiments with the library encog
     */
    public void restEncog_1() throws Exception {
        // decided to use something called Encog-
        // the thing looks to be pretty complete
        double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };

        double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
        network.getStructure().finalizeStructure();
        network.reset();

        // create training data
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

        // train the neural network
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;

        do {
            train.iteration();
            log("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while(train.getError() > 0.2);
        train.finishTraining();

        log("trained network weights:" + network.dumpWeights());
        int arrayLength = network.encodedArrayLength();
        double[] array = new double[arrayLength];
        network.encodeToArray(array);
        int layerCount = network.getLayerCount();
        log("network layer count: " + layerCount + " weight count: " + arrayLength);
        for (int i = 0; i < array.length; i++) {
            log("element i: " + i + " value:" + array[i]);
        }

//        for (int layer = 0; layer < network.getLayerCount(); layer++) {
//            int neuronCount = network.getLayerNeuronCount(layer);
//            for (int i = 0; i < neuronCount; i++) {
//                for (int j = 0; j < neuronCount; j++) {
//                    double weight = network.getWeight(layer, i, j);
//                    log("layer:(" + layer + ") weight i-j: (" + i + ", " + j + ") " + weight);
//                }
//            }
//        }

        // test the neural network
        System.out.println("Neural Network Results:");
        for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());
            log(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
    }
    /**
     * this is for running the training routine
     * with something easy- XOR is pretty much easiest what you get
     */
    /*public void testXOR() throws Exception {
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
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.setActivationFunction(new SigmoidFunction());
        neuralNetwork.setInverseFunction(new LogitFunction());
        // create the bias and weight matrices
        double[][] valuesA = {{0.1},{0.1}};
        double[][] valuesB = {{0.1,0.1}, {0.1,0.1}};

        BasicMatrix a = PrimitiveMatrix.FACTORY.columns(valuesA);
        BasicMatrix b = PrimitiveMatrix.FACTORY.columns(valuesB);

        Vector bias = new Vector();
        bias.setMatrix(a);
        neuralNetwork.setBias(bias);

        Matrix weights = new Matrix();
        weights.setMatrix(b);
        neuralNetwork.setWeights(weights);

        Vector input = new Vector();
        Vector target = new Vector();
        for (int i = 0; i < EPOCH; i++) {
            for (int j = 0; j < inputPatterns.size(); j++) {
                input.setMatrix(PrimitiveMatrix.FACTORY.columns(inputPatterns.get(j)));
                target.setMatrix(PrimitiveMatrix.FACTORY.columns(outputPatterns.get(j)));
                //neuralNetwork.trainNetwork(input, target);
            }
            log("pass: " + i + " error: " + neuralNetwork.getError().getMatrix().toString());
        }
    }*/

    /*
    depending on how you generate the UUID you have different fields which are accessible
    this test is in order to demonstrate the differences
     */
    /*public void testUUIDGenerators() throws Exception {

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
    }*/

    protected void log(String message) {
        if (debug) {
            System.out.println(message);
            //logger.debug(message);
        }
    }
}
