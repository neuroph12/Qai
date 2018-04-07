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
import qube.qai.persistence.StockEntity;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.SpawningProcedure;
import qube.qai.procedure.analysis.ChangePointAnalysis;
import qube.qai.procedure.finance.AverageSequence;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.QaiInjectorService;

import javax.inject.Inject;
import java.util.*;

public class FinanceNetworkBuilder extends Procedure implements SpawningProcedure {

    public static final String NAME = "Finance-Network Builder";

    public static final String DESCRIPTION = "This creates finance networks out of given set of finance-entities";

    private ChangePointAnalysis changePoint;

    private AverageSequence averager;

    private Set<FinanceNetworkTrainer> spawn;

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    /**
     * this class does the necessary work of running the averaging
     * procedure, run the change-point analysis on the average-sequence
     * and spawn a Network-Builder for each of the intervals
     * found during the change-point analysis.
     */
    public FinanceNetworkBuilder() {
        this.spawn = new HashSet<>();
    }

    @Override
    public void execute() {

        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalStateException("Procedure has not been configured right- no data to work on, terminating!");
        }

        averager = new AverageSequence();
        QaiInjectorService.getInstance().injectMembers(averager);

        averager.setInputs(inputs);
        averager.execute();

        Map<String, TimeSequence> timeSequenceMap = averager.getSequenceMap();
        Set<Date> alldates = averager.getAllDates();

        StockEntity averageEntity = averager.getChildEntity();
        changePoint = new ChangePointAnalysis();
        changePoint.addInputs(new DataProvider<>(averageEntity));
        QaiInjectorService.getInstance().injectMembers(changePoint);

        changePoint.execute();
        Date start = alldates.iterator().next();
        Collection<ChangePointAnalysis.ChangePointMarker> markers = changePoint.getMarkers();

        if (markers == null || markers.isEmpty()) {
            FinanceNetworkTrainer trainer = new FinanceNetworkTrainer();
            trainer.setParent(this);
            trainer.setStartDate(start);
            trainer.setEndDate(null);
            trainer.setTimeSequenceMap(timeSequenceMap);

            spawn.add(trainer);
            procedureRunner.submitProcedure(trainer);

        } else {

            for (ChangePointAnalysis.ChangePointMarker marker : markers) {

                FinanceNetworkTrainer trainer = new FinanceNetworkTrainer();
                trainer.setParent(this);
                trainer.setStartDate(start);
                trainer.setEndDate(marker.getDate());
                trainer.setTimeSequenceMap(timeSequenceMap);

                spawn.add(trainer);
                procedureRunner.submitProcedure(trainer);

                start = marker.getDate();
            }
        }

    }

    @Override
    public Procedure createInstance() {
        return new FinanceNetworkBuilder();
    }

    @Override
    protected void buildArguments() {

    }

    @Override
    public boolean haveChildrenExceuted() {
        boolean isAllExec = true;

        IMap<String, Procedure> procedureMap = hazelcastInstance.getMap(QaiConstants.PROCEDURES);
        for (Procedure procedure : spawn) {
            Procedure child = procedureMap.get(procedure.getUuid());
            isAllExec &= child.hasExecuted();
        }

        return isAllExec;
    }

    @Override
    public Set<String> getSpawnedProcedureUUIDs() {

        Set<String> uuids = new TreeSet<>();

        for (Procedure procedure : spawn) {
            uuids.add(procedure.getUuid());
        }

        return uuids;
    }

    public Set<FinanceNetworkTrainer> getSpawn() {
        return spawn;
    }

    public void setSpawn(Set<FinanceNetworkTrainer> spawn) {
        this.spawn = spawn;
    }
}
