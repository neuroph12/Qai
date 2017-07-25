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

package qube.qai.procedure;

import qube.qai.procedure.analysis.*;
import qube.qai.procedure.archive.DirectoryIndexer;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.finance.StockEntityInitialization;
import qube.qai.procedure.finance.StockQuoteRetriever;
import qube.qai.procedure.utils.CreateUserProcedure;
import qube.qai.procedure.utils.RelateProcedure;
import qube.qai.procedure.utils.SelectionProcedure;
import qube.qai.procedure.utils.SimpleProcedure;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

/**
 * Created by rainbird on 7/14/17.
 */
public class ProcedureLibrary {

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

    public static ProcedureTemplate<StockQuoteRetriever> stockQuoteRetriverTemplate = new ProcedureTemplate<StockQuoteRetriever>() {
        @Override
        public StockQuoteRetriever createProcedure() {
            return new StockQuoteRetriever();
        }
    };

    public static ProcedureTemplate<WikiRipperProcedure> wikiRipperTemplate = new ProcedureTemplate<WikiRipperProcedure>() {
        @Override
        public WikiRipperProcedure createProcedure() {
            return new WikiRipperProcedure();
        }
    };

    public static ProcedureTemplate<RelateProcedure> relateTemplate = new ProcedureTemplate<RelateProcedure>() {
        @Override
        public RelateProcedure createProcedure() {
            return new RelateProcedure();
        }
    };

    public static ProcedureTemplate<SelectionProcedure> selectionTemplate = new ProcedureTemplate<SelectionProcedure>() {
        @Override
        public SelectionProcedure createProcedure() {
            return new SelectionProcedure();
        }
    };

    public static ProcedureTemplate<CreateUserProcedure> createUserTemplate = new ProcedureTemplate<CreateUserProcedure>() {
        @Override
        public CreateUserProcedure createProcedure() {
            return new CreateUserProcedure();
        }
    };

    public static ProcedureTemplate[] allTemplates = new ProcedureTemplate[]{
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
            stockQuoteRetriverTemplate,
            wikiRipperTemplate,
            relateTemplate,
            selectionTemplate,
            createUserTemplate
    };
}