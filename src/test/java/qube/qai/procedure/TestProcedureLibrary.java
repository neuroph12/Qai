/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

package qube.qai.procedure;

import java.util.HashMap;
import java.util.Map;

public class TestProcedureLibrary implements ProcedureLibraryInterface {

    /*public static ProcedureTemplate<SimpleProcedure> simpleTemplate = new ProcedureTemplate<SimpleProcedure>() {
        @Override
        public SimpleProcedure createProcedure() {
            return new SimpleProcedure();
        }
    };

    public static ProcedureTemplate<MatrixStatistics> matrixStatisticsTemplate = new ProcedureTemplate<MatrixStatistics>() {
        @Override
        public MatrixStatistics createProcedure() {
            return new MatrixStatistics();
        }
    };

    public static ProcedureTemplate<TimeSequenceAnalysis> timeSequenceAnalysisTemplate = new ProcedureTemplate<TimeSequenceAnalysis>() {
        @Override
        public TimeSequenceAnalysis createProcedure() {
            return new TimeSequenceAnalysis();
        }
    };

    public static ProcedureTemplate<NetworkStatistics> networkStatisticstemplate = new ProcedureTemplate<NetworkStatistics>() {
        @Override
        public NetworkStatistics createProcedure() {
            return new NetworkStatistics();
        }
    };

    public static ProcedureTemplate<NeuralNetworkAnalysis> neuralNetworkAnalysisTemplate = new ProcedureTemplate<NeuralNetworkAnalysis>() {
        @Override
        public NeuralNetworkAnalysis createProcedure() {
            return new NeuralNetworkAnalysis();
        }
    };

    public static ProcedureTemplate<NeuralNetworkForwardPropagation> forwardPropagationTemplate = new ProcedureTemplate<NeuralNetworkForwardPropagation>() {
        @Override
        public NeuralNetworkForwardPropagation createProcedure() {
            return new NeuralNetworkForwardPropagation();
        }
    };

    public static ProcedureTemplate<DirectoryIndexer> directoryIndexerTemplate = new ProcedureTemplate<DirectoryIndexer>() {
        @Override
        public DirectoryIndexer createProcedure() {
            return new DirectoryIndexer();
        }
    };

    public static ProcedureTemplate<WikiArchiveIndexer> wikiArchiveIndexerTemplate = new ProcedureTemplate<WikiArchiveIndexer>() {
        @Override
        public WikiArchiveIndexer createProcedure() {
            return new WikiArchiveIndexer();
        }
    };

    public static ProcedureTemplate<ChangePointAnalysis> changePointAnalysisTemplate = new ProcedureTemplate<ChangePointAnalysis>() {
        @Override
        public ChangePointAnalysis createProcedure() {
            return new ChangePointAnalysis();
        }
    };



    public static ProcedureTemplate<StockEntityInitialization> stockEntityInitializationTemplate = new ProcedureTemplate<StockEntityInitialization>() {
        @Override
        public StockEntityInitialization createProcedure() {
            return new StockEntityInitialization();
        }
    };

    public static ProcedureTemplate<StockQuoteUpdater> plainStockQuoteUpdater = new ProcedureTemplate<StockQuoteUpdater>() {
        @Override
        public StockQuoteUpdater createProcedure() {
            return new StockQuoteUpdater();
        }
    };

    public static ProcedureTemplate<ForEach> stockQuoteUpdaterTemplate = new ProcedureTemplate<ForEach>() {
        @Override
        public ForEach createProcedure() {
            ForEach forEach = new ForEach();
            forEach.setTargetInputName(STOCK_ENTITY);
            forEach.setTemplate(plainStockQuoteUpdater);
            return forEach;
        }
    };

    public static ProcedureTemplate<WikiRipperProcedure> wikiRipperTemplate = new ProcedureTemplate<WikiRipperProcedure>() {
        @Override
        public WikiRipperProcedure createProcedure() {
            return new WikiRipperProcedure();
        }
    };

    public static ProcedureTemplate<Attach> attachTemplate = new ProcedureTemplate<Attach>() {
        @Override
        public Attach createProcedure() {
            return new Attach();
        }
    };

    public static ProcedureTemplate<SelectOut> selectionTemplate = new ProcedureTemplate<SelectOut>() {
        @Override
        public SelectOut createProcedure() {
            return new SelectOut();
        }
    };

    public static ProcedureTemplate<CreateUser> createUserTemplate = new ProcedureTemplate<CreateUser>() {
        @Override
        public CreateUser createProcedure() {
            return new CreateUser();
        }
    };

    public static ProcedureTemplate<FinanceNetworkBuilder> financeNetworkBuilderTemplate = new ProcedureTemplate<FinanceNetworkBuilder>() {
        @Override
        public FinanceNetworkBuilder createProcedure() {
            return new FinanceNetworkBuilder();
        }
    };

    public static ProcedureTemplate<SequenceCollectionAverager> sequenceAveragertemplate = new ProcedureTemplate<SequenceCollectionAverager>() {
        @Override
        public SequenceCollectionAverager createProcedure() {
            return new SequenceCollectionAverager();
        }
    };

    static ProcedureTemplate<SortPercentiles> plainSortingPercentiles = new ProcedureTemplate<SortPercentiles>() {
        @Override
        public SortPercentiles createProcedure() {
            return new SortPercentiles();
        }
    };

    */

