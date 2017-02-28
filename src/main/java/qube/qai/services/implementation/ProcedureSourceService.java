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

package qube.qai.services.implementation;

import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureSourceInterface;

/**
 * Created by rainbird on 12/16/15.
 */
public class ProcedureSourceService implements ProcedureSourceInterface {

    /**
     * @TODO implement the methods
     */


    public Procedure getProcedureWithName(String name) {
        return null;
    }

    public String[] getProcedureNames() {
        return new String[0];
    }

    /*private int number = 100;
    private int size = 10;
    private String startDate = "2000-1-1";
    private String endDate = "2000-12-31";

    private String[] procedureNames = { ChangePointAnalysis.NAME,
            MatrixStatistics.NAME,
            NetworkStatistics.NAME,
            NeuralNetworkAnalysis.NAME,
            NeuralNetworkForwardPropagation.NAME,
            SortingPercentilesProcedure.NAME,
            TimeSequenceAnalysis.NAME,
            MarketNetworkBuilder.NAME
    };

    public Procedure getProcedureWithName(String name) {

        Procedure toDecorate = new SelectionProcedure();
        Procedure procedure = null;
        Date start = DateTime.parse(startDate).toDate();
        Date end = DateTime.parse(endDate).toDate();

        if (ChangePointAnalysis.NAME.equals(name)) {

            TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(start, end);
            Selector<TimeSequence> selector = new DataSelector<TimeSequence>(timeSequence);
            toDecorate.getArguments().setArgument(ChangePointAnalysis.INPUT_TIME_SEQUENCE, selector);

            procedure = new ChangePointAnalysis(toDecorate);

        } else if (MatrixStatistics.NAME.equals(name)) {

            Matrix matrix = Matrix.createMatrix(true, 100, 100);
            Selector<Matrix> selector = new DataSelector<Matrix>(matrix);
            toDecorate.getArguments().setArgument(MatrixStatistics.INPUT_MATRIX, selector);

            procedure = new MatrixStatistics(toDecorate);

        } else if (NetworkStatistics.NAME.equals(name)) {

            Network network = Network.createTestNetwork();
            Selector<Network> selector = new DataSelector<Network>(network);
            toDecorate.getArguments().setArgument(NetworkStatistics.INPUT_NETWORK, selector);
            procedure = new NetworkStatistics(toDecorate);

        } else if (NeuralNetworkAnalysis.NAME.equals(name)) {

            Matrix matrix = Matrix.createMatrix(true, 100, 100);
            NeuralNetwork network = new NeuralNetwork(matrix);
            Selector<NeuralNetwork> selector = new DataSelector<NeuralNetwork>(network);
            toDecorate.getArguments().setArgument(NeuralNetworkAnalysis.INPUT_NEURAL_NETWORK, selector);

            procedure = new NeuralNetworkAnalysis(toDecorate);

        } else if (NeuralNetworkForwardPropagation.NAME.equals(name)) {

            Arguments arguments = toDecorate.getArguments();

            Matrix matrix = Matrix.createMatrix(true, size, size);
            NeuralNetwork neuralNetwork = new NeuralNetwork(matrix);
            Selector<NeuralNetwork> networkSelector = new DataSelector<NeuralNetwork>(neuralNetwork);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_NEURAL_NETWORK, networkSelector);

            double[] firstDay = new double[size];
            RandomNumber generator = new Normal(0.5, 0.1);
            for (int i = 0; i < firstDay.length; i++) {
                firstDay[i] = generator.doubleValue();
            }
            Vector startVector = Vector.buildFromArray(firstDay);
            Selector<Vector> startVectorSelector = new DataSelector<Vector>(startVector);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_START_VECTOR, startVectorSelector);

            List<String> names = new ArrayList<String>();
            String[] nameStrings = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eightth", "nineth", "tenth"};
            for (String n : nameStrings) {
                names.add(n);
            }
            Selector<List> namesSelector = new DataSelector<List>(names);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_NAMES, namesSelector);

            Date startDate = DateTime.parse("2015-1-1").toDate();
            Date endDate = DateTime.parse("2015-1-10").toDate();

            List<Date> dates = TimeSequence.createDates(startDate, endDate);;
            Selector<List> stepsSelector = new DataSelector<List>(dates);
            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_DATES_FOR_STEPS, stepsSelector);

            procedure = new NeuralNetworkForwardPropagation(toDecorate);

        } else if (SortingPercentilesProcedure.NAME.equals(name)) {

            procedure = new SortingPercentilesProcedure(toDecorate);


            Date startDate = DateTime.parse("2015-1-1").toDate();
            Date endDate = DateTime.now().toDate();

            Map<String, Selector> timeSeriesMap = new HashMap<String, Selector>();
            for (int i = 0; i < number; i++) {
                TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(startDate, endDate);
                Selector<TimeSequence> selector = new DataSelector<TimeSequence>(timeSequence);
                String key = "entity_" + i;
                timeSeriesMap.put(key, selector);
            }

            Selector<Map> collectionSelector = new DataSelector<Map>(timeSeriesMap);
            procedure.getArguments().setArgument(SortingPercentilesProcedure.FROM, collectionSelector);

            // @TODO is this really required in the latest form of the class and what it does?!?
            Selector<String> criteria = new DataSelector<String>("criteria");
            procedure.getArguments().setArgument(SortingPercentilesProcedure.CRITERIA, criteria);

        } else if (TimeSequenceAnalysis.NAME.equals(name)) {

            TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(start, end);
            Selector<TimeSequence> selector = new DataSelector<TimeSequence>(timeSequence);

            toDecorate.getArguments().setArgument(TimeSequenceAnalysis.INPUT_TIME_SEQUENCE, selector);
            procedure = new TimeSequenceAnalysis(toDecorate);

        } else if (MarketNetworkBuilder.NAME.equals(name)) {

            procedure = new MarketNetworkBuilder(toDecorate);

            Collection<StockEntity> stockEntities = new ArrayList<StockEntity>();
            Selector<Collection> selector = new DataSelector<Collection>(stockEntities);

            procedure.getArguments().setArgument(MarketNetworkBuilder.INPUT_STOCK_ENTITY_COLLECTION, selector);

        }

        return procedure;
    }

    public String[] getProcedureNames() {
        return procedureNames;
    }*/
}
