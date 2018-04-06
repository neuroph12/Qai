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
import qube.qai.main.QaiConstants;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.utils.ForEach;
import qube.qai.procedure.utils.SelectForEach;
import qube.qai.procedure.utils.SliceIntervals;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.SimulationService;
import qube.qai.services.implementation.SearchResult;
import qube.qai.services.implementation.UUIDService;

import javax.inject.Inject;
import java.util.*;

public class MarketBuilderSim implements SimulationService, QaiConstants {

    @Inject
    private Logger logger;

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
        ForEach retrieveEach = new ForEach();
        /*for (SearchResult result : searchResults) {
            //StockQuoteUpdater retriever = ProcedureLibrary.stockQuoteUpdaterTemplate.createProcedure();
            retrieveQuotesUUIDs.add(retriever.getUuid());
            procedureRunner.submitProcedure(retriever);
        }*/

        Map map = new HashMap();
        // this will sort the values and sort them, while calculating their averages as well- which is what we actually need.
        // @TODO get this right this works differenty now
//        SortPercentiles sorter = ProcedureLibrary.sortingPercentilesTemplate.createProcedure();
//        sorter.getProcedureInputs().getNamedInput(FROM).setValue(map);
//        //retrieveEach.getProcedureInputs().getNamedInput("whatever").setValue(new ValueNode("Values", searchResults));
//        procedureRunner.submitProcedure(sorter);


        SelectForEach changePoint = ProcedureLibrary.changePointAnalysisTemplate.createProcedure();
//        SelectForAll selectChanges = new SelectForAll(
//                sorter.getProcedureResults().getNamedResult(AVERAGE_TIME_SEQUENCE),
//                changePoint.getProcedureInputs().getNamedInput(INPUT_TIME_SEQUENCE));

        SliceIntervals slicer = new SliceIntervals();
//        SelectForAll selectChangePoints = new SelectForAll(
//                changePoint.getProcedureResults().getNamedResult(CHANGE_POINTS),
//                slicer.getProcedureInputs().getNamedInput("INTERVALS"));

        //FinanceNetworkTrainer builderProc = ProcedureLibrary.financeNetworkBuilderTemplate.createProcedure();

        ForEach forEach = new ForEach();
        //forEach.addChild(slicer);
        //forEach.addChild(builderProc);

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
