
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

import qube.qai.network.finance.FinanceNetworkBuilderSpawner;
import qube.qai.network.wiki.WikiNetworkBuilder;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.analysis.SortPercentiles;
import qube.qai.procedure.finance.SequenceCollectionAverager;
import qube.qai.procedure.finance.StockQuoteUpdater;
import qube.qai.procedure.utils.ForEach;
import qube.qai.procedure.utils.Select;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 7/14/17.
 */
public class ProcedureLibrary implements ProcedureLibraryInterface, ProcedureConstants {

    /*public static ProcedureTemplate<Attach> attachTemplate = new ProcedureTemplate<Attach>() {
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

    private static ProcedureTemplate<SortPercentiles> plainSortingPercentiles = new ProcedureTemplate<SortPercentiles>() {
        @Override
        public SortPercentiles createProcedure() {
            return new SortPercentiles();
        }
    };

    public static ProcedureTemplate<StockEntityInitialization> stockEntityInitializationTemplate = new ProcedureTemplate<StockEntityInitialization>() {
        @Override
        public StockEntityInitialization createProcedure() {
            return new StockEntityInitialization();
        }
    };

    public static ProcedureTemplate<WikiRipperProcedure> wikiRipperTemplate = new ProcedureTemplate<WikiRipperProcedure>() {
        @Override
        public WikiRipperProcedure createProcedure() {
            return new WikiRipperProcedure();
        }
    };

    public static ProcedureTemplate<SliceIntervals> sliceTemplate = new ProcedureTemplate<SliceIntervals>() {
        @Override
        public SliceIntervals createProcedure() {
            return new SliceIntervals();
        }

        @Override
        public String getProcedureName() {
            return null;
        }

        @Override
        public String getProcedureDescription() {
            return null;
        }
    };

    public static ProcedureTemplate<SequenceCollectionAverager> sequenceAveragerTemplate = new ProcedureTemplate<SequenceCollectionAverager>() {
        @Override
        public SequenceCollectionAverager createProcedure() {
            return new SequenceCollectionAverager();
        }

        @Override
        public String getProcedureName() {
            return null;
        }

        @Override
        public String getProcedureDescription() {
            return null;
        }
    };

    public static ProcedureTemplate<ForEach> forEachTemplate = new ProcedureTemplate<ForEach>() {
        @Override
        public ForEach createProcedure() {
            return new ForEach();
        }
    };

    public static ProcedureTemplate<FinanceNetworkBuilder> financeNetworkBuilderTemplate = new ProcedureTemplate<FinanceNetworkBuilder>() {
        @Override
        public FinanceNetworkBuilder createProcedure() {
            return new FinanceNetworkBuilder();
        }

        @Override
        public String getProcedureName() {
            return FinanceNetworkBuilder.NAME;
        }

        @Override
        public String getProcedureDescription() {
            return FinanceNetworkBuilder.DESCRIPTION;
        }
    };*/


    private static ProcedureTemplate<StockQuoteUpdater> plainStockQuoteUpdater = new SimpleProcedureTemplate<StockQuoteUpdater>(new StockQuoteUpdater());
    ;


    public static ProcedureTemplate<Select> wikiNetworkBuilderTemplate = new ProcedureTemplate<Select>() {
        @Override
        public Select createProcedure() {

            Select select = new Select();

            return select;
        }

        @Override
        public String getProcedureName() {
            return WikiNetworkBuilder.NAME;
        }

        @Override
        public String getProcedureDescription() {
            return WikiNetworkBuilder.DESCRIPTION;
        }
    };

    public static ProcedureTemplate<Select> financeNetworkBuilderTemplate = new ProcedureTemplate<Select>() {
        @Override
        public Select createProcedure() {

            Select select = new Select();
            return select;
        }

        @Override
        public String getProcedureName() {
            return FinanceNetworkBuilderSpawner.NAME;
        }

        @Override
        public String getProcedureDescription() {
            return FinanceNetworkBuilderSpawner.DESCRIPTION;
        }
    };

    public static ProcedureTemplate<Select> changePointAnalysisTemplate = new ProcedureTemplate<Select>() {
        @Override
        public Select createProcedure() {


            ForEach forEach = new ForEach();

            ChangePointAnalysis changePoint = new ChangePointAnalysis();
            Select select = new Select();


            return select;
        }

        @Override
        public String getProcedureName() {
            return ChangePointAnalysis.NAME;
        }

        @Override
        public String getProcedureDescription() {
            return ChangePointAnalysis.DESCRIPTION;
        }
    };

    public static ProcedureTemplate<Select> sequenceAveragerTemplate = new ProcedureTemplate<Select>() {
        @Override
        public Select createProcedure() {

            SequenceCollectionAverager averager = new SequenceCollectionAverager();
            Select select = new Select();
            averager.setSelect(select);

            return select;
        }

        @Override
        public String getProcedureName() {
            return SequenceCollectionAverager.NAME;
        }

        @Override
        public String getProcedureDescription() {
            return SequenceCollectionAverager.DESCRIPTION;
        }
    };


