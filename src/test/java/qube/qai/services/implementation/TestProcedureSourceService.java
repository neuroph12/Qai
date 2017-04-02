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
import qube.qai.procedure.analysis.*;
import qube.qai.services.ProcedureSourceInterface;

/**
 * Created by rainbird on 12/27/15.
 */
public class TestProcedureSourceService implements ProcedureSourceInterface {

    private int number = 100;
    private int size = 10;
    private String startDate = "2000-1-1";
    private String endDate = "2000-12-31";

    private String[] procedureNames = {ChangePointAnalysis.NAME,
            MatrixStatistics.NAME,
            NetworkStatistics.NAME,
            NeuralNetworkAnalysis.NAME,
            NeuralNetworkForwardPropagation.NAME,
            SortingPercentilesProcedure.NAME,
            TimeSequenceAnalysis.NAME,
            MarketNetworkBuilder.NAME
    };

    public Procedure getProcedureWithName(String name) {

        Procedure procedure = null;
//        Procedure toDecorate = new SelectionProcedure();
//        Date start = DateTime.parse(startDate).toDate();
//        Date end = DateTime.parse(endDate).toDate();
//
//        if (ChangePointAnalysis.NAME.equals(name)) {
//
//            TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(start, end);
//            SelectionOperator<TimeSequence> selectionOperator = new DataSelectionOperator<TimeSequence>(timeSequence);
//            toDecorate.getArguments().setArgument(ChangePointAnalysis.INPUT_TIME_SEQUENCE, selectionOperator);
//
//            procedure = new ChangePointAnalysis(toDecorate);
//
//        } else if (MatrixStatistics.NAME.equals(name)) {
//
//            Matrix matrix = Matrix.createMatrix(true, 100, 100);
//            SelectionOperator<Matrix> selectionOperator = new DataSelectionOperator<Matrix>(matrix);
//            toDecorate.getArguments().setArgument(MatrixStatistics.INPUT_MATRIX, selectionOperator);
//
//            procedure = new MatrixStatistics(toDecorate);
//
//        } else if (NetworkStatistics.NAME.equals(name)) {
//
//            Network network = Network.createTestNetwork();
//            SelectionOperator<Network> selectionOperator = new DataSelectionOperator<Network>(network);
//            toDecorate.getArguments().setArgument(NetworkStatistics.INPUT_NETWORK, selectionOperator);
//            procedure = new NetworkStatistics();
//            procedure.setFirstChild(toDecorate);
//
//        } else if (NeuralNetworkAnalysis.NAME.equals(name)) {
//
//            Matrix matrix = Matrix.createMatrix(true, 100, 100);
//            NeuralNetwork network = new NeuralNetwork(matrix);
//            SelectionOperator<NeuralNetwork> selectionOperator = new DataSelectionOperator<NeuralNetwork>(network);
//            toDecorate.getArguments().setArgument(NeuralNetworkAnalysis.INPUT_NEURAL_NETWORK, selectionOperator);
//
//            procedure = new NeuralNetworkAnalysis();
//            procedure.setFirstChild(toDecorate);
//
//        } else if (NeuralNetworkForwardPropagation.NAME.equals(name)) {
//
//            Arguments arguments = toDecorate.getArguments();
//
//            Matrix matrix = Matrix.createMatrix(true, size, size);
//            NeuralNetwork neuralNetwork = new NeuralNetwork(matrix);
//            SelectionOperator<NeuralNetwork> networkSelectionOperator = new DataSelectionOperator<NeuralNetwork>(neuralNetwork);
//            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_NEURAL_NETWORK, networkSelectionOperator);
//
//            double[] firstDay = new double[size];
//            RandomNumber generator = new Normal(0.5, 0.1);
//            for (int i = 0; i < firstDay.length; i++) {
//                firstDay[i] = generator.doubleValue();
//            }
//            Vector startVector = Vector.buildFromArray(firstDay);
//            SelectionOperator<Vector> startVectorSelectionOperator = new DataSelectionOperator<Vector>(startVector);
//            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_START_VECTOR, startVectorSelectionOperator);
//
//            List<String> names = new ArrayList<String>();
//            String[] nameStrings = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eightth", "nineth", "tenth"};
//            for (String n : nameStrings) {
//                names.add(n);
//            }
//            SelectionOperator<List> namesSelectionOperator = new DataSelectionOperator<List>(names);
//            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_NAMES, namesSelectionOperator);
//
//            Date startDate = DateTime.parse("2015-1-1").toDate();
//            Date endDate = DateTime.parse("2015-1-10").toDate();
//
//            List<Date> dates = TimeSequence.createDates(startDate, endDate);
//            ;
//            SelectionOperator<List> stepsSelectionOperator = new DataSelectionOperator<List>(dates);
//            arguments.setArgument(NeuralNetworkForwardPropagation.INPUT_DATES_FOR_STEPS, stepsSelectionOperator);
//
//            procedure = new NeuralNetworkForwardPropagation(toDecorate);
//
//        } else if (SortingPercentilesProcedure.NAME.equals(name)) {
//
//            procedure = new SortingPercentilesProcedure(toDecorate);
//
//
//            Date startDate = DateTime.parse("2015-1-1").toDate();
//            Date endDate = DateTime.now().toDate();
//
//            Map<String, SelectionOperator> timeSeriesMap = new HashMap<String, SelectionOperator>();
//            for (int i = 0; i < number; i++) {
//                TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(startDate, endDate);
//                SelectionOperator<TimeSequence> selectionOperator = new DataSelectionOperator<TimeSequence>(timeSequence);
//                String key = "entity_" + i;
//                timeSeriesMap.put(key, selectionOperator);
//            }
//
//            SelectionOperator<Map> collectionSelectionOperator = new DataSelectionOperator<Map>(timeSeriesMap);
//            procedure.getArguments().setArgument(SortingPercentilesProcedure.FROM, collectionSelectionOperator);
//
//            // @TODO is this really required in the latest form of the class and what it does?!?
//            SelectionOperator<String> criteria = new DataSelectionOperator<String>("criteria");
//            procedure.getArguments().setArgument(SortingPercentilesProcedure.CRITERIA, criteria);
//
//        } else if (TimeSequenceAnalysis.NAME.equals(name)) {
//
//            TimeSequence<Double> timeSequence = TimeSequence.createTimeSeries(start, end);
//            SelectionOperator<TimeSequence> selectionOperator = new DataSelectionOperator<TimeSequence>(timeSequence);
//
//            toDecorate.getArguments().setArgument(TimeSequenceAnalysis.INPUT_TIME_SEQUENCE, selectionOperator);
//            procedure = new TimeSequenceAnalysis(toDecorate);
//
//        } else if (MarketNetworkBuilder.NAME.equals(name)) {
//
//            procedure = new MarketNetworkBuilder(toDecorate);
//
//            Collection<StockEntity> stockEntities = new ArrayList<StockEntity>();
//            SelectionOperator<Collection> selectionOperator = new DataSelectionOperator<Collection>(stockEntities);
//
//            procedure.getArguments().setArgument(MarketNetworkBuilder.INPUT_STOCK_ENTITY_COLLECTION, selectionOperator);
//
//        }
//
        return procedure;
    }

    public String[] getProcedureNames() {
        return procedureNames;
    }
}
