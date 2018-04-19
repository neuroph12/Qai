
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
import qube.qai.procedure.analysis.ChangePoints;
import qube.qai.procedure.finance.AverageSequence;
import qube.qai.procedure.finance.StockQuoteUpdater;
import qube.qai.procedure.utils.SelectForAll;
import qube.qai.procedure.utils.SelectForEach;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zenpunk on 7/14/17.
 */
public class ProcedureLibrary implements ProcedureLibraryInterface, ProcedureConstants {

    /**
     *
     */
    private static ProcedureTemplate<SelectForAll> semanticNetworkBuiderTemplate = new ProcedureTemplate<SelectForAll>() {

        private String name = "Semantic-network Builder";

        private String desc = "Drop in the wiki-articles or pdf-documents from which you want to create a semantic network. " +
                "These networks will be used for shortest-path algorithms required, which will be used " +
                "for machine translation tasks.";

        @Override
        public SelectForAll createProcedure() {

            SelectForAll procedure = new SelectForAll();
            procedure.setNAME(name);
            procedure.setDESCRIPTION(desc);

            return procedure;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    private static ProcedureTemplate<SelectForAll> syntaxNetworkBuiderTemplate = new ProcedureTemplate<SelectForAll>() {

        private String name = "Syntax-network Builder";

        private String desc = "Drop in the wiki-articles or pdf-documents from which you want to create a syntactic network. " +
                "These networks will be used as training-set for training recurrent neural-networks, which will be used " +
                "for machine translation tasks.";

        @Override
        public SelectForAll createProcedure() {

            SelectForAll procedure = new SelectForAll();
            procedure.setNAME(name);
            procedure.setDESCRIPTION(desc);

            return procedure;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    private static ProcedureTemplate<SelectForEach> forwardPropagationTemplate = new ProcedureTemplate<SelectForEach>() {

        private String name = "Neural-network forward-propagation";

        private String desc = "Drop in the trained neural-networks for forward-propagation. In case of " +
                "stock-entity networks, forward propagation will be used for Monte-Carlo simulations " +
                "in order to make future estimations of the market development.";

        @Override
        public SelectForEach createProcedure() {

            SelectForEach procedure = new SelectForEach();
            procedure.setNAME(name);
            procedure.setDESCRIPTION(desc);

            return procedure;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    private static ProcedureTemplate<SelectForEach> neuralNetworkAnalysisTemplate = new ProcedureTemplate<SelectForEach>() {

        private String name = "Neural-network Analysis";

        private String desc = "Drop in the trained neural-networks in order to make statistical analysis of the weights and entropy " +
                "and their graph representation for additional information.";

        @Override
        public SelectForEach createProcedure() {
            SelectForEach procedure = new SelectForEach();

            procedure.setNAME(name);
            procedure.setDESCRIPTION(desc);

            return procedure;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    public static ProcedureTemplate<SelectForAll> wikiNetworkBuilderTemplate = new ProcedureTemplate<SelectForAll>() {

        private String name = "Wiki-network Builder";

        private String desc = "Drop in the wiki-articles for which the network should be created in the tab. " +
                "With the help of this network, the articles can be ranked and related according to their different attributes.";

        @Override
        public SelectForAll createProcedure() {

            SelectForAll select = new SelectForAll();
            select.setNAME(name);
            select.setDESCRIPTION(desc);

            ProcedureTemplate<WikiNetworkBuilder> template = new SimpleProcedureTemplate<>(new WikiNetworkBuilder());
            select.setTemplate(template);

            return select;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    public static ProcedureTemplate<SelectForAll> financeNetworkBuilderTemplate = new ProcedureTemplate<SelectForAll>() {

        private String name = "Finance-network Builder";

        private String desc = "Drop in the stock-entities from the finance menu here. " +
                "The routines will take over the job creating and traning the neural-network with their quotes " +
                "which will be interpreted as a graph. With the help of these graphs the stock-entities can be ranked, " +
                "as well as entropy of the market around this period will also be estimated. " +
                "Additionally the so trained neural-networks will also be used for Monte-Carlo simulations to serve as prognosis tool.";

        @Override
        public SelectForAll createProcedure() {

            SelectForAll select = new SelectForAll();
            select.setNAME(name);
            select.setDESCRIPTION(desc);

            ProcedureTemplate<FinanceNetworkBuilder> template = new SimpleProcedureTemplate<>(new FinanceNetworkBuilder());
            select.setTemplate(template);

            return select;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    public static ProcedureTemplate<SelectForEach> changePointAnalysisTemplate = new ProcedureTemplate<SelectForEach>() {

        private String name = "Change-point Analysis";

        private String desc = "Drop in the stock-entities for which the analysis should be made. " +
                "This routine detects change-points in time-series, in this case stock-quotes of the selected stock-entities, to divide them in periods. " +
                "These periods will be taken as traning-sets with which neural-networks will be trained." +
                "This way, each trained neural-network with its entropy will be associatied with one such period.";

        @Override
        public SelectForEach createProcedure() {


            SelectForEach select = new SelectForEach();
            select.setNAME(name);
            select.setDESCRIPTION(desc);

            ProcedureTemplate<ChangePoints> template = new SimpleProcedureTemplate<>(new ChangePoints());
            select.setTemplate(template);

            return select;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    public static ProcedureTemplate<SelectForAll> stockQuoteUpdaterTemplate = new ProcedureTemplate<SelectForAll>() {

        private String name = "Stock-quote Updater";

        private String desc = "Drop the stock-quotes to be updated in the selection tab, " +
                "the procedure will retrieve the stock-quotes to the latest stand.";

        @Override
        public SelectForAll createProcedure() {

            SelectForAll procedure = new SelectForAll();
            procedure.setNAME(name);
            procedure.setDESCRIPTION(desc);

            ProcedureTemplate template = new SimpleProcedureTemplate(new StockQuoteUpdater());
            procedure.setTemplate(template);

            return procedure;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return desc;
        }
    };

    /**
     *
     */
    public static ProcedureTemplate<SelectForAll> averageSequenceTemplate = new ProcedureTemplate<SelectForAll>() {

        private String name = "AverageSequence";

        private String desc = "Drop the stock-quotes in the selection tab, " +
                "the procedure will calculate their average time-series.";

        @Override
        public SelectForAll createProcedure() {

            SelectForAll select = new SelectForAll();

            select.setNAME(name);
            select.setDESCRIPTION(desc);

            ProcedureTemplate template = new SimpleProcedureTemplate(new AverageSequence());
            select.setTemplate(template);

            return select;
        }

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public String getProcedureDescription() {
            return name;
        }
    };

    @Override
    public Map<Class, ProcedureTemplate> getTemplateMap() {

        Map<Class, ProcedureTemplate> templateMap = new HashMap<>();

        //templateMap.put(ChangePoints.class, changePointAnalysisTemplate);
        //templateMap.put(FinanceNetworkTrainer.class, financeNetworkBuilderTemplate);
        //templateMap.put(NeuralNetworkForwardPropagation.class, forwardPropagationTemplate);
        //templateMap.put(NeuralNetworkAnalysis.class, neuralNetworkAnalysisTemplate);
        //templateMap.put(SemanticNetworkBuilder.class, semanticNetworkBuiderTemplate);
        templateMap.put(AverageSequence.class, averageSequenceTemplate);
        //templateMap.put(SyntaxNetworkBuilder.class, syntaxNetworkBuiderTemplate);
        //templateMap.put(StockQuoteUpdater.class, stockQuoteUpdaterTemplate);
        //templateMap.put(WikiNetworkBuilder.class, wikiNetworkBuilderTemplate);

        return templateMap;
    }

}