    /**
     * remember this is a template which is meant to be executed with the
     * input from the gui-layer, in this case a collection of pointers to
     * stock-entities whose quotes need first updated
     */
    public static ProcedureTemplate<Select> sortingPercentilesTemplate = new ProcedureTemplate<Select>() {

        @Override
        public Select createProcedure() {
            Select select = new Select();
            SortPercentiles procedure = new SortPercentiles();
            ForEach forEach = new ForEach();
            forEach.getProcedureDescription().getProcedureInputs().getNamedInput(PROCEDURE_TEMPLATE).setValue(stockQuoteUpdaterTemplate);
            forEach.getProcedureDescription().getProcedureInputs().getNamedInput(TARGET_INPUT_NAME).setValue(STOCK_ENTITY);
            procedure.getProcedureDescription().getProcedureInputs().getNamedInput(FROM).setValue(forEach);
            //return forEach;

            return select;
        }

        @Override
        public String getProcedureName() {
            return SortPercentiles.NAME;
        }

        @Override
        public String getProcedureDescription() {
            return SortPercentiles.DESCRIPTION;
        }
    };


    public static ProcedureTemplate<Select> stockQuoteUpdaterTemplate = new ProcedureTemplate<Select>() {
        @Override
        public Select createProcedure() {
            Select procedure = new Select();


            return procedure;
        }

        @Override
        public String getProcedureName() {
            return StockQuoteUpdater.NAME;
        }

        @Override
        public String getProcedureDescription() {
            return StockQuoteUpdater.DESCRIPTION;
        }
    };

    @Override
    public Map<Class, ProcedureTemplate> getTemplateMap() {

        Map<Class, ProcedureTemplate> templateMap = new HashMap<>();

        //templateMap.put(SimpleProcedure.class, simpleTemplate);
        //templateMap.put(MatrixStatistics.class, matrixStatisticsTemplate);
        //templateMap.put(TimeSequenceAnalysis.class, timeSequenceAnalysisTemplate);
        //templateMap.put(NetworkStatistics.class, networkStatisticstemplate);
        //templateMap.put(NeuralNetworkAnalysis.class, neuralNetworkAnalysisTemplate);
        //templateMap.put(NeuralNetworkForwardPropagation.class, forwardPropagationTemplate);
        //templateMap.put(DirectoryIndexer.class, directoryIndexerTemplate);
        //templateMap.put(WikiArchiveIndexer.class, wikiArchiveIndexerTemplate);
        //templateMap.put(StockEntityInitialization.class, stockEntityInitializationTemplate);
        //templateMap.put(WikiRipperProcedure.class, wikiRipperTemplate);
        //templateMap.put(Attach.class, attachTemplate);
        //templateMap.put(SelectOut.class, selectionTemplate);
        //templateMap.put(CreateUser.class, createUserTemplate);
        //templateMap.put(SortPercentiles.class, sortingPercentilesTemplate);
        //templateMap.put(ForEach.class, forEachTemplate);
        //templateMap.put(FinanceNetworkBuilder.class, financeNetworkBuilderTemplate);
        //templateMap.put(SliceIntervals.class, sliceTemplate);

        templateMap.put(WikiNetworkBuilder.class, wikiNetworkBuilderTemplate);
        templateMap.put(Procedure.class, financeNetworkBuilderTemplate);
        templateMap.put(ChangePointAnalysis.class, changePointAnalysisTemplate);
        templateMap.put(SequenceCollectionAverager.class, sequenceAveragerTemplate);
        templateMap.put(SortPercentiles.class, sortingPercentilesTemplate);
        templateMap.put(StockQuoteUpdater.class, stockQuoteUpdaterTemplate);

        return templateMap;
    }

//    public static Map<String, ProcedureTemplate> getTemplateNameMap() {
//
//        Map<String, ProcedureTemplate> templateMap = new TreeMap<>();
//
//        templateMap.put(SimpleProcedure.NAME, simpleTemplate);
//        templateMap.put(MatrixStatistics.NAME, matrixStatisticsTemplate);
//        templateMap.put(ChangePointAnalysis.NAME, changePointAnalysisTemplate);
//        templateMap.put(TimeSequenceAnalysis.NAME, timeSequenceAnalysisTemplate);
//        templateMap.put(NetworkStatistics.NAME, networkStatisticstemplate);
//        templateMap.put(NeuralNetworkAnalysis.NAME, neuralNetworkAnalysisTemplate);
//        templateMap.put(NeuralNetworkForwardPropagation.NAME, forwardPropagationTemplate);
//        templateMap.put(DirectoryIndexer.NAME, directoryIndexerTemplate);
//        templateMap.put(WikiArchiveIndexer.NAME, wikiArchiveIndexerTemplate);
//        templateMap.put(StockEntityInitialization.NAME, stockEntityInitializationTemplate);
//        templateMap.put(StockQuoteUpdater.NAME, stockQuoteUpdaterTemplate);
//        templateMap.put(WikiRipperProcedure.NAME, wikiRipperTemplate);
//        templateMap.put(Attach.NAME, attachTemplate);
//        templateMap.put(SelectOut.NAME, selectionTemplate);
//        templateMap.put(CreateUser.NAME, createUserTemplate);
//        templateMap.put(FinanceNetworkBuilder.NAME, financeNetworkBuilderTemplate);
//        templateMap.put(SortPercentiles.NAME, sortingPercentilesTemplate);
//        templateMap.put(SliceIntervals.NAME, sliceTemplate);
//        templateMap.put(ForEach.NAME, forEachTemplate);
//
//        return templateMap;
//    }

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
