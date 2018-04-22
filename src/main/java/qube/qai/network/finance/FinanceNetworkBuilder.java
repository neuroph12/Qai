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

import qube.qai.data.TimeSequence;
import qube.qai.persistence.DataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.ChangePoints;
import qube.qai.procedure.finance.AverageSequence;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.QaiInjectorService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class FinanceNetworkBuilder extends Procedure {

    public String NAME = "Finance-Network Builder";

    public String DESCRIPTION = "This creates finance networks out of given set of finance-entities";

    private ChangePoints changePoint;

    private AverageSequence averager;

    private FinanceNetworkTrainer[] spawn;

    private boolean runChildren = true;

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    /**
     * this class does the necessary work of running the averaging
     * procedure, run the change-point analysis on the average-sequence
     * and spawn a Network-Builder for each of the intervals
     * found during the change-point analysis.
     */
    public FinanceNetworkBuilder() {
        super("Finance-Network Builder");
        this.spawn = new FinanceNetworkTrainer[0];
    }

    @Override
    public void execute() {

        if (getInputs() == null || getInputs().isEmpty()) {
            throw new IllegalStateException("Procedure has not been configured right- no data to work on, terminating!");
        }

        averager = new AverageSequence();
        QaiInjectorService.getInstance().injectMembers(averager);

        averager.addInputs(inputs);
        averager.execute();

        TimeSequence[] timeSequences = averager.getSequences();
        Date[] alldates = averager.getAllDates();

        StockEntity averageEntity = averager.getChildEntity();
        changePoint = new ChangePoints();
        changePoint.addInputs(new DataProvider<>(averageEntity));
        QaiInjectorService.getInstance().injectMembers(changePoint);

        changePoint.execute();
        Date start = alldates[0];
        Date end = alldates[alldates.length - 1];

        Collection<ChangePoints.ChangePointMarker> markers = Arrays.asList(changePoint.getMarkers());

        if (markers == null || markers.isEmpty()) {
            FinanceNetworkTrainer trainer = new FinanceNetworkTrainer();
            trainer.setParent(this);
            trainer.setStartDate(start);
            trainer.setAllDates(alldates);
            trainer.setEndDate(end);
            trainer.setSequences(timeSequences);

            if (runChildren) {
                QaiInjectorService.getInstance().injectMembers(trainer);
                trainer.execute();
            } else {
                procedureRunner.submitProcedure(trainer);
            }

            addSpawn(trainer);

        } else {

            for (ChangePoints.ChangePointMarker marker : markers) {

                FinanceNetworkTrainer trainer = new FinanceNetworkTrainer();
                trainer.setParent(this);
                trainer.setStartDate(start);
                trainer.setEndDate(marker.getDate());
                trainer.setAllDates(alldates);
                trainer.setSequences(timeSequences);

                if (runChildren) {
                    QaiInjectorService.getInstance().injectMembers(trainer);
                    trainer.execute();
                } else {
                    procedureRunner.submitProcedure(trainer);
                }

                addSpawn(trainer);

                start = marker.getDate();
            }
        }

        hasExecuted = true;

    }

    private void addSpawn(FinanceNetworkTrainer trainer) {

        Collection<FinanceNetworkTrainer> tempSpawn = new ArrayList<>();
        for (FinanceNetworkTrainer tr : spawn) {
            tempSpawn.add(tr);
        }
        tempSpawn.add(trainer);

        spawn = new FinanceNetworkTrainer[tempSpawn.size()];
        tempSpawn.toArray(spawn);
    }

    @Override
    public Procedure createInstance() {
        return new FinanceNetworkBuilder();
    }

    @Override
    protected void buildArguments() {

    }

    /*@Override
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
    public ArrayList<String> getSpawnedProcedureUUIDs() {

        ArrayList<String> uuids = new ArrayList<>();

        for (Procedure procedure : spawn) {
            uuids.add(procedure.getUuid());
        }

        return uuids;
    }*/

    public FinanceNetworkTrainer[] getSpawn() {
        return spawn;
    }

    /*public void setSpawn(Set<FinanceNetworkTrainer> spawn) {
        this.spawn = spawn;
    }*/

    public ChangePoints getChangePoint() {
        return changePoint;
    }

    public void setChangePoint(ChangePoints changePoint) {
        this.changePoint = changePoint;
    }

    public AverageSequence getAverager() {
        return averager;
    }

    public void setAverager(AverageSequence averager) {
        this.averager = averager;
    }

    public boolean isRunChildren() {
        return runChildren;
    }

    public void setRunChildren(boolean runChildren) {
        this.runChildren = runChildren;
    }
}