    /**
     * remember this is a template which is meant to be executed with the
     * input from the gui-layer, in this case a collection of pointers to
     * stock-entities whose quotes need first updated
     *//*
    @Deprecated
    public static ProcedureTemplate<ForEach> sortingPercentilesTemplate = new ProcedureTemplate<ForEach>() {

        @Override
        public ForEach createProcedure() {
            SortPercentiles procedure = new SortPercentiles();
            ForEach forEach = new ForEach();
            forEach.getProcedureDescription().getProcedureInputs().getNamedInput(PROCEDURE_TEMPLATE).setValue(stockQuoteUpdaterTemplate);
            forEach.getProcedureDescription().getProcedureInputs().getNamedInput(TARGET_INPUT_NAME).setValue(STOCK_ENTITY);
            procedure.getProcedureDescription().getProcedureInputs().getNamedInput(FROM).setValue(forEach);
            return forEach;
        }
    };

    public static ProcedureTemplate<SliceIntervals> sliceTemplate = new ProcedureTemplate<SliceIntervals>() {
        @Override
        public SliceIntervals createProcedure() {
            return new SliceIntervals();
        }
    };

    public static ProcedureTemplate<SequenceCollectionAverager> sequenceAveragerTemplate = new ProcedureTemplate<SequenceCollectionAverager>() {
        @Override
        public SequenceCollectionAverager createProcedure() {
            return new SequenceCollectionAverager();
        }
    };

    public static ProcedureTemplate<ForEach> forEachTemplate = new ProcedureTemplate<ForEach>() {
        @Override
        public ForEach createProcedure() {
            return new ForEach();
        }
    };

    public static ProcedureTemplate<WikiNetworkBuilder> wikiNetworkBuilderTemplate = new ProcedureTemplate<WikiNetworkBuilder>() {
        @Override
        public WikiNetworkBuilder createProcedure() {
            return new WikiNetworkBuilder();
        }
    };*/
    public Map<Class, ProcedureTemplate> getTemplateMap() {

        Map<Class, ProcedureTemplate> templateMap = new HashMap<>();

        //templateMap.put(SimpleProcedure.class, simpleTemplate);
//        templateMap.put(MatrixStatistics.class, matrixStatisticsTemplate);
//        templateMap.put(ChangePointAnalysis.class, changePointAnalysisTemplate);
//        templateMap.put(TimeSequenceAnalysis.class, timeSequenceAnalysisTemplate);
//        templateMap.put(NetworkStatistics.class, networkStatisticstemplate);
//        templateMap.put(NeuralNetworkAnalysis.class, neuralNetworkAnalysisTemplate);
//        templateMap.put(NeuralNetworkForwardPropagation.class, forwardPropagationTemplate);
//        templateMap.put(DirectoryIndexer.class, directoryIndexerTemplate);
//        templateMap.put(WikiArchiveIndexer.class, wikiArchiveIndexerTemplate);
//        templateMap.put(StockEntityInitialization.class, stockEntityInitializationTemplate);
//        templateMap.put(StockQuoteUpdater.class, stockQuoteUpdaterTemplate);
//        templateMap.put(WikiRipperProcedure.class, wikiRipperTemplate);
//        templateMap.put(Attach.class, attachTemplate);
//        templateMap.put(SelectOut.class, selectionTemplate);
//        templateMap.put(CreateUser.class, createUserTemplate);
//        templateMap.put(FinanceNetworkBuilder.class, financeNetworkBuilderTemplate);
//        //templateMap.put(SortPercentiles.class, sortingPercentilesTemplate);
//        templateMap.put(SliceIntervals.class, sliceTemplate);
//        templateMap.put(ForEach.class, forEachTemplate);
//        templateMap.put(WikiNetworkBuilder.class, wikiNetworkBuilderTemplate);
        return templateMap;
    }
}
