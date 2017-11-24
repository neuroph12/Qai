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

package qube.qai.services.sims;

import com.hazelcast.core.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.message.QaiMessageListener;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.analysis.MarketNetworkBuilder;
import qube.qai.procedure.analysis.SortingPercentilesProcedure;
import qube.qai.procedure.finance.StockQuoteRetriever;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.procedure.utils.ForEachProcedure;
import qube.qai.procedure.utils.SliceProcedure;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.implementation.DistributedSearchService;
import qube.qai.services.implementation.SearchResult;
import qube.qai.services.implementation.UUIDService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MarketBuilderSim extends QaiMessageListener implements QaiConstants {

    private Logger logger = LoggerFactory.getLogger("MarketBuilderSim");

    private String NAME = "Market-Building & Stock-Market Simulation: [%s]";

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    private Collection<SearchResult> searchResults;

    private Set<String> retrieveQuotesUUIDs;

    public MarketBuilderSim(Collection<SearchResult> searchResults) {
        super();
        this.searchResults = searchResults;
        if (StringUtils.isEmpty(this.getUUID())) {
            NAME = String.format(NAME, UUIDService.uuidString());
        } else {
            NAME = String.format(NAME, this.getUUID());
        }
        procedureTopicName = NAME;
        this.searchResults = searchResults;
    }

    @Override
    public void initialize() {

    }

    /**
     * if we were to write the procedure we want to test in our grammar form
     * <p>
     * marketNetwork := (for-each network-builder epoch)
     * <p>
     * epoch := (slice (change-point-analysis (select "average"
     * (statistical-analysis (for-each fetch-quotes-for
     * (for-each find-entities-of (search-results)))))
     * <p>
     * // these are then the results which we want to see when done.
     * (for-each entropy-analysis (for-each forward-propagation marketNetwork)
     */
    @Override
    public void onMessage(Message message) {


        Object mesasgeObject = message.getMessageObject();

        if (mesasgeObject instanceof DistributedSearchService.SearchRequest) {

        }
    }

    private boolean isQuoteRetrievalComplete(Message message) {

        return false;
    }

    public void runSimulations() {

        // first start with downloading the data and making sure you have them up to date
        retrieveQuotesUUIDs = new HashSet<>();
        ForEachProcedure retrieveEach = new ForEachProcedure();
        for (SearchResult result : searchResults) {
            StockQuoteRetriever retriever = ProcedureLibrary.stockQuoteRetriverTemplate.createProcedure();
            retrieveQuotesUUIDs.add(retriever.getUuid());
            procedureRunner.submitProcedure(retriever);
        }
        // this will sort the values and sort them, while calculating their averages as well- which is what we actually need.
        SortingPercentilesProcedure sorter = ProcedureLibrary.sortingPercentilesTemplate.createProcedure();
        retrieveEach.getProcedureInputs().getNamedInput("whatever").setValue(new ValueNode("Values", searchResults));
        procedureRunner.submitProcedure(sorter);


        ChangePointAnalysis changePoint = ProcedureLibrary.changePointAnalysisTemplate.createProcedure();
//        SelectionProcedure selectChanges = new SelectionProcedure(
//                sorter.getProcedureResults().getNamedResult(AVERAGE_TIME_SEQUENCE),
//                changePoint.getProcedureInputs().getNamedInput(INPUT_TIME_SEQUENCE));

        SliceProcedure slicer = new SliceProcedure();
//        SelectionProcedure selectChangePoints = new SelectionProcedure(
//                changePoint.getProcedureResults().getNamedResult(CHANGE_POINTS),
//                slicer.getProcedureInputs().getNamedInput("INTERVALS"));

        MarketNetworkBuilder builderProc = ProcedureLibrary.marketNetworkBuilderTemplate.createProcedure();

        ForEachProcedure forEach = new ForEachProcedure();
        forEach.addChild(slicer);
        forEach.addChild(builderProc);

        //assertNotNull("You have to create a procedure ", builderProc);

        procedureRunner.submitProcedure(forEach);
    }
}
