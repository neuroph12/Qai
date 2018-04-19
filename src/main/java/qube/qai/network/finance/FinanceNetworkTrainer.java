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
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.network.neural.trainer.BasicNetworkTrainer;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureConstants;

import java.util.Date;

/**
 * Created by rainbird on 12/25/15.
 */
public class FinanceNetworkTrainer extends Procedure implements NetworkBuilder, ProcedureConstants {

    public String NAME = "Finance-Network Trainer";

    public String DESCRIPTION = "Creates and trains a neural-network out of the quotes of the stocks given";

    private TimeSequence[] sequences;

    private Date startDate;

    private Date endDate;

    private Date[] allDates;

    private BasicNetworkTrainer trainer;

    private NeuralNetwork network;

    public FinanceNetworkTrainer() {
        super("Finance-Network Builder");
    }

    @Override
    public void execute() {

        if (sequences == null
                || startDate == null
                || allDates == null) {
            throw new IllegalStateException("Procedure has not been initialized right- stopping execution!");
        }

        // well, here goes nothing
        network = new NeuralNetwork(sequences.length);
        trainer = new BasicNetworkTrainer(network);
        trainer.createTrainingSet(startDate, endDate, allDates, sequences);
        trainer.trainNetwork();

        info("Market-network builder ended with: " + TRAINED_NEURAL_NETWORK);
    }

    @Override
    public Procedure createInstance() {
        return new FinanceNetworkTrainer();
    }

    @Override
    public void buildArguments() {

    }

    public TimeSequence[] getSequences() {
        return sequences;
    }

    public void setSequences(TimeSequence[] sequences) {
        this.sequences = sequences;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public NeuralNetwork getNetwork() {
        return network;
    }

    public void setNetwork(NeuralNetwork network) {
        this.network = network;
    }

    @Override
    public Network buildNetwork(QaiDataProvider... input) {

        if (input == null || input.length == 0) {
            info("No inputs given- terminating execution");
            return null;
        }

        addInputs(input);
        execute();

        return network;
    }

    public BasicNetworkTrainer getTrainer() {
        return trainer;
    }

    public Date[] getAllDates() {
        return allDates;
    }

    public void setAllDates(Date[] allDates) {
        this.allDates = allDates;
    }

}
