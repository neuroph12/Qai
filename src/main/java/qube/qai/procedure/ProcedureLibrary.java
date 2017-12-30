
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

import qube.qai.network.finance.FinanceNetworkBuilder;
import qube.qai.network.wiki.WikiNetworkBuilder;
import qube.qai.procedure.analysis.*;
import qube.qai.procedure.archive.DirectoryIndexer;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.finance.SequenceCollectionAverager;
import qube.qai.procedure.finance.StockEntityInitialization;
import qube.qai.procedure.finance.StockQuoteUpdater;
import qube.qai.procedure.utils.*;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by rainbird on 7/14/17.
 */
public class ProcedureLibrary implements ProcedureConstants {

    public static ProcedureTemplate<SimpleProcedure> simpleTemplate = new ProcedureTemplate<SimpleProcedure>() {
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

    public static ProcedureTemplate<ChangePointAnalysis> changePointAnalysisTemplate = new ProcedureTemplate<ChangePointAnalysis>() {
        @Override
        public ChangePointAnalysis createProcedure() {
            return new ChangePointAnalysis();
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

    private static ProcedureTemplate<SortPercentiles> plainSortingPercentiles = new ProcedureTemplate<SortPercentiles>() {
        @Override
        public SortPercentiles createProcedure() {
            return new SortPercentiles();
        }
    };

    /**
     * remember this is a template which is meant to be executed with the
     * input from the gui-layer, in this case a collection of pointers to
     * stock-entities whose quotes need first updated
     */
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
    };

    public static Map<Class, ProcedureTemplate> getTemplateMap() {

        Map<Class, ProcedureTemplate> templateMap = new HashMap<>();

        templateMap.put(SimpleProcedure.class, simpleTemplate);
        templateMap.put(MatrixStatistics.class, matrixStatisticsTemplate);
        templateMap.put(ChangePointAnalysis.class, changePointAnalysisTemplate);
        templateMap.put(TimeSequenceAnalysis.class, timeSequenceAnalysisTemplate);
        templateMap.put(NetworkStatistics.class, networkStatisticstemplate);
        templateMap.put(NeuralNetworkAnalysis.class, neuralNetworkAnalysisTemplate);
        templateMap.put(NeuralNetworkForwardPropagation.class, forwardPropagationTemplate);
        templateMap.put(DirectoryIndexer.class, directoryIndexerTemplate);
        templateMap.put(WikiArchiveIndexer.class, wikiArchiveIndexerTemplate);
        templateMap.put(StockEntityInitialization.class, stockEntityInitializationTemplate);
        templateMap.put(StockQuoteUpdater.class, stockQuoteUpdaterTemplate);
        templateMap.put(WikiRipperProcedure.class, wikiRipperTemplate);
        templateMap.put(Attach.class, attachTemplate);
        templateMap.put(SelectOut.class, selectionTemplate);
        templateMap.put(CreateUser.class, createUserTemplate);
        templateMap.put(FinanceNetworkBuilder.class, financeNetworkBuilderTemplate);
        templateMap.put(SortPercentiles.class, sortingPercentilesTemplate);
        templateMap.put(SliceIntervals.class, sliceTemplate);
        templateMap.put(ForEach.class, forEachTemplate);
        templateMap.put(WikiNetworkBuilder.class, wikiNetworkBuilderTemplate);
        return templateMap;
    }

    public static Map<String, ProcedureTemplate> getTemplateNameMap() {

        Map<String, ProcedureTemplate> templateMap = new TreeMap<>();

        templateMap.put(SimpleProcedure.NAME, simpleTemplate);
        templateMap.put(MatrixStatistics.NAME, matrixStatisticsTemplate);
        templateMap.put(ChangePointAnalysis.NAME, changePointAnalysisTemplate);
        templateMap.put(TimeSequenceAnalysis.NAME, timeSequenceAnalysisTemplate);
        templateMap.put(NetworkStatistics.NAME, networkStatisticstemplate);
        templateMap.put(NeuralNetworkAnalysis.NAME, neuralNetworkAnalysisTemplate);
        templateMap.put(NeuralNetworkForwardPropagation.NAME, forwardPropagationTemplate);
        templateMap.put(DirectoryIndexer.NAME, directoryIndexerTemplate);
        templateMap.put(WikiArchiveIndexer.NAME, wikiArchiveIndexerTemplate);
        templateMap.put(StockEntityInitialization.NAME, stockEntityInitializationTemplate);
        templateMap.put(StockQuoteUpdater.NAME, stockQuoteUpdaterTemplate);
        templateMap.put(WikiRipperProcedure.NAME, wikiRipperTemplate);
        templateMap.put(Attach.NAME, attachTemplate);
        templateMap.put(SelectOut.NAME, selectionTemplate);
        templateMap.put(CreateUser.NAME, createUserTemplate);
        templateMap.put(FinanceNetworkBuilder.NAME, financeNetworkBuilderTemplate);
        templateMap.put(SortPercentiles.NAME, sortingPercentilesTemplate);
        templateMap.put(SliceIntervals.NAME, sliceTemplate);
        templateMap.put(ForEach.NAME, forEachTemplate);

        return templateMap;
    }

    /*public static ProcedureTemplate[] allTemplates = new ProcedureTemplate[]{
            simpleTemplate,
            matrixStatisticsTemplate,
            changePointAnalysisTemplate,
            timeSequenceAnalysisTemplate,
            networkStatisticstemplate,
            neuralNetworkAnalysisTemplate,
            forwardPropagationTemplate,
            directoryIndexerTemplate,
            wikiArchiveIndexerTemplate,
            stockEntityInitializationTemplate,
            stockQuoteUpdaterTemplate,
            wikiRipperTemplate,
            attachTemplate,
            selectionTemplate,
            createUserTemplate,
            financeNetworkBuilderTemplate,
            sortingPercentilesTemplate,
            sliceTemplate,
            forEachTemplate
    };*/

    //private static Map<String, ProcedureTemplate> templateMap = new TreeMap<>();


    /*public static ProcedureTemplate getNamedProcedureTemplate(String name) {
        return getTemplateMap().get(name);
    }*/

    /**
     * All known implementing sub-classes
     *
     * @return
     */
    /*public static Collection<Class> knownSubClasses() {

        Collection<Class> classes = new ArrayList<>();

        // analysis
        classes.add(SimpleProcedure.class);
        classes.add(MatrixStatistics.class);
        classes.add(ChangePointAnalysis.class);
        classes.add(TimeSequenceAnalysis.class);
        classes.add(NetworkStatistics.class);
        classes.add(NeuralNetworkAnalysis.class);
        classes.add(NeuralNetworkForwardPropagation.class);
        classes.add(DirectoryIndexer.class);
        classes.add(WikiArchiveIndexer.class);
        classes.add(StockEntityInitialization.class);
        classes.add(WikiRipperProcedure.class);

        classes.add(FinanceNetworkBuilder.class);
        classes.add(SortPercentiles.class);
        classes.add(TimeSequenceAnalysis.class);

        // archive

        // finance
        classes.add(StockQuoteUpdater.class);

        // utils
        classes.add(Attach.class);
        classes.add(CreateUser.class);
        classes.add(SelectOut.class);

        return classes;
    }*/

}
