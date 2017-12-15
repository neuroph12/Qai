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

package qube.qai.services.sims;

import com.hazelcast.core.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.analysis.MarketNetworkBuilder;
import qube.qai.procedure.analysis.SortingPercentilesProcedure;
import qube.qai.procedure.finance.StockQuoteRetriever;
import qube.qai.procedure.utils.ForEachProcedure;
import qube.qai.procedure.utils.SliceProcedure;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.SimulationService;
import qube.qai.services.implementation.SearchResult;
import qube.qai.services.implementation.UUIDService;

import javax.inject.Inject;
import java.util.*;

import static qube.qai.procedure.ProcedureConstants.FROM;

public class MarketBuilderSim implements SimulationService, QaiConstants {

    private Logger logger = LoggerFactory.getLogger("MarketBuilderSim");

    private String NAME = "Market-Building & Stock-Market Simulation: [%s]";

    private String uuid;

    private String procedureTopicName = "Market-Builder Simulation";

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    private Collection<SearchResult> searchResults;

    private Set<String> retrieveQuotesUUIDs;

    public MarketBuilderSim(Collection<SearchResult> searchResults) {
        super();
        this.searchResults = searchResults;
        if (StringUtils.isEmpty(getUuid())) {
            NAME = String.format(NAME, UUIDService.uuidString());
        } else {
            NAME = String.format(NAME, getUuid());
        }
        procedureTopicName = NAME;
        this.searchResults = searchResults;
    }



    private boolean isQuoteRetrievalComplete(Message message) {

        return false;
    }

    @Override
    public void runSimulation() {

        // first start with downloading the data and making sure you have them up to date
        retrieveQuotesUUIDs = new HashSet<>();
        ForEachProcedure retrieveEach = new ForEachProcedure();
        for (SearchResult result : searchResults) {
            StockQuoteRetriever retriever = ProcedureLibrary.stockQuoteRetriverTemplate.createProcedure();
            retrieveQuotesUUIDs.add(retriever.getUuid());
            procedureRunner.submitProcedure(retriever);
        }

        Map map = new HashMap();
        // this will sort the values and sort them, while calculating their averages as well- which is what we actually need.
        SortingPercentilesProcedure sorter = ProcedureLibrary.sortingPercentilesTemplate.createProcedure();
        sorter.getProcedureInputs().getNamedInput(FROM).setValue(map);
        //retrieveEach.getProcedureInputs().getNamedInput("whatever").setValue(new ValueNode("Values", searchResults));
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

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProcedureTopicName() {
        return procedureTopicName;
    }

    public void setProcedureTopicName(String procedureTopicName) {
        this.procedureTopicName = procedureTopicName;
    }
}
