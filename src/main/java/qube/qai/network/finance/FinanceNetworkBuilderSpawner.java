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

package qube.qai.network.finance;

import com.hazelcast.core.IMap;
import qube.qai.data.TimeSequence;
import qube.qai.main.QaiConstants;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.SpawningProcedure;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.finance.SequenceCollectionAverager;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.QaiInjectorService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class FinanceNetworkBuilderSpawner extends Procedure implements SpawningProcedure {

    public static final String NAME = "Finance-Network Spawner";

    public static final String DESCRIPTION = "This creates finance networks out of given set of finance-entities";

    private QaiDataProvider<Collection> entityProvider;

    private ChangePointAnalysis changePoint;

    private SequenceCollectionAverager averager;

    private Set<String> spawnedProcedureUUIDS;

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    /**
     * this class does the necessary work of running the averaging
     * procedure, run the change-point analysis on the average-sequence
     * and spawn a Network-Builder for each of the intervals
     * found during the change-point analysis.
     */
    public FinanceNetworkBuilderSpawner() {
    }

    @Override
    public void execute() {

        if (entityProvider == null || entityProvider.getData() == null) {
            throw new IllegalStateException("Procedure has not been configured right- no data to work on, terminating!");
        }

        averager = new SequenceCollectionAverager();
        QaiInjectorService.getInstance().injectMembers(averager);
//        ForEach forEach = new ForEach();
//        forEach.setTargetCollectionProvider(entityProvider);
//        averager.setSelect(forEach);

        averager.execute();

        Map<String, TimeSequence> timeSequenceMap = averager.getSequenceMap();
        Set<Date> alldates = averager.getAllDates();

        StockEntity averageEntity = averager.getChildEntity();
        changePoint = new ChangePointAnalysis();
        changePoint.setEntityProvider(new DataProvider<>(averageEntity));
        QaiInjectorService.getInstance().injectMembers(changePoint);

        changePoint.execute();
        Collection<ChangePointAnalysis.ChangePointMarker> markers = changePoint.getMarkers();

        Date start = alldates.iterator().next();

        for (ChangePointAnalysis.ChangePointMarker marker : markers) {

            FinanceNetworkBuilder builder = new FinanceNetworkBuilder();
            builder.setParent(this);
            builder.setStartDate(start);
            builder.setEndDate(marker.getDate());
            builder.setTimeSequenceMap(timeSequenceMap);

            spawnedProcedureUUIDS.add(builder.getUuid());

            procedureRunner.submitProcedure(builder);

            start = marker.getDate();
        }

    }

    @Override
    public Procedure createInstance() {
        return new FinanceNetworkBuilderSpawner();
    }

    @Override
    protected void buildArguments() {

    }

    @Override
    public boolean haveChildrenExceuted() {
        boolean isAllExec = true;

        IMap<String, Procedure> procedureMap = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
        for (String uuid : spawnedProcedureUUIDS) {
            Procedure child = procedureMap.get(uuid);
            isAllExec = child.hasExecuted();
        }

        return isAllExec;
    }

    @Override
    public Set<String> getSpawnedProcedureUUIDs() {
        return spawnedProcedureUUIDS;
    }
}
